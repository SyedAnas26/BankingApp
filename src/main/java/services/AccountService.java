package services;

import authentication.Secured;
import connectors.DbConnector;
import models.Account;
import models.enums.AccountType;
import models.enums.UserType;
import services.beans.AccountFilterBean;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.UUID;

@Secured
@Path("{userType}/{userId}/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {
    @PathParam("userType")
    private String userType;
    @PathParam("userId")
    private int userId;

//    @Path("/list")
    @GET
    public Response getAccountList(@BeanParam AccountFilterBean accountFilterBean)  throws Exception {

        ArrayList<Account> accounts = new ArrayList<>();

        String query =  accountFilterBean.getFilteredQuery(userType,userId);

        DbConnector.get(query, res -> {
            while (res.next()) {
                Account account = new Account();
                account.setId(res.getInt("id"));
                account.setBalance(res.getFloat("balance"));
                account.setAccountType(AccountType.getTypeById(res.getInt("account_type")));
                account.setUserId(res.getInt("user_id"));
                account.setCreationDate(res.getDate("created_date"));
                account.setAccountNo(res.getInt("account_no"));

                if (UserType.getTypeByPath(userType) == UserType.CUSTOMER && userId != account.getUserId())
                    continue;

                accounts.add(account);
            }
            return null;
        });

        return Response.ok().entity(accounts).build();
    }




    @Path("/{id}")
    @GET
    public Response getAccount(@PathParam("id") int id) throws Exception {

        String query = "select * from account where id=" + id + ";";

        System.out.println(UserType.getTypeByPath(userType));
        System.out.println(userId);

        Account requestedAccount = (Account) DbConnector.get(query, res -> {
            Account account = new Account();
            if (res.next()) {
                account.setId(res.getInt("id"));
                account.setAccountNo(res.getInt("account_no"));
                account.setBalance(res.getFloat("balance"));
                account.setAccountType(AccountType.getTypeById(res.getInt("account_type")));
                account.setUserId(res.getInt("user_id"));
                account.setCreationDate(res.getDate("created_date"));
                account.setAccountNo(res.getInt("account_no"));
            }
            return account;
        });

        if (requestedAccount.getId() == 0){
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"not found\"}").build();

        }

        if(UserType.getTypeByPath(userType) == UserType.CUSTOMER && userId != requestedAccount.getUserId()){
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"Not accessible\"}").build();
        }

        return Response.ok().entity(requestedAccount).build();
    }



    @POST
    public Response createAccount(Account newAccount) {

        newAccount.setAccountNo(generateRandomNumber());
        String insertQuery = "insert into account(balance,account_type,user_id,account_no) values ('"+
                newAccount.getBalance() + "','" +
                newAccount.getAccountType().getId() + "','" + newAccount.getUserId() + "','"+ newAccount.getAccountNo() + "')";
        try {
            newAccount.setId(DbConnector.insert(insertQuery));
        } catch (SQLIntegrityConstraintViolationException e){
            return Response.status(409).entity("{\"error\":\"account number already exists\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().entity(newAccount).build();

    }

    @PUT
    public Response updateAccount(Account account) {

        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("update account set ");
        if (account.getAccountType() != null){
            updateQuery.append("account_type=").append(account.getAccountType().getId());
        }

        if(account.getAccountType() != null && account.getBalance() > 0){
            updateQuery.append(",");
        }

        if(account.getBalance() > 0){
            updateQuery.append("balance=").append(account.getBalance());
        }
        updateQuery.append("where id=").append(account.getId()).append(";");

        try {
            DbConnector.update(updateQuery.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().entity(account).build();

    }



    private int generateRandomNumber() {
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }

}
