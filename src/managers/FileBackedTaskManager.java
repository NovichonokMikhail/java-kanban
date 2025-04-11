package managers;

import exceptions.ManagerSaveException;
import exceptions.TimeIntervalOccupiedException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.TaskStatus;
import util.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
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

    @Override
    public void createTask(Task task) throws TimeIntervalOccupiedException {
        super.createTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createEpic(Epic epic) throws TimeIntervalOccupiedException {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createSubtask(Subtask subtask) throws TimeIntervalOccupiedException {
        super.createSubtask(subtask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    private void save() throws ManagerSaveException {
        List<String> lines = Stream.of(tasks, epics, subtasks)
                .flatMap(map -> map.values().stream())
                .map(FileBackedTaskManager::toString)
                .toList();
        final String firstLine = "id,type,name,status,description,epic,duration,date\n";

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8))  {
            writer.write(firstLine);
            for (String line : lines) writer.write(line);
        } catch (IOException e) {
            throw new ManagerSaveException("Save was not successful");
        }
    }

    private static String toString(Task task) {
        final String FORMAT = "%d,%S,%s,%S,%s,%s,%s,%s\n";

        if (task.getStartTime() != null) {
            if (task.getType() != TaskType.SUBTASK) {
                return String.format(FORMAT, task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), " ", task.getDuration().toMinutes(),
                        task.getStartTime().format(DATE_TIME_FORMATTER));
            }
            Subtask subtask = (Subtask) task;
            return String.format(FORMAT, subtask.getId(), subtask.getType(), subtask.getName(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getRelatedEpicId(),
                    subtask.getDuration().toMinutes(), subtask.getStartTime().format(DATE_TIME_FORMATTER));
        }

        if (task.getType() != TaskType.SUBTASK) {
            return String.format(FORMAT, task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), " ", " ", " ");
        }
        Subtask subtask = (Subtask) task;
        return String.format(FORMAT, subtask.getId(), subtask.getType(), subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getRelatedEpicId(),
                " ", " ");
    }

    private static void fromString(String str, FileBackedTaskManager manager) {
        String[] data = str.split(",");
        final int id = Integer.parseInt(data[0]);
        final TaskType type = TaskType.valueOf(data[1]);
        final String name = data[2];
        final TaskStatus status = TaskStatus.valueOf(data[3]);
        final String description = data[4];
        final int epicId = (!data[5].isBlank()) ? Integer.parseInt(data[5]) : -1;
        final Long duration;
        final LocalDateTime dateTime;
        if (!data[6].isBlank()) {
            duration = Long.parseLong(data[6]);
            dateTime = LocalDateTime.parse(data[7], DATE_TIME_FORMATTER);
        } else {
            duration = null;
            dateTime = null;
        }


        switch (type) {
            case TASK -> manager.createTask(new Task(name, description, status, id, duration,dateTime));
            case EPIC -> manager.createEpic(new Epic(name, description, id, duration, dateTime));
            case SUBTASK -> manager.createSubtask(new Subtask(name, description, status, id,
                    epicId, duration, dateTime));
        }
    }
}