package models;

import models.enums.AccountType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class Account {

    private int id;
    private int userId;
    public int accountNo;
    private float balance;
    private Date creationDate;
    private AccountType accountType;

    private String upiId;

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }


    public void setValuesFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.balance =rs.getFloat("balance");
        this.accountType = AccountType.getTypeById(rs.getInt("account_type"));
        this.userId = rs.getInt("user_id");
        this.creationDate = rs.getDate("created_date");
        this.accountNo = rs.getInt("account_no");
        this.upiId = rs.getString("upi_id");
    }
}
