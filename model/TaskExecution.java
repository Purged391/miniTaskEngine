package model;

import java.time.LocalDateTime;

/** Stores the outcome and metadata produced when a task is processed. */
public record TaskExecution(
    Long taskId,
    ExecutionStatus status,
    String message,
    String taskType,
    LocalDateTime timestamp
) {
    
}
