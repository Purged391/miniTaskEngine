import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import annotation.Handles;
import exception.InvalidHandlerException;
import exception.TaskExecutionException;
import exception.DuplicateEntityException;
import exception.HandlerNotFoundException;
import handler.DefaultTaskHandlers;
import interfaces.TaskHandlerContainer;
import model.CleanUpTask;
import model.EmailTask;
import model.ExecutionStatus;
import model.HandlerRegistration;
import model.PriorityEnum;
import model.ReportTask;
import model.Task;
import model.TaskExecution;
import processor.TaskProcessor;
import repository.InMemoryRepository;
import repository.Repository;
import service.ExecutionReport;
import service.TaskQueue;

/** Console entry point containing executable demonstrations of the engine. */
public class Main {

    private static final Task email1 = new EmailTask(
            1L,
            PriorityEnum.HIGH,
            "mario@example.com",
            "Welcome to the platform");

    private static final Task report1 = new ReportTask(
            2L,
            PriorityEnum.LOW,
            "Monthly sales report");

    private static final Task cleanup1 = new CleanUpTask(
            3L,
            PriorityEnum.MEDIUM,
            "/tmp/task-engine");

    private static final Task email2 = new EmailTask(
            4L,
            PriorityEnum.MEDIUM,
            "admin@example.com",
            "System maintenance completed");

    private static final Task report2 = new ReportTask(
            5L,
            PriorityEnum.HIGH,
            "" // caso preparado para que falle
    );

    private static final Task cleanup2 = new CleanUpTask(
            6L,
            PriorityEnum.LOW,
            "/var/cache/task-engine");

        /** Selects a test case from the command-line argument, defaulting to case 8. */
    public static void main(String[] args) {
                int testCase = args.length == 0 ? 8 : Integer.parseInt(args[0]);

                switch (testCase) {
                        case 1 -> testRepositoryFind();
                        case 2 -> testRepositoryMissing();
                        case 3 -> testRepositoryDuplicate();
                        case 4 -> testPriority();
                        case 5 -> testAnnotationHandlers();
                        case 6 -> testMissingHandler();
                        case 7 -> testHandlerFailureCause();
                        case 8 -> testExecutionReport();
                        case 9 -> testHandlersFromDifferentObjects();
                        case 10 -> testDuplicateHandlerAcrossObjects();
                        default -> System.out.println("Selecciona un caso del 1 al 10");
                }
        }

        /** Verifies saving and retrieving a task by identifier. */
        private static void testRepositoryFind() {
                Repository<Long, Task> repository = new InMemoryRepository<>();
                repository.save(email1);
                System.out.println(repository.findById(1L).orElseThrow());
        }

        /** Verifies that an unknown identifier returns an empty optional. */
        private static void testRepositoryMissing() {
                Repository<Long, Task> repository = new InMemoryRepository<>();
                System.out.println(repository.findById(999L));
        }

        /** Verifies that duplicate identifiers are rejected. */
        private static void testRepositoryDuplicate() {
                Repository<Long, Task> repository = new InMemoryRepository<>();
                repository.save(email1);

                try {
                        repository.save(new EmailTask(
                                        1L,
                                        PriorityEnum.LOW,
                                        "duplicate@example.com",
                                        "Duplicate task"));
                } catch (DuplicateEntityException e) {
                        System.out.println(e.getClass().getSimpleName());
                }
        }

        /** Verifies that the queue returns tasks in descending priority order. */
        private static void testPriority() {
                TaskQueue taskQueue = new TaskQueue();
                taskQueue.add(new ReportTask(1L, PriorityEnum.LOW, "Low"));
                taskQueue.add(new ReportTask(10L, PriorityEnum.HIGH, "High"));
                taskQueue.add(new ReportTask(5L, PriorityEnum.MEDIUM, "Medium"));

                while (!taskQueue.isEmpty()) {
                        System.out.println(taskQueue.next().orElseThrow().id());
                }
        }

        /** Verifies automatic discovery of the three annotated handlers. */
        private static void testAnnotationHandlers() {
                TaskProcessor taskProcesor = new TaskProcessor(new DefaultTaskHandlers());
                List<Task> tasks = List.of(email1, report1, cleanup1);

                for (Task task : tasks) {
                        TaskExecution execution = taskProcesor.process(task);
                        System.out.printf("%s -> %s%n", task.getClass().getSimpleName(), execution.status());
                }
        }

        /** Verifies the exception raised when a handler is unavailable. */
        private static void testMissingHandler() {
                TaskProcessor taskProcesor = new TaskProcessor(new DefaultTaskHandlers());

                try {
                        Field handlerMapField = TaskProcessor.class.getDeclaredField("handlerMap");
                        handlerMapField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        Map<Class<? extends Task>, HandlerRegistration> handlerMap =
                                        (Map<Class<? extends Task>, HandlerRegistration>) handlerMapField.get(taskProcesor);
                        handlerMap.remove(ReportTask.class);
                        taskProcesor.process(report1);
                } catch (HandlerNotFoundException e) {
                        System.out.println(e.getClass().getSimpleName());
                } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException("No se pudo retirar temporalmente el handler", e);
                }
        }

        /** Verifies that handler failures retain their original cause. */
        private static void testHandlerFailureCause() {
                TaskProcessor taskProcesor = new TaskProcessor(new DefaultTaskHandlers());

                try {
                        taskProcesor.process(report2);
                } catch (TaskExecutionException e) {
                        System.out.printf("Cause: %s%nMessage: %s%n",
                                        e.getCause().getClass().getSimpleName(),
                                        e.getCause().getMessage());
                }
        }

        /** Runs all sample tasks and prints an aggregated execution report. */
        private static void testExecutionReport() {
        Repository<Long, Task> repository = new InMemoryRepository<>();
        repository.save(email1);
        repository.save(email2);
        repository.save(report1);
        repository.save(report2);
        repository.save(cleanup1);
        repository.save(cleanup2);

        TaskQueue taskQueue = new TaskQueue();
        taskQueue.add(email1);
        taskQueue.add(report1);
        taskQueue.add(cleanup1);
        taskQueue.add(email2);
        taskQueue.add(report2);
        taskQueue.add(cleanup2);

        DefaultTaskHandlers defaultTaskHandlers = new DefaultTaskHandlers();
        TaskProcessor taskProcesor = new TaskProcessor(defaultTaskHandlers);

        List<TaskExecution> listExecutedTasks = new ArrayList<>();
        while (!taskQueue.isEmpty()) {
            Task task = taskQueue.next()
                    .orElseThrow();
            try {
                TaskExecution taskExecution = taskProcesor.process(task);
                listExecutedTasks.add(taskExecution);
                System.out.printf(
                        "[%s] %s%n",
                        taskExecution.status(),
                        taskExecution.message());
            } catch (TaskExecutionException e) {

                TaskExecution taskExecutionFailed = new TaskExecution(
                        task.id(),
                        ExecutionStatus.FAILED,
                        e.getCause().getMessage(),
                        task.getClass().getSimpleName(),
                        LocalDateTime.now());
                listExecutedTasks.add(taskExecutionFailed);
                System.out.printf(
                        "[%s] %s%n",
                        taskExecutionFailed.status(),
                        taskExecutionFailed.message());

            }

        }

        ExecutionReport executionReport = new ExecutionReport(listExecutedTasks);
        System.out.printf(
                "REPORT%n%n" +
                        "Total: %d%n" +
                        "Success: %d%n" +
                        "Failed: %d%n%n" +
                        "By status:%n" +
                        "%s%n%n" +
                        "Successful task ids:%n" +
                        "%s%n",
                executionReport.total(),
                executionReport.successCount(),
                executionReport.failedCount(),
                executionReport.countByStatus(),
                executionReport.successfulTaskIds());
    }

        /** Verifies that each task is dispatched to the object that declares its handler. */
        private static void testHandlersFromDifferentObjects() {
                EmailTaskHandlers emailHandlers = new EmailTaskHandlers();
                ReportTaskHandlers reportHandlers = new ReportTaskHandlers();
                CleanUpTaskHandlers cleanupHandlers = new CleanUpTaskHandlers();
                TaskProcessor taskProcesor = new TaskProcessor(
                                emailHandlers,
                                reportHandlers,
                                cleanupHandlers);

                assertMessage(taskProcesor.process(email1), "EmailTaskHandlers");
                assertMessage(taskProcesor.process(report1), "ReportTaskHandlers");
                assertMessage(taskProcesor.process(cleanup1), "CleanUpTaskHandlers");
                System.out.println("Multiple handler objects: passed");
        }

        /** Verifies that duplicate task handlers are rejected across different objects. */
        private static void testDuplicateHandlerAcrossObjects() {
                try {
                        new TaskProcessor(new EmailTaskHandlers(), new DuplicateEmailTaskHandlers());
                        throw new AssertionError("Expected InvalidHandlerException for duplicate handlers");
                } catch (InvalidHandlerException e) {
                        if (!"Repeated task handler".equals(e.getMessage())) {
                                throw new AssertionError("Unexpected duplicate-handler message", e);
                        }
                        System.out.println("Duplicate handlers across objects: passed");
                }
        }

        private static void assertMessage(TaskExecution execution, String expectedMessage) {
                if (!expectedMessage.equals(execution.message())) {
                        throw new AssertionError(
                                        "Expected handler message " + expectedMessage + " but got " + execution.message());
                }
        }

        private static TaskExecution successfulExecution(Task task, String handlerObject) {
                return new TaskExecution(
                                task.id(),
                                ExecutionStatus.SUCCESS,
                                handlerObject,
                                task.getClass().getSimpleName(),
                                LocalDateTime.now());
        }

        public static final class EmailTaskHandlers implements TaskHandlerContainer {
                @Handles(EmailTask.class)
                public TaskExecution handle(EmailTask task) {
                        return successfulExecution(task, "EmailTaskHandlers");
                }
        }

        public static final class ReportTaskHandlers implements TaskHandlerContainer {
                @Handles(ReportTask.class)
                public TaskExecution handle(ReportTask task) {
                        return successfulExecution(task, "ReportTaskHandlers");
                }
        }

        public static final class CleanUpTaskHandlers implements TaskHandlerContainer {
                @Handles(CleanUpTask.class)
                public TaskExecution handle(CleanUpTask task) {
                        return successfulExecution(task, "CleanUpTaskHandlers");
                }
        }

        public static final class DuplicateEmailTaskHandlers implements TaskHandlerContainer {
                @Handles(EmailTask.class)
                public TaskExecution handle(EmailTask task) {
                        return successfulExecution(task, "DuplicateEmailTaskHandlers");
                }
        }
}
