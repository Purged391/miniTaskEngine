package model;

/** Represents a task that generates a named report. */
public final record ReportTask(
    Long id,
    PriorityEnum priority,
    String taskName
) implements Task {
}
