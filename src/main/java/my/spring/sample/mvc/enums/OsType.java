package my.spring.sample.mvc.enums;

import lombok.Getter;

public enum OsType implements Findable{
    iOS("iOS", "iOS"),
    Android("Android", "Android"),
    Windows("Windows", "Windows"),
    macOS("macOS", "macOS"),
    Linux("Linux", "Linux"),
    Etc("Etc", "Etc")
    ;

    @Getter
    private String value;
    @Getter
    private String desc;

    private OsType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static OsType get(String value) {
        return Findable.getValueOf(OsType.class, value);
    }
}
