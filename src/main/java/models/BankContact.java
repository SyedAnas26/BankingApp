package models;

import models.enums.AccountStatus;
import models.enums.AccountType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankContact {
    private int id;
    private int accountId;
    private String nickName;
    private int userId;

    private int accountNumber;

    private String holderName;

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setValuesFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("bank_contact_id");
        this.nickName =rs.getString("nick_name");
        this.userId = rs.getInt("user_id");
        this.accountId = rs.getInt("account_id");
        this.holderName = rs.getString("name");
        this.accountNumber = rs.getInt("account_no");
    }
}
