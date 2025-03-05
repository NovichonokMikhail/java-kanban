import managers.InMemoryTaskManager;

class InMemoryTaskManagersTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createManger() {
        return new InMemoryTaskManager();
    }
}