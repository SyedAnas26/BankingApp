package services.beans;

import models.enums.UserType;

import javax.ws.rs.QueryParam;
import java.util.Date;
import java.util.Objects;

public class TransactionBean {

    @QueryParam("startDate") Date startDate;
    @QueryParam("endDate") Date endDate;
    @QueryParam("transactionStatus") int transactionStatus;
    @QueryParam("accountNo") int accountNo;
    @QueryParam("upiId") String upiId;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getFilteredQuery(String userType, int userId, String transferTable) {

        StringBuilder query = new StringBuilder();

        if(UserType.getTypeByPath(userType) == UserType.CUSTOMER)
            query.append("select * from "+transferTable+" as bt inner join transaction as t on t.id=bt.transaction_id inner join account as a on a.id = t.account_id and a.user_id=").append(userId);
        else
            query.append("select * from "+transferTable);

        if (this.startDate != null && this.endDate != null){
            if(UserType.getTypeByPath(userType) == UserType.CUSTOMER)
                query.append("and t.created_date >='").append(this.startDate).append("' and t.created_date <'").append(this.endDate).append("'");
            else
                query.append("as bt inner join transaction as t on t.id=bt.transaction_id and t.created_date >='").append(this.startDate).append("' and t.created_date <'").append(this.endDate).append("'");
        }

        if(this.transactionStatus > 0)
            query.append("and t.transaction_status='").append(this.transactionStatus).append("'");

        if(this.accountNo > 0 && Objects.equals(transferTable, "bank_transfer"))
            query.append("and bt.account_no='").append(this.accountNo).append("'");

        if(this.upiId != null && Objects.equals(transferTable, "upi_transfer"))
            query.append("and bt.upiId='").append(this.upiId).append("'");

        return query.toString();
    }
}
