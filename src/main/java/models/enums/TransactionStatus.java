package models.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;

@XmlEnum
public enum TransactionStatus {
    @XmlEnumValue(value = "1")
    PROCESSING(1),
    @XmlEnumValue(value = "2")
    SUCCESS(2),
    @XmlEnumValue(value = "3")
    FAILED(3);

    private final int id;
    TransactionStatus(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static TransactionStatus getStatusById(int id){
        return Arrays.stream(TransactionStatus.values()).filter(transactionStatus -> transactionStatus.id == id).findFirst().orElse(null);
    }



}
