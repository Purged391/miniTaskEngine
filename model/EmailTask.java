package model;

/** Represents a task that sends a message to an email address. */
public final record EmailTask(
        Long id,
        PriorityEnum priority,
        String email,
        String message) implements Task {

}
