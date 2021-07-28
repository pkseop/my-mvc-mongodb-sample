package my.spring.sample.mvc.component;

import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings("unchecked")
@Component
public class RedisConnector {

    @Value("${resource.redis.connect}")
    private boolean redisConnect;

    @SuppressWarnings("rawtypes")
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @SuppressWarnings("rawtypes")
    private RedisTemplate getRedisTemplate() {
        if(redisTemplate != null) {
            return redisTemplate;
        }else {
            if(redisConnect) {
                redisTemplate = (RedisTemplate)applicationContext.getBean("redisTemplate");
                log.info("********************** redis connected. **********************");
                return redisTemplate;
            }
            return null;
        }
    }

    public void setValue(Object key, Object value, long timeout, TimeUnit unit) {
        if(getRedisTemplate() != null) {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        }
    }

    public Object getValue(Object key) {
        if(getRedisTemplate() != null) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }

    public void putValue(Object key, Object mapKey, Object value) {
        if(getRedisTemplate() != null)
            redisTemplate.opsForHash().put(key, mapKey, value);

    }

    public Object getValue(Object key, Object mapKey) {
        if(getRedisTemplate() != null) {
            return redisTemplate.opsForHash().get(key, mapKey);
        }
        return null;
    }

    public void deleteMapKeys(Object key, Object... mapKeys) {
        if(getRedisTemplate() != null)
            redisTemplate.opsForHash().delete(key, mapKeys);
    }

    public Map<Object, Object> getMap(Object key) {
        if(getRedisTemplate() != null) {
            return redisTemplate.opsForHash().entries(key);
        }
        return null;
    }

    public void putAllValue(Object key, Map<Object, Object> map) {
        if(getRedisTemplate() != null)
            redisTemplate.opsForHash().putAll(key, map);
    }

    public void putAllStringValue(String key, Map<String, Object> map) {
        if(getRedisTemplate() != null)
            redisTemplate.opsForHash().putAll(key, map);
    }

    public void putAllGeoMapValue(Object key, Map<String, Map<String, Object>> map) {
        if(getRedisTemplate() != null) {
            Map<String, String> castMap = new HashMap<String, String>();
            for (Map.Entry<String, Map<String, Object>> set : map.entrySet()) {
                castMap.put(set.getKey(), JsonUtils.convertMapToJson(set.getValue()));
            }
            redisTemplate.opsForHash().putAll(key, castMap);
        }
    }

    public void putAllMapValue(String key, Map<String, Object> map) {
        if(getRedisTemplate() != null) {
            Map<String, String> castMap = new HashMap<String, String>();
            for (Map.Entry<String, Object> set : map.entrySet()) {
                castMap.put(set.getKey(), JsonUtils.convertMapToJson((Map<String, Object>)set.getValue()));
            }
            redisTemplate.opsForHash().putAll(key, castMap);
        }
    }

    public void putAllObjectMapValue(String key, Map<Object, Object> map) {
        if(getRedisTemplate() != null) {
            Map<String, String> castMap = new HashMap<String, String>();
            for (Map.Entry<Object, Object> set : map.entrySet()) {
                castMap.put(set.getKey().toString(), JsonUtils.convertObjectMapToJson((Map<Object, Object>)set.getValue()));
            }
            redisTemplate.opsForHash().putAll(key, castMap);
        }
    }

    public Map<Object, Map<Object, Object>>getAllMap(Object pattern) {
        if(getRedisTemplate() != null) {
            Map<Object, Map<Object,Object>> resultMap = new HashMap<Object, Map<Object, Object>>();

            Set<Object> keys = redisTemplate.keys(pattern);
            for (Object key : keys) {
                Map<Object,Object> value = redisTemplate.opsForHash().entries(key);
                resultMap.put(key, value);
            }

            return resultMap;
        }
        return null;
    }

    public void deleteKey(Object key) {
        if(getRedisTemplate() != null)
            redisTemplate.delete(key);
    }

    public void setKeyExpire(Object key, long sec) {
        if(getRedisTemplate() != null)
            redisTemplate.expire(key, sec, TimeUnit.SECONDS);
    }

    public void intRedisIndex(Object key) {
        if(getRedisTemplate() != null) {
            redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
            redisTemplate.opsForValue().set(key, "0");
        }
    }

    public boolean checkInitRedisIndex(Object key) {
        if(getRedisTemplate() != null) {
            Object obj = redisTemplate.opsForValue().get(key);
            if(obj!=null) {
                return true;
            }
            return false;
        }
        return false;
    }

    public Long getRedisIndex(Object key, long increment) {
        if(getRedisTemplate() != null) {
            return redisTemplate.opsForValue().increment(key, increment);
        }
        return 0L;
    }
}
