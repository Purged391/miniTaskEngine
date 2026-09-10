package handler;

import java.time.LocalDateTime;

import annotation.Handles;
import interfaces.TaskHandlerContainer;
import model.CleanUpTask;
import model.EmailTask;
import model.ExecutionStatus;
import model.ReportTask;
import model.Task;
import model.TaskExecution;
import service.TaskFormatter;

/** Default annotated handlers for all task types supported by the engine. */
public class DefaultTaskHandlers implements TaskHandlerContainer{

    private final TaskFormatter taskFormatter = new TaskFormatter();

    /** Processes an email task successfully. */
    @Handles(EmailTask.class)
    public TaskExecution handleEmail(EmailTask task) {
        return handle(task, taskFormatter.describe(task));
    }

    /** Validates and processes a report task. */
    @Handles(ReportTask.class)
    public TaskExecution handleReport(ReportTask task) {
        if (task.taskName() == null || task.taskName().isBlank()) {
            throw new IllegalArgumentException(
                    "Report name cannot be empty");
        }
        return handle(task, taskFormatter.describe(task));
    }

    /** Processes a cleanup task successfully. */
    @Handles(CleanUpTask.class)
    public TaskExecution handleCleanup(CleanUpTask task) {
        return handle(task, taskFormatter.describe(task));
    }

    private TaskExecution handle(Task task, String description) {
        return new TaskExecution(task.id(), ExecutionStatus.SUCCESS, description,
                task.getClass().getSimpleName(), LocalDateTime.now());

    }
}
