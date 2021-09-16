package my.spring.sample.mvc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CastUtil {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        return (Map<String, Object>)obj;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> toList(Object obj) {
        return (List<Object>)obj;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> toListMap(Object obj) {
        return (List<Map<String, Object>>)obj;
    }

    public static String getStr(Map<String, Object> map, String key) {
        return map.get(key) != null ? String.valueOf(map.get(key)) : "";
    }

    public static Integer getInt(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if(obj == null) {
            return 0;
        } else if(obj instanceof String) {
            String str = (String)obj;
            return Integer.parseInt(str.trim());
        } else {
            return (Integer)obj;
        }
    }

    public static Long getLong(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if(obj == null) {
            return 0L;
        } else if(obj instanceof String) {
            String str = (String)obj;
            return Long.parseLong(str.trim());
        } else {
            return (Long)obj;
        }
    }

    public static Float getFloat(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if(obj == null) {
            return Float.valueOf(0);
        } else if(obj instanceof Integer) {
            return Float.valueOf(String.valueOf(obj));
        } else if(obj instanceof String) {
            String str = (String)obj;
            return Float.valueOf(str.trim());
        } else {
            return (Float)obj;
        }
    }

    public static Double getDouble(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if(obj == null) {
            return Double.valueOf(0);
        } else if(obj instanceof Integer) {
            return Double.valueOf(String.valueOf(obj));
        } else if(obj instanceof String) {
            String str = (String)obj;
            return Double.valueOf(str.trim());
        } else {
            return (Double)obj;
        }
    }

    public static Boolean getBoolean(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if(obj == null) {
            return false;
        } else if(obj instanceof String) {
            return Boolean.valueOf((String)obj);
        } else {
            return (Boolean) obj;
        }
    }

    public static Object jsonToObj(String json) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, Object.class);
    }

    public static String objToJsonStr(Object obj) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(obj);
    }
}
