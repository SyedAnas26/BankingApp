package services;

import connectors.DbConnector;

import models.Transaction;
import models.enums.TransactionType;
import models.enums.UserType;
import org.apache.commons.lang3.StringUtils;
import services.beans.TransactionFilterBean;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.ArrayList;

@Secured
public class TransactionService {

    String userType;
    int userId;

    public TransactionService(String userType, int userId) {
        this.userId= userId;
        this.userType = userType;
    }

    @Path("/list")
        @GET
        public Response getTransactionList(@BeanParam TransactionFilterBean transactionFilterBean)  throws Exception {

            ArrayList<Transaction> transactions = new ArrayList<>();

            String query =  transactionFilterBean.getFilteredQuery(userType,userId);

            DbConnector.get(query, rs -> {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setValuesFromResultSet(rs);
                    transactions.add(transaction);
                }
                return null;
            });

            return Response.ok().entity(transactions).build();
        }

        @Path("/{id}")
        @GET
        public Response getTransaction(@PathParam("id") int id) throws Exception {

            String query = "select * from transaction where id=" + id + ";";

            Transaction requestedTransaction = (Transaction) DbConnector.get(query, rs -> {
                Transaction transaction = new Transaction();
                if (rs.next()) {
                   transaction.setValuesFromResultSet(rs);
                }
                return transaction;
            });

            if (requestedTransaction.getId() == 0){
                return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"not found\"}").build();

            }

            if(UserType.getTypeByPath(userType) == UserType.CUSTOMER && !verifyAccount(requestedTransaction.getAccountId())){
                return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"Not accessible\"}").build();
            }

            return Response.ok().entity(requestedTransaction).build();
        }



        @POST
        public Response createTransaction(Transaction newTransaction) throws Exception {

            if(!verifyAccount(newTransaction.getAccountId()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"failed\", \"reason\":\"Invalid Account ID\"}").build();

            String insertQuery = "insert into transaction(account_id,mode_of_payment,transaction_type,amount,transaction_status) values ('"+
                    newTransaction.getAccountId() + "','" +
                    newTransaction.getModeOfPayment().getId() + "','"+ newTransaction.getTransactionType().getId() +
                    "','"+ newTransaction.getAmount() + "','"+ newTransaction.getTransactionStatus().getId() +"')";

            try {

                newTransaction.setId(DbConnector.insert(insertQuery));
                float changeInAmount = newTransaction.getAmount();;

                if(newTransaction.getTransactionType() == TransactionType.DEBIT)
                    changeInAmount = -(changeInAmount);

                String getQuery = "select * from account where id="+newTransaction.getAccountId();
                float finalChangeInAmount = changeInAmount;

                DbConnector.get(getQuery, rs->{
                    if(rs.next()){
                        DbConnector.update("update account set balance="+ (rs.getFloat("balance") + finalChangeInAmount)+ "where id="+rs.getInt("id"));
                    }
                    return null;
                });

            } catch (Exception e) {
                e.printStackTrace();
                return Response.serverError().build();
            }

            return Response.ok().entity(newTransaction).build();

        }

    private boolean verifyAccount(int accountId) throws Exception {
            String accountCheckQuery = "select 1 from account where id="+accountId+" and user_id="+userId+";";
            return (Boolean) DbConnector.get(accountCheckQuery, ResultSet::next);
    }

    @PUT
    public Response updateTransaction(Transaction transaction) {

            ArrayList<String> strings = new ArrayList<>();

            if(transaction.getTransactionType() != null)
                strings.add("transaction_type="+transaction.getTransactionType().getId());
            if(transaction.getAmount() > 0)
                strings.add("amount="+transaction.getAmount());
            if (transaction.getModeOfPayment() != null)
                strings.add("mode_of_payment=" + transaction.getModeOfPayment().getId());
            if (transaction.getTransactionStatus() != null)
                strings.add("transaction_status="+ transaction.getTransactionStatus().getId());

            if(strings.size() == 0 || transaction.getId() == 0)
                return Response.status(Response.Status.PARTIAL_CONTENT).entity("{\"status\":\"failed\", \"reason\":\"Not enough values\"}").build();

            String updateQuery = "update transaction set " + StringUtils.join(strings, ",") + " where id="+transaction.getId() +";";

            try {

                if (transaction.getAmount() > 0 || transaction.getTransactionType() != null) {
                    String getTransQuery = "select t.amount,a.balance,t.account_id,t.transaction_type from transaction as t inner join account as a on t.account_id = a.id and t.id="+ transaction.getId();

                    DbConnector.get(getTransQuery, rs -> {
                        if(rs.next()) {
                            float amountDifference = transaction.getAmount() - rs.getFloat("amount");

                            if (transaction.getTransactionType() != null && transaction.getTransactionType().getId() != rs.getInt("transaction_type")) {

                                if(transaction.getAmount() >= rs.getFloat("amount"))
                                    amountDifference = ( transaction.getAmount() * 2 ) - Math.abs(amountDifference);
                                else
                                    amountDifference = ( transaction.getAmount() * 2 ) + Math.abs(amountDifference);

                            }

                            if (transaction.getTransactionType() == TransactionType.DEBIT)
                                amountDifference = -amountDifference;

                            System.out.println(amountDifference);
                            DbConnector.update("update account set balance=" + (rs.getFloat("balance") + amountDifference) + "where id=" + rs.getInt("account_id"));
                        }
                        return null;
                    });
                }

                DbConnector.update(updateQuery);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.serverError().build();
            }
            return Response.ok().entity(transaction).build();

        }

    }

