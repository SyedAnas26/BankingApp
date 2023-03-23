package models;

public enum AccountType {
    SALARY(1),
    SAVINGS(2);

    private final int val;
    AccountType(int val){
        this.val = val;
    }
    public int getVal() {
        return val;
    }
}

