package models.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;

@XmlEnum
public enum PaymentMode {
    @XmlEnumValue(value = "1")
    UPI(1),
    @XmlEnumValue(value = "2")
    BANK_TRANSFER(2),
    @XmlEnumValue(value = "3")
    ATM(3);

    private final int id;
    PaymentMode(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static PaymentMode getModeById(int id){
        return Arrays.stream(PaymentMode.values()).filter(paymentMode -> paymentMode.id == id).findFirst().orElse(null);
    }
}
