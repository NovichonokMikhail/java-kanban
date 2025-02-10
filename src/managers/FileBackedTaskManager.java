package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;
import util.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    /**
     * Дефолтный конструктор
     * @param file название файла
     */
    public FileBackedTaskManager(File file) {
        super();
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.out.println("Failed to create a new file");
                }
            } catch (IOException e) {
                System.out.println("Error while creating file");
            }
        }
        setHistoryManager(new InMemoryHistoryManager());
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    private void save() throws ManagerSaveException {
        List<HashMap<Integer, ? extends Task>> tasksList = List.of(tasks, epics, subtasks);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            String FILE_FIRST_LINE = "id,type,name,status,description,epic\n";
            writer.write(FILE_FIRST_LINE);
            for (HashMap<Integer, ? extends Task> map : tasksList) {
                for (Task task : map.values()) {
                    String stringTask = toString(task);
                    writer.write(stringTask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Save was not successful");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                fromString(line, manager);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
        return manager;
    }

    private static String toString(Task task) {
        final String FORMAT = "%d,%S,%s,%S,%s,%s\n";
        if (task.getType() != TaskType.SUBTASK) {
            return String.format(FORMAT, task.getId(), task.getType(), task.getName()
                    , task.getStatus(), task.getDescription(), "");
        }
        Subtask subtask = (Subtask) task;
        return String.format(FORMAT, subtask.getId(), subtask.getType(), subtask.getName()
                , subtask.getStatus(), subtask.getDescription(), subtask.getRelatedEpic().getId());
    }

    private static void fromString(String str, FileBackedTaskManager manager) {
        String[] data = str.split(",");
        final int id = Integer.parseInt(data[0]);
        final TaskType type = TaskType.valueOf(data[1]);
        final String name = data[2];
        final TaskStatus status = TaskStatus.valueOf(data[3]);
        final String description = data[4];
        final int epicId = (data.length == 6) ? Integer.parseInt(data[5]) : -1;

        switch (type) {
            case TASK -> manager.createTask(new Task(name, description, status, id));
            case EPIC -> manager.createEpic(new Epic(name, description, id));
            case SUBTASK -> manager.createSubtask(new Subtask(name, description, status, id, manager.getEpic(epicId)));
        }
    }

    static class ManagerSaveException extends Exception {
        ManagerSaveException(String message) {
            super(message);
        }
    }
}