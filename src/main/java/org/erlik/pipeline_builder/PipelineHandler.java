package org.erlik.pipeline_builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface PipelineHandler<TRequest, TReturn> {

    private static boolean isAssignableFrom(Class<?> aClass, Class<?> otherClass) {
        Type[] interfaces = aClass.getGenericInterfaces();
        Type genericSuperclass = aClass.getGenericSuperclass();

        ParameterizedType type = (interfaces.length > 0)
                                 ? (ParameterizedType) interfaces[0]
                                 : (ParameterizedType) genericSuperclass;

        Type handlerCommand = type.getActualTypeArguments()[0];
        Class<?> handlerCommandClass = (handlerCommand instanceof ParameterizedType parameterized)
                                       ? (Class<?>) parameterized.getRawType()
                                       : (Class<?>) handlerCommand;

        return handlerCommandClass.isAssignableFrom(otherClass);
    }

    TReturn handle(TRequest request);

    default boolean matches(TRequest request) {
        Class<?> handlerType = getClass();
        Class<?> requestType = request.getClass();
        return isAssignableFrom(handlerType, requestType);
    }
}
