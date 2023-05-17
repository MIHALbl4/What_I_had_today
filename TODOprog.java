import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Task {
    private String text;
    private int priority;
    private LocalDateTime dueDate;

    public Task(String text, int priority, LocalDateTime dueDate) {
        this.text = text;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public String getText() {
        return text;
    }

    public int getPriority() {
        return priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }
}

class TodoList {
    private List<Task> tasks;

    public TodoList() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> getTasksByPriority(int priority) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPriority() == priority) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> getTasksDueToday() {
        List<Task> result = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        for (Task task : tasks) {
            if (task.getDueDate().toLocalDate().equals(today.toLocalDate())) {
                result.add(task);
            }
        }
        return result;
    }

    public void saveToFile(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Task task : tasks) {
                writer.write(task.getText() + "," + task.getPriority() + "," + task.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME));
                writer.newLine();
            }
        }
    }

    public void loadFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String text = parts[0];
                int priority = Integer.parseInt(parts[1]);
                LocalDateTime dueDate = LocalDateTime.parse(parts[2], DateTimeFormatter.ISO_DATE_TIME);
                tasks.add(new Task(text, priority, dueDate));
            }
        }
    }

    public void sortTasksByName() {
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getText().compareTo(t2.getText());
            }
        });
    }

    public void notifyTasksDueToday() {
        List<Task> tasksDueToday = getTasksDueToday();
        if (tasksDueToday.isEmpty()) {
            System.out.println("There are no tasks due today.");
        } else {
            System.out.println("Tasks due today:");
            for (Task task : tasksDueToday) {
                System.out.println(task.getText());
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to TodoList!");

        while (true) {
            System.out.println("Enter command (add, remove, list, save, load, sort, notify, exit):");
            String command = scanner.nextLine();
            if (command.equals("add")) {
                System.out.println("Enter task text:");
                String text = scanner.nextLine();
                System.out.println("Enter task priority (1-5):");
                int priority;
                while (true) {
                    try {
                        priority = Integer.parseInt(scanner.nextLine());
                        if (priority >= 1 && priority <= 5) {
                            break;
                        } else {
                            System.out.println("Invalid priority. Enter a number between 1 and 5:");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid priority. Enter a number between 1 and 5:");
                    }
                }
                System.out.println("Enter task due date (YYYY-MM-DDTHH:MM:SS):");
                LocalDateTime dueDate;
                while (true) {
                    try {
                        dueDate = LocalDateTime.parse(scanner.nextLine(), DateTimeFormatter.ISO_DATE_TIME);
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format. Enter a valid date and time (YYYY-MM-DDTHH:MM:SS):");
                    }
                }
                todoList.addTask(new Task(text, priority, dueDate));

            } else if (command.equals("remove")) {
                System.out.println("Enter task text to remove:");
                String text = scanner.nextLine();
                List<Task> tasks = todoList.getTasks();
                Task taskToRemove = null;
                for (Task task : tasks) {
                    if (task.getText().equals(text)) {
                        taskToRemove = task;
                        break;
                    }
                }
                if (taskToRemove != null) {
                    todoList.removeTask(taskToRemove);
                    System.out.println("Task removed.");
                } else {
                    System.out.println("Task not found.");
                }

            } else if (command.equals("list")) {
                System.out.println("Tasks:");
                List<Task> tasks = todoList.getTasks();
                for (Task task : tasks) {
                    System.out.println(task.getText() + " (priority: " + task.getPriority() + ", due date: " + task.getDueDate().format(DateTimeFormatter.ISO_DATE_TIME) + ")");
                }

            } else if (command.equals("save")) {
                try {
                    System.out.println("Enter file name:");
                    String fileName = scanner.nextLine();
                    todoList.saveToFile(fileName);
                    System.out.println("Tasks saved to file.");

                } catch (IOException e) {
                    System.out.println("Error saving tasks to file: " + e.getMessage());
                }

            } else if (command.equals("load")) {
                try {
                    System.out.println("Enter file name:");
                    String fileName = scanner.nextLine();
                    todoList.loadFromFile(fileName);
                    System.out.println("Tasks loaded from file.");

                } catch (IOException e) {
                    System.out.println("Error loading tasks from file: " + e.getMessage());
                }

            } else if (command.equals("sort")) {
                todoList.sortTasksByName();
                System.out.println("Tasks sorted by name.");

            } else if (command.equals("notify")) {
                todoList.notifyTasksDueToday();

            } else if (command.equals("exit")) {
                break;

            } else {
                System.out.println("Invalid command.");
            }
        }

        System.out.println("Goodbye!");
    }
}
