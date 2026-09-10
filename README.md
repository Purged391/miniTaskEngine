# Mini Task Engine

Mini Task Engine is a small Java task-processing example. It demonstrates a generic in-memory repository, priority-based task scheduling, runtime handler discovery through annotations, reflective task dispatching, failure handling, and execution reporting.

## Features

- Generic `Repository<ID, T>` contract with an `InMemoryRepository` implementation.
- Three sealed task types: `EmailTask`, `ReportTask`, and `CleanUpTask`.
- Priority queue that processes higher-priority tasks first.
- Automatic discovery of handler methods annotated with `@Handles`.
- Validation of handler declarations during processor construction.
- Preservation of the original cause when a handler fails.
- Aggregated execution reports with totals, status counts, and successful task IDs.
- Ten executable console demonstrations in `Main`.

## Project Structure

```text
annotation/   Runtime handler annotations
exception/    Domain-specific runtime exceptions
handler/      Default handlers for the supported task types
interfaces/   Shared interfaces
model/        Tasks, priorities, statuses, and execution results
processor/    Annotation-based task dispatcher
repository/   Repository contract and in-memory implementation
service/      Queue, formatter, and execution report services
Main.java     Console entry point and test-case runner
```

## Requirements

- Java 21 or a compatible Java version supporting sealed types and pattern matching in switch expressions.
- No external dependencies are required.

## Compile and Run

From the project root, compile all sources into the `out` directory:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java).FullName
```

Run a specific demonstration by passing its number:

```powershell
java -cp out Main 1
```

If no argument is supplied, case 8 is executed:

```powershell
java -cp out Main
```

## Console Cases

| Case | Description |
| --- | --- |
| 1 | Saves task 1 and retrieves it with `findById`. |
| 2 | Looks up an unknown ID and prints `Optional.empty()`. |
| 3 | Attempts to save two tasks with ID 1 and expects `DuplicateEntityException`. |
| 4 | Verifies priority order using priorities 1, 10, and 5. |
| 5 | Verifies automatic discovery of the email, report, and cleanup handlers. |
| 6 | Temporarily removes a handler and expects `HandlerNotFoundException`. |
| 7 | Verifies that a handler failure keeps the original `IllegalArgumentException` cause. |
| 8 | Processes the sample task set and prints the execution report. |
| 9 | Verifies handlers discovered from three different objects. |
| 10 | Verifies duplicate handlers are rejected across different objects. |

## Debugging in VS Code

The project includes `.vscode/launch.json`. Press `F5`, select **Debug Main**, and choose a case from 1 to 10 when prompted. The selected number is passed to `Main` as a command-line argument.

You can also create a fixed debug configuration by setting the argument directly:

```json
{
    "type": "java",
    "name": "Debug Main case 1",
    "request": "launch",
    "mainClass": "Main",
    "args": "1"
}
```

## Example Report

Case 8 prints task results followed by a report similar to:

```text
REPORT

Total: 6
Success: 5
Failed: 1

By status:
{SUCCESS=5, FAILED=1}

Successful task ids:
[1, 3, 4, 2, 6]
```

The failed task is intentional: `report2` has an empty report name so that the original handler cause can be demonstrated.
