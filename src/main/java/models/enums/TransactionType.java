package models.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;

@XmlEnum
public enum TransactionType {
    @XmlEnumValue(value = "1")
    CREDIT(1),

    @XmlEnumValue(value = "2")
    DEBIT(2);

    private final int id;
    TransactionType(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static TransactionType getTypeById(int id){
        return Arrays.stream(TransactionType.values()).filter(transactionType -> transactionType.id == id).findFirst().orElse(null);
    }
}
