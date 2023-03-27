package services.beans;

import models.enums.UserType;

import javax.ws.rs.QueryParam;
import java.util.Date;

public class UpiTransferBean {

    @QueryParam("startDate") Date startDate;
    @QueryParam("endDate") Date endDate;
    @QueryParam("transactionStatus") int transactionStatus;
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

    public String getFilteredQuery(String userType, int userId) {
        StringBuilder query = new StringBuilder();

        if(UserType.getTypeByPath(userType) == UserType.CUSTOMER)
            query.append("select * from upi_transfer as bt inner join transaction as t on t.id=bt.transaction_id inner join account as a on a.id = t.account_id and a.user_id=").append(userId);
        else
            query.append("select * from upi_transfer");

        if (this.startDate != null && this.endDate != null){
            if(UserType.getTypeByPath(userType) == UserType.CUSTOMER)
                query.append("and t.created_date >='").append(this.startDate).append("' and t.created_date <'").append(this.endDate).append("'");
            else
                query.append("as bt inner join transaction as t on t.id=bt.transaction_id and t.created_date >='").append(this.startDate).append("' and t.created_date <'").append(this.endDate).append("'");
        }

        if(this.transactionStatus > 0)
            query.append("and t.transaction_status='").append(this.transactionStatus).append("'");

        if(this.upiId != null)
            query.append("and bt.upiId='").append(this.upiId).append("'");

        return query.toString();
    }
}
