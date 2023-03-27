package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpiTransfer {
    private int id;
    private String upiId;
    private int transactionId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setValuesFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.upiId = rs.getString("upi_id");
        this.transactionId = rs.getInt("transaction_id");
    }


}
