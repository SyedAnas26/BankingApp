package services;

import connectors.DbConnector;
import models.enums.UserType;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Secured
@Path("{userType}/{userId}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MainService {

    @PathParam("userType")
    public String userType;

    @PathParam("userId")
    public int userId;

    @Path("/account")
    public AccountService getAccountService() {
        return new AccountService(userType, userId);
    }

    @Path("/transaction")
    public TransactionService getTransactionService() {
        return new TransactionService(userType, userId);
    }

    @Path("/customerData")
    @GET
    public Response getCustomerData(@QueryParam("userId") String userId) throws Exception {
        if (UserType.getTypeByPath(userType) == UserType.CUSTOMER)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        String query = "SELECT u.first_name,  u.last_name,  u.email,\n" +
                " COUNT(DISTINCT a.id) AS accounts_count,\n" +
                " SUM(a.balance) AS total_balance,\n" +
                " COUNT(DISTINCT t.id) AS transactions_count \n" +
                "FROM   user u\n" +
                "LEFT JOIN account a ON a.user_id = u.id  \n" +
                "LEFT JOIN transaction t ON t.account_id = a.id \n" +
                "where u.is_employee=0 \n" +
                "GROUP BY " +
                "u.id " ;
        JsonArrayBuilder userListBuilder = Json.createArrayBuilder();
        DbConnector.get(query, rs -> {
            while (rs.next()) {
                JsonObjectBuilder jsonObject = Json.createObjectBuilder();
                jsonObject.add("accountsCount", rs.getInt("accounts_count"));
                jsonObject.add("transactionsCount", rs.getInt("transactions_count"));
                jsonObject.add("totalBalance", rs.getInt("total_balance"));
                jsonObject.add("firstName", rs.getString("first_name"));
                jsonObject.add("lastName", rs.getString("last_name"));
                jsonObject.add("email", rs.getString("email"));
                userListBuilder.add(jsonObject);
            }
            return null;
        });
        return Response.ok().entity(userListBuilder.build().toString()).build();

    }

    @Path("/dashboardData")
    @GET
    public Response getDashboardDetails(@QueryParam("userId") String userId) throws Exception {
        String query = null;

        if (UserType.getTypeByPath(userType) == UserType.CUSTOMER) {
            query = "SELECT \n" +
                    "    COUNT(CASE WHEN t.mode_of_payment = 2 THEN 1 END) AS bank_transfer_count,\n" +
                    "    COUNT(CASE WHEN t.mode_of_payment = 1 THEN 1 END) AS upi_transfer_count,\n" +
                    "    COUNT(DISTINCT t.id) AS transaction_count,\n" +
                    "    (SELECT COUNT(a.id) from account a where user_id="+userId+") as account_count," +
                    "    (SELECT GROUP_CONCAT(DISTINCT CONCAT(a.account_no, ':', a.balance) ORDER BY a.account_no SEPARATOR ',') from account a where a.user_id="+ userId +") AS account_list\n" +
                    "FROM \n" +
                    "    transaction t \n" +
                    "    JOIN account a ON t.account_id = a.id" +
                    " where a.user_id = " + userId;

        } else if (UserType.getTypeByPath(userType) == UserType.EMPLOYEE) {
            query = "SELECT \n" +
                    "    COUNT(CASE WHEN t.mode_of_payment = 2 THEN 1 END) AS bank_transfer_count,\n" +
                    "    COUNT(CASE WHEN t.mode_of_payment = 1 THEN 1 END) AS upi_transfer_count,\n" +
                    "    COUNT(DISTINCT t.id) AS transaction_count,\n" +
                    "    (SELECT GROUP_CONCAT(DISTINCT CONCAT(a.account_no, ':', a.balance) ORDER BY a.account_no SEPARATOR ',') FROM account a) AS account_list,\n" +
                    "\t( SELECT COUNT(a.id)  from account a) as account_count\n" +
                    "FROM \n" +
                    "    transaction t ";
        }

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        DbConnector.get(query, rs -> {
            if (rs.next()) {
                jsonBuilder.add("accountsCount", rs.getInt("account_count"));
                jsonBuilder.add("transactionCount", rs.getInt("transaction_count"));
                jsonBuilder.add("bankTransferCount", rs.getInt("bank_transfer_count"));
                jsonBuilder.add("upiTransferCount", rs.getInt("upi_transfer_count"));
                System.out.println(userType);
                if (UserType.getTypeByPath(userType) == UserType.CUSTOMER) {
                    String accountListStr = rs.getString("account_list");
                    JsonArrayBuilder accountListBuilder = Json.createArrayBuilder();
                    for (String accountStr : accountListStr.split(",")) {
                        System.out.println(accountStr);
                        String[] accountArr = accountStr.split(":");
                        JsonObjectBuilder accountBuilder = Json.createObjectBuilder();
                        accountBuilder.add("accountNo", accountArr[0]);
                        accountBuilder.add("balance", accountArr[1]);
                        accountListBuilder.add(accountBuilder);
                    }
                    jsonBuilder.add("accountList", accountListBuilder.build());
                }
            }
            return null;

        });
        System.out.println(jsonBuilder.build());

        return Response.ok().entity(jsonBuilder.build().toString()).build();
    }


}
