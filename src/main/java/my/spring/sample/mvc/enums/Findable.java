package my.spring.sample.mvc.enums;

public interface Findable {

    public Object getValue();
    public String getDesc();

    public static <T extends Enum<T> & Findable> T getValueOf(Class<T> enumClass, Object value) {
        if(value == null)
            return null;

        for (T e : enumClass.getEnumConstants()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }

        throw new RuntimeException("No enum const " + enumClass.getName() + " for value \'" + value
                + "\'");
    }
}
