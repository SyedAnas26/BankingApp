package models.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Arrays;

@XmlEnum
public enum UserType {
    @XmlEnumValue(value = "customer")
    CUSTOMER("customer"),
    @XmlEnumValue(value = "employee")
    EMPLOYEE("employee");

    private final String path;
    UserType(String path){
        this.path = path;
    }
    public String getPath() {
        return path;
    }
    public static UserType getTypeByPath(String path){
        return Arrays.stream(UserType.values()).filter(userType -> userType.path.equals(path)).findFirst().orElse(null);
    }
}
