import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    void checkHistoryManagerIsReady(){
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
    @Test
    void checkDefaultManagerIsReady(){
        Assertions.assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

}
