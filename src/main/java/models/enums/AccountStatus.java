package models.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;
@XmlEnum
public enum AccountStatus {
    @XmlEnumValue(value = "1")
    ACTIVE(1),
    @XmlEnumValue(value = "0")
    INACTIVE(0);

    private final int id;
    AccountStatus(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static AccountStatus getTypeById(int id){
        return Arrays.stream(AccountStatus.values()).filter(accountStatus -> accountStatus.id == id).findFirst().orElse(null);
    }
}

