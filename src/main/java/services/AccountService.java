package services;

import connectors.DbConnector;
import models.Account;
import models.enums.UserType;

import org.apache.commons.lang3.StringUtils;
import services.beans.AccountFilterBean;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.UUID;

@Secured
public class AccountService {


    String userType;
    int userId;

    public AccountService(String userType, int userId) {
        this.userId = userId;
        this.userType = userType;
    }


    @Path("/list")
    @GET
    public Response getAccountList(@BeanParam AccountFilterBean accountFilterBean) throws Exception {

        ArrayList<Account> accounts = new ArrayList<>();

        String query = accountFilterBean.getFilteredQuery(userType, userId);

        DbConnector.get(query, rs -> {
            while (rs.next()) {
                Account account = new Account();
                account.setValuesFromResultSet(rs);
                if (UserType.getTypeByPath(userType) == UserType.CUSTOMER && userId != account.getUserId())
                    continue;

                accounts.add(account);
            }
            return null;
        });

        return Response.ok().entity(accounts).build();
    }

    @Path("/verifyAccount")
    @GET
    public Response verifyAccountDetails(@QueryParam("holderName") String holderName, @QueryParam("toAccountNumber") int toAccountNumber, @QueryParam("upiId") String upiId, @QueryParam("modeOfPayment") int modeOfPayment) throws Exception {
        String accountCheckQuery = "";
        if (modeOfPayment == 1)
            accountCheckQuery = "select a.id from account as a inner join user as u on a.user_id = u.id where a.upi_id ='" + upiId + "'";
        else
            accountCheckQuery = "select a.id from account as a inner join user as u on a.user_id = u.id where a.account_no =" + toAccountNumber + " and concat(u.first_name,' ',u.last_name) ='" + holderName + "';";
        System.out.println(accountCheckQuery);
        int toAccountId = 0;
        try {
            toAccountId = (int) DbConnector.get(accountCheckQuery, rs -> {
                if (rs.next())
                    return rs.getInt("id");
                return 0;
            });
        } catch (Exception e) {
            return Response.ok().entity("{\"status\":\"failed\"}").build();
        }

        if (toAccountId > 0)
            return Response.ok().entity("{\"status\":\"success\",\"toAccountId\":" + toAccountId + "}").build();

        return Response.ok().entity("{\"status\":\"failed\"}").build();
    }


    @Path("/{id}")
    @GET
    public Response getAccount(@PathParam("id") int id) throws Exception {

        String query = "select * from account where id=" + id + ";";

        Account requestedAccount = (Account) DbConnector.get(query, rs -> {
            Account account = new Account();
            if (rs.next()) {
                account.setValuesFromResultSet(rs);
            }
            return account;
        });

        if (requestedAccount.getId() == 0) {
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"not found\"}").build();

        }

        if (UserType.getTypeByPath(userType) == UserType.CUSTOMER && userId != requestedAccount.getUserId()) {
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"Not accessible\"}").build();
        }

        return Response.ok().entity(requestedAccount).build();
    }


    @POST
    public Response createAccount(Account newAccount) {

        newAccount.setUserId(userId);
        newAccount.setAccountNo(generateRandomNumber());
        newAccount.setUpiId(newAccount.getAccountNo() + "@bank.com");

        if (newAccount.getAccountType() == null)
            return Response.status(Response.Status.PARTIAL_CONTENT).entity("{\"status\":\"failed\", \"reason\":\"Not enough values\"}").build();

        String insertQuery = "insert into account(balance,account_type,user_id,account_no,upi_id) values ('" +
                newAccount.getBalance() + "','" +
                newAccount.getAccountType().getId() + "','" + newAccount.getUserId() + "','" + newAccount.getAccountNo() + "','" + newAccount.getUpiId() + "')";
        try {
            newAccount.setId(DbConnector.insert(insertQuery));
        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.status(409).entity("{\"error\":\"Account number already exists\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().entity(newAccount).build();

    }

    @PUT
    public Response updateAccount(Account account) throws Exception {

        ArrayList<String> queryStrings = new ArrayList<>();

        if (account.getAccountType() != null)
            queryStrings.add("account_type=" + account.getAccountType().getId());
        if (account.getBalance() > 0)
            queryStrings.add("balance=" + account.getBalance());

        if (queryStrings.size() == 0 || account.getId() == 0)
            return Response.status(Response.Status.PARTIAL_CONTENT).entity("{\"status\":\"failed\", \"reason\":\"Not enough values\"}").build();

        if (!verifyAccount(account.getId()))
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"failed\", \"reason\":\"Invalid Account ID\"}").build();

        String updateQuery = "update account set " + StringUtils.join(queryStrings, ",") + " where id=" + account.getId() + ";";

        try {
            DbConnector.update(updateQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }

        return Response.ok().entity(account).build();

    }


    private boolean verifyAccount(int accountId) throws Exception {
        String accountCheckQuery = "select 1 from account where id=" + accountId + " and user_id=" + userId + ";";
        return (Boolean) DbConnector.get(accountCheckQuery, ResultSet::next);
    }


    private int generateRandomNumber() {
        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }

}
