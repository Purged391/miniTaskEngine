package model;

import interfaces.Identifiable;

/** Common contract for every task accepted by the engine. */
public sealed interface Task extends Identifiable<Long> permits EmailTask, ReportTask, CleanUpTask{
    /** Returns the priority used when ordering the task. */
    public PriorityEnum priority();
}