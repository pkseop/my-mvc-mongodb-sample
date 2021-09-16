package my.spring.sample.mvc.utils;

import my.spring.sample.mvc.dto.DataListResponse;
import my.spring.sample.mvc.dto.DataListWithPageResponse;
import my.spring.sample.mvc.dto.DataResponse;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParamTypeRefUtil {

    public static<T> ParameterizedTypeReference<DataListWithPageResponse<T>> listWithPage(Class<T> clazz) {
        return new ParameterizedTypeReference<DataListWithPageResponse<T>>(){
            @Override
            public Type getType() {
                return new CustomParameterizedTypeImpl((ParameterizedType)super.getType(), new Type[]{clazz});
            }
        };
    }

    public static<T> ParameterizedTypeReference<DataListResponse<T>> list(Class<T> clazz) {
        return new ParameterizedTypeReference<DataListResponse<T>>(){
            @Override
            public Type getType() {
                return new CustomParameterizedTypeImpl((ParameterizedType)super.getType(), new Type[]{clazz});
            }
        };
    }

    public static<T> ParameterizedTypeReference<DataResponse<T>> data(Class<T> clazz) {
        return new ParameterizedTypeReference<DataResponse<T>>(){
            @Override
            public Type getType() {
                return new CustomParameterizedTypeImpl((ParameterizedType)super.getType(), new Type[]{clazz});
            }
        };
    }

}

class CustomParameterizedTypeImpl implements ParameterizedType {
    private ParameterizedType delegate;
    private Type[] actualTypeArguments;

    CustomParameterizedTypeImpl(ParameterizedType delegate, Type[] actualTypeArguments) {
        this.delegate = delegate;
        this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return delegate.getRawType();
    }

    @Override
    public Type getOwnerType() {
        return delegate.getOwnerType();
    }
}
