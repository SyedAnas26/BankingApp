package services;

import connectors.DbConnector;

import models.UpiTransfer;
import models.enums.UserType;
import org.apache.commons.lang3.StringUtils;
import services.beans.UpiTransferBean;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.ArrayList;

@Secured
public class UpiTransferService {

    String userType;
    int userId;

    public UpiTransferService(String userType, int userId) {
        this.userId= userId;
        this.userType = userType;
    }


    @Path("/list")
    @GET
    public Response getUpiTransferList(@BeanParam UpiTransferBean upiTransferBean)  throws Exception {

        ArrayList<UpiTransfer> upiTransfers = new ArrayList<>();

        String query =  upiTransferBean.getFilteredQuery(userType,userId);

        DbConnector.get(query, rs -> {
            while (rs.next()) {
                UpiTransfer upiTransfer = new UpiTransfer();
                upiTransfer.setValuesFromResultSet(rs);
                upiTransfers.add(upiTransfer);
            }
            return null;
        });

        return Response.ok().entity(upiTransfers).build();
    }

    @Path("/{id}")
    @GET
    public Response getUpiId(@PathParam("id") int id) throws Exception {

        String query = "select * from upi_transfer where id=" + id + ";";

        UpiTransfer upiTransfer = (UpiTransfer) DbConnector.get(query, rs -> {
            UpiTransfer newTransfer = new UpiTransfer();
            if (rs.next()) {
                newTransfer.setValuesFromResultSet(rs);
            }
            return newTransfer;
        });

        if (upiTransfer.getId() == 0){
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"not found\"}").build();
        }

        if(UserType.getTypeByPath(userType) == UserType.CUSTOMER && !verifyAccount(upiTransfer.getTransactionId())){
            return Response.status(404).entity("{\"status\":\"failed\", \"reason\":\"Not accessible\"}").build();
        }

        return Response.ok().entity(upiTransfer).build();
    }



    @POST
    public Response createUpiId(UpiTransfer newTransfer) throws Exception {

        if(!verifyAccount(newTransfer.getTransactionId()))
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"failed\", \"reason\":\"Invalid Transaction ID\"}").build();

        String insertQuery = "insert into upi_transfer(transaction_id,upi_id) values ('"+
                newTransfer.getTransactionId() + "','" +
                newTransfer.getUpiId()+  "')";

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
    public Response updateUpiTransfer(UpiTransfer upiTransfer) {


        ArrayList<String> strings = new ArrayList<>();

        if(upiTransfer.getUpiId() != null)
            strings.add("upi_id='"+upiTransfer.getUpiId()+"'");

        if(strings.size() == 0 || upiTransfer.getId() == 0)
            return Response.status(Response.Status.PARTIAL_CONTENT).entity("{\"status\":\"failed\", \"reason\":\"Not enough values\"}").build();

        String updateQuery = "update upi_transfer set " + StringUtils.join(strings, ",") + " where id="+upiTransfer.getId() +";";

        try {
            DbConnector.update(updateQuery);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.ok().entity(upiTransfer).build();

    }

}

