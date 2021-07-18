package my.spring.sample.mvc.mongodb.cascade;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Getter
@Setter
public class CascadeCallback implements ReflectionUtils.FieldCallback{
    private Object source;
    private MongoOperations mongoOperations;

    CascadeCallback(final Object source, final MongoOperations mongoOperations) {
        this.source = source;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.makeAccessible(field);

        if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
            final Object fieldValue = field.get(getSource());

            if (fieldValue != null) {
                final FieldCallback callback = new FieldCallback();

                if(fieldValue instanceof List<?>) {
                    for(Object item : (List<?>)fieldValue) {
                        ReflectionUtils.doWithFields(item.getClass(), callback);
                        getMongoOperations().save(item);
                    }
                } else {
                    ReflectionUtils.doWithFields(fieldValue.getClass(), callback);
                    getMongoOperations().save(fieldValue);
                }
            }
        }
    }
}
