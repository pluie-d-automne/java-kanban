package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ManagersTest {
    @Test
    public void checkHistoryManagerIsReady() {
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }

    @Test
    public void checkDefaultManagerIsReady() throws IOException {
        File file = File.createTempFile("kanban", "csv");
        Assertions.assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault(file.toString()));
    }

}
