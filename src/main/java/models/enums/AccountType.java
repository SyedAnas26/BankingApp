package models.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;
@XmlEnum
public enum AccountType {
    @XmlEnumValue(value = "1")
    SALARY(1),
    @XmlEnumValue(value = "2")
    SAVINGS(2);

    private final int id;
    AccountType(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static AccountType getTypeById(int id){
        return Arrays.stream(AccountType.values()).filter(accountType -> accountType.id == id).findFirst().orElse(null);
    }
}

