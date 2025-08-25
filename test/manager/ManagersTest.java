package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    public void checkHistoryManagerIsReady() {
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }

    @Test
    public void checkDefaultManagerIsReady() {
        Assertions.assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

}
