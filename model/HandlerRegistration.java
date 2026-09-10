package model;

import java.lang.reflect.Method;

public record HandlerRegistration(
        Object target,
        Method method
) {
}