package service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import model.ExecutionStatus;
import model.TaskExecution;

/** Aggregates execution results for a collection of processed tasks. */
public class ExecutionReport {
    private final List<TaskExecution> executions;

    private static final Predicate<TaskExecution> SUCCESS_TASK_PREDICATE = taskExecution -> ExecutionStatus.SUCCESS == taskExecution.status();
    private static final Predicate<TaskExecution> FAILED_TASK_PREDICATE = taskExecution -> ExecutionStatus.FAILED == taskExecution.status();

    /** Creates a report from an immutable copy of the executions. */
    public ExecutionReport(List<TaskExecution> executions) {
        this.executions = List.copyOf(executions);
    }

    /** Returns the number of processed tasks. */
    public long total() {
        return executions.size();
    }

    /** Returns the number of successful executions. */
    public long successCount() {
        return executions.stream().filter(SUCCESS_TASK_PREDICATE).count();
    }

    /** Returns the number of failed executions. */
    public long failedCount() {
        return executions.stream().filter(FAILED_TASK_PREDICATE).count();
    }

    /** Counts executions grouped by their status. */
    public Map<ExecutionStatus, Long> countByStatus() {
        return executions.stream().collect(Collectors.groupingBy(TaskExecution::status, Collectors.counting()));

    }

    /** Returns the identifiers of successful tasks in execution order. */
    public List<Long> successfulTaskIds() {
        return executions.stream().filter(SUCCESS_TASK_PREDICATE).map(TaskExecution::taskId).toList();
    }
}
