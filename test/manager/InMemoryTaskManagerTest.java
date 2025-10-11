package manager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}
