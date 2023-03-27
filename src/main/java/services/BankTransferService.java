package services;

import connectors.DbConnector;

import models.BankTransfer;
import models.enums.UserType;
import org.apache.commons.lang3.StringUtils;
import services.beans.BankTransferBean;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.ArrayList;


@Secured
public class BankTransferService {


    String userType;
    int userId;

    public BankTransferService(String userType, int userId) {
        this.userId= userId;
        this.userType = userType;
    }

    @Path("/list")
    @GET
    public Response getBankTransferList(@BeanParam BankTransferBean bankTransferBean)  throws Exception {

        ArrayList<BankTransfer> bankTransfers = new ArrayList<>();

        String query =  bankTransferBean.getFilteredQuery(userType,userId);

        DbConnector.get(query, rs -> {
            while (rs.next()) {
                BankTransfer bankTransfer = new BankTransfer();
                bankTransfer.setValuesFromResultSet(rs);
                bankTransfers.add(bankTransfer);
            }
            return null;
        });

        return Response.ok().entity(bankTransfers).build();
    }


    @Path("/{id}")
    @GET
    public Response getBankTransfer(@PathParam("id") int id) throws Exception {

        String query = "select * from bank_transfer where id=" + id + ";";

        BankTransfer bankTransfer = (BankTransfer) DbConnector.get(query, rs -> {
            BankTransfer newTransfer = new BankTransfer();
            if (rs.next()) {
                newTransfer.setValuesFromResultSet(rs);
            }
            return newTransfer;
        });

        if (bankTransfer.getId() == 0){
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"not found\"}").build();

        }

        if(UserType.getTypeByPath(userType) == UserType.CUSTOMER && !verifyAccount(bankTransfer.getTransactionId())){
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"Not accessible\"}").build();
        }

        return Response.ok().entity(bankTransfer).build();
    }



    @POST
    public Response createBankTransfer(BankTransfer newTransfer) throws Exception {

        if(!verifyAccount(newTransfer.getTransactionId()))
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"failed\", \"reason\":\"Invalid Transaction ID\"}").build();

        String insertQuery = "insert into bank_transfer(transaction_id,account_no,holder_name) values ('"+
                newTransfer.getTransactionId() + "','" +
                newTransfer.getAccountNo()+ "','"+ newTransfer.getHolderName() + "')";

        try {
            newTransfer.setId(DbConnector.insert(insertQuery));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }

        return Response.ok().entity(newTransfer).build();

    }

    private boolean verifyAccount(int transactionId) throws Exception {
        String accountCheckQuery = "select 1 from transaction as t inner join account as a on  t.account_id=a.id  and t.id = " + transactionId + " and a.user_id="+userId+";";
        return (Boolean) DbConnector.get(accountCheckQuery, ResultSet::next);
    }


    @PUT
    public Response updateBankTransfer(BankTransfer bankTransfer) {

        ArrayList<String> strings = new ArrayList<>();

        if(bankTransfer.getHolderName() != null)
            strings.add("holder_name='"+bankTransfer.getHolderName()+"'");
        if(bankTransfer.getAccountNo() > 0)
            strings.add("account_no="+bankTransfer.getAccountNo());

        if(strings.size() == 0 || bankTransfer.getId() == 0)
            return Response.status(Response.Status.PARTIAL_CONTENT).entity("{\"status\":\"failed\", \"reason\":\"Not enough values\"}").build();

        String updateQuery = "update bank_transfer set " + StringUtils.join(strings, ",") + " where id="+bankTransfer.getId() +";";

        try {
            DbConnector.update(updateQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().entity(bankTransfer).build();

    }

}

