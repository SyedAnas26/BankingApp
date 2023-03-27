package services;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public AccountService getAccountService(){
        return new AccountService(userType,userId);
    }

    @Path("/transaction")
    public TransactionService getTransactionService(){
        return new TransactionService(userType, userId);
    }

    @Path("/bank_transfer")
    public BankTransferService getTransferServiceForBank(){
        return new BankTransferService(userType,userId);
    }

    @Path("/upi_transfer")
    public UpiTransferService getTransferServiceForUpi(){
        return new UpiTransferService(userType, userId);
    }


}
