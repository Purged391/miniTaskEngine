package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import model.Task;

/** Marks a method as the handler for a specific task type. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handles {

    /** Returns the task class handled by the annotated method. */
    Class<? extends Task> value();
}