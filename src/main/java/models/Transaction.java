package models;

import java.util.Date;

public class Transaction {
    private long id;
    private long accountId;
    private Date createdDate;
    private int modeOfPayment;
    private int transactionType;
    private float amount;
    private int transactionStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(int modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
