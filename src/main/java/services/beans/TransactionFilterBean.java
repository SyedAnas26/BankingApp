package services.beans;

import models.enums.UserType;

import javax.management.Query;
import javax.ws.rs.QueryParam;
import java.util.Date;

public class TransactionFilterBean {
    @QueryParam("startDate")
    Date startDate;
    @QueryParam("endDate")
    Date endDate;
    @QueryParam("transactionType")
    int transactionType;
    @QueryParam("paymentMode")
    int paymentMode;
    @QueryParam("transactionStatus")
    int transactionStatus;
    @QueryParam("accountId")
    int accountId;

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

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public int getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getFilteredQuery(String userType, int userId) {

        StringBuilder query = new StringBuilder();

        if (UserType.getTypeByPath(userType) == UserType.CUSTOMER)
            query.append("SELECT t.*, a1.account_no AS account_no, a2.account_no AS to_account_no\n" +
                    "FROM transaction t \n" +
                    "INNER JOIN account a1 ON t.account_id = a1.id \n" +
                    "INNER JOIN account a2 ON t.to_account_id = a2.id where a1.user_id=").append(userId);
        else
            query.append("SELECT t.*, a1.account_no AS account_no, a2.account_no AS to_account_no\n" +
                    "FROM transaction t \n" +
                    "INNER JOIN account a1 ON t.account_id = a1.id\n" +
                    "INNER JOIN account a2 ON t.to_account_id = a2.id");

        if (this.startDate != null && this.endDate != null) {
            if (UserType.getTypeByPath(userType) == UserType.CUSTOMER)
                query.append("and t.created_date >='").append(this.startDate).append("' and t.created_date <'").append(this.endDate).append("'");
            else
                query.append("where t.created_date >='").append(this.startDate).append("' and t.created_date <'").append(this.endDate).append("'");
        }

        if (this.transactionType > 0)
            query.append("and t.transaction_type='").append(this.transactionType).append("'");

        if (this.transactionStatus > 0)
            query.append("and t.transaction_status='").append(this.transactionStatus).append("'");

        if (this.paymentMode > 0)
            query.append("and t.mode_of_payment='").append(this.paymentMode).append("'");


        return query.toString();
    }
}
