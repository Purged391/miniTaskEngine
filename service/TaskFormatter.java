package service;

import model.CleanUpTask;
import model.EmailTask;
import model.ReportTask;
import model.Task;

/** Converts supported task types into human-readable descriptions. */
public class TaskFormatter {
    /** Describes a task according to its concrete type. */
    public String describe(Task task){
        return switch (task) {
            case EmailTask emailTask -> "Sending email to " + emailTask.email();
            case ReportTask reportTask -> "Generating report: " + reportTask.taskName();
            case CleanUpTask cleanUpTask -> "Cleaning " + cleanUpTask.path();
        };
    }
}
