package services;

import connectors.DbConnector;
import models.BankContact;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

@Secured
public class BankContactService {


    String userType;
    int userId;

    public BankContactService(String userType, int userId) {
        this.userId = userId;
        this.userType = userType;
    }


    @Path("/list")
    @GET
    public Response getContactList(@QueryParam("userId") int queryUserId) throws Exception {

        ArrayList<BankContact> bankContacts = new ArrayList<>();

        String query = "SELECT bc.id AS bank_contact_id,bc.user_id, bc.account_id, bc.nick_name, a.account_no, CONCAT(u.first_name, ' ', u.last_name) AS name\n" +
                "FROM bank_contact bc\n" +
                "JOIN account a ON bc.account_id = a.id\n" +
                "JOIN user u ON bc.user_id = u.id\n" +
                "WHERE bc.user_id ="+ userId + ";";

        System.out.println(query);

        DbConnector.get(query, rs -> {
            while (rs.next()) {
                BankContact contact = new BankContact();
                contact.setValuesFromResultSet(rs);
                bankContacts.add(contact);
            }
            return null;
        });

        return Response.ok().entity(bankContacts).build();
    }


    @POST
    public Response createBankContact(BankContact newBankContact) throws Exception {

        if (newBankContact.getAccountId() == 0 || newBankContact.getUserId() == 0)
            return Response.status(Response.Status.PARTIAL_CONTENT).entity("{\"status\":\"failed\", \"reason\":\"Not enough values\"}").build();
        if(verifyAccount(newBankContact.getAccountId()))
            return Response.status(Response.Status.CONFLICT).entity("{\"status\":\"failed\", \"reason\":\"Same User's Account\"}").build();


        String insertQuery = "insert into bank_contact(account_id,nick_name,user_id) values ('" +
                newBankContact.getAccountId() + "','" +
                newBankContact.getNickName() + "','" + newBankContact.getUserId() + "')";
        try {
            newBankContact.setId(DbConnector.insert(insertQuery));
        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.status(409).entity("{\"status\":\"failed\",\"error\":\"Account number Contact already exists\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().entity(newBankContact).build();

    }


    private boolean verifyAccount(int accountId) throws Exception {
        String accountCheckQuery = "select 1 from account where id=" + accountId + " and user_id=" + userId + ";";
        return (Boolean) DbConnector.get(accountCheckQuery, ResultSet::next);
    }


}
