package service;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

import model.Task;

/** Priority queue that returns higher-priority tasks first. */
public class TaskQueue {
    private static final Comparator<Task> TASK_COMPARATOR = Comparator.comparing(Task::priority).reversed()
            .thenComparing(Task::id);
    private final PriorityQueue<Task> priorityQueue = new PriorityQueue<>(TASK_COMPARATOR);

    /** Adds a task to the queue. */
    public void add(Task task) {
        priorityQueue.add(task);
    }

    /** Retrieves and removes the next task, if any. */
    public Optional<Task> next(){
        return Optional.ofNullable(priorityQueue.poll());
    }
    
    /** Reports whether the queue contains no tasks. */
    public boolean isEmpty(){
        return priorityQueue.isEmpty();
    }
}
