package services.beans;

import models.enums.UserType;

import javax.ws.rs.QueryParam;
import java.util.Date;

public class AccountFilterBean {
    @QueryParam("startDate") Date startDate;
    @QueryParam("endDate") Date endDate;
    @QueryParam("accountType") int accountType;

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

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }


    public String getFilteredQuery(String userType, int userId) {

        StringBuilder query = new StringBuilder();

        if (this.startDate != null && this.endDate != null){
            query.append("select * from account where created_date >='").append(this.startDate).append("' and created_date <'").append(this.endDate).append("'");
        }

        if(this.accountType > 0){
            if(query.length() == 0)
                query.append("select * from account where account_type='").append(this.accountType).append("'");
            else
                query.append("and account_type='").append(this.accountType).append("'");
        }

        if(UserType.getTypeByPath(userType) == UserType.CUSTOMER){
            if(query.length() == 0)
                query.append("select * from account where user_id='").append(userId).append("'");
            else
                query.append("and user_id='").append(userId).append("'");
        }else if(query.length() == 0) {
            query.append("select * from account");
        }

        return query.toString();
    }
}
