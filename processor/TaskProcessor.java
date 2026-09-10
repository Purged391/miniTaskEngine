package processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import annotation.Handles;
import exception.HandlerNotFoundException;
import exception.InvalidHandlerException;
import exception.TaskExecutionException;
import interfaces.TaskHandlerContainer;
import model.HandlerRegistration;
import model.Task;
import model.TaskExecution;

/** Discovers annotated handlers and dispatches tasks to them reflectively. */
public class TaskProcessor {

    private final Map<Class<? extends Task>, HandlerRegistration> handlerMap = new HashMap<>();

    /**
     * Builds a processor and validates every annotated handler declaration.
     *
     * @param handlers object containing the annotated handler methods
     * @throws InvalidHandlerException if a handler declaration is invalid
     */
    public TaskProcessor(TaskHandlerContainer... handlerObjects) {
        for (TaskHandlerContainer target : handlerObjects) {
            Class<?> clazz = target.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Handles.class)) {
                    Handles annotation = method.getAnnotation(Handles.class);
                    Class<? extends Task> clazzType = annotation.value();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (method.getParameterCount() != 1) {
                        throw new InvalidHandlerException(
                                "Illegal task handler. Must contain 1 argument with the task class");
                    }
                    Class<?> parameterType = parameterTypes[0];
                    if (handlerMap.containsKey(clazzType)) {
                        throw new InvalidHandlerException("Repeated task handler");
                    }
                    if (!parameterType.isAssignableFrom(clazzType)) {
                        throw new InvalidHandlerException("Illegal Task Handler Argument. Must be the same class");
                    }
                    if (!TaskExecution.class.isAssignableFrom(method.getReturnType())) {
                        throw new InvalidHandlerException("The return must be TaskExection class");
                    }
                    handlerMap.put(clazzType, new HandlerRegistration(target, method));
                }
            }
        }
    }

    /**
     * Processes a task using the handler registered for its concrete class.
     *
     * @param task task to process
     * @return the execution result returned by its handler
     * @throws HandlerNotFoundException if no handler is registered
     * @throws TaskExecutionException   if reflective invocation fails
     */
    public TaskExecution process(Task task) {
        Class<? extends Task> clazz = task.getClass();
        HandlerRegistration registration =
        handlerMap.get(clazz);
        if (registration == null) {
            throw new HandlerNotFoundException("Couldn't find task handler for that task");
        }
        Method method = registration.method();
        if (method == null) {
            throw new HandlerNotFoundException("Couldn't find handler method for that task");
        }
        try {
            return (TaskExecution) method.invoke(registration.target(), task);
        } catch (IllegalAccessException e) {
            throw new TaskExecutionException("Couldn't access method", e);
        } catch (InvocationTargetException e) {
            throw new TaskExecutionException("Couldn't invoke the method", e.getCause());
        }
    }

}
