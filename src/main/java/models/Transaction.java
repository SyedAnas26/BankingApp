package models;

import models.enums.PaymentMode;
import models.enums.TransactionStatus;
import models.enums.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Transaction {
    private int id;
    private int accountId;

    private int accountNumber;
    private Date createdDate;
    private PaymentMode modeOfPayment;
    private TransactionType transactionType;
    private float amount;
    private TransactionStatus transactionStatus;

    private int toAccountId;

    private int toAccountNumber;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public PaymentMode getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(PaymentMode modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(int toAccountId) {
        this.toAccountId = toAccountId;
    }

    public int getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(int toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public void setValuesFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.transactionStatus = TransactionStatus.getStatusById(rs.getInt("transaction_status"));
        this.accountId = rs.getInt("account_id");
        this.amount = rs.getFloat("amount");
        this.createdDate= rs.getDate("created_date");
        this.modeOfPayment= PaymentMode.getModeById(rs.getInt("mode_of_payment"));
        this.transactionType= TransactionType.getTypeById(rs.getInt("transaction_type"));
        this.accountNumber = rs.getInt("account_no");
        this.toAccountId = rs.getInt("to_account_id");
        this.toAccountNumber = rs.getInt("to_account_no");
    }
}
