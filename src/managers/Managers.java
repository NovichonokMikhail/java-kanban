package managers;

/**
 * Вспомогательный класс для создания менеджеров
 */
public class Managers {
    /**
     * Метод для получения менеджера задач
     * @return {@code TaskManager} менеджер для задач
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Метод для получения менеджера истории задач
     * @return {@code HistoryManager} менеджер истории задач
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
