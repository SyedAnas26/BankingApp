package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankTransfer {
    private int id;
    private int transactionId;
    private int accountNo;
    private String holderName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setValuesFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.holderName = rs.getString("holder_name");
        this.transactionId = rs.getInt("transaction_id");
        this.accountNo = rs.getInt("account_no");
    }
}
