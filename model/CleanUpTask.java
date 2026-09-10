package model;

/** Represents a task that cleans a configured path. */
public final record CleanUpTask(
        Long id,
        PriorityEnum priority,
        String path)
        implements Task {

}
