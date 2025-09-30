package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SubtaskTest {
    @Test
    public void checkEpicEqualsById() {
        Subtask task1 = new Subtask(
                "Сабтаск 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                TaskType.SUBTASK,
                0
        );
        Task task2 = new Subtask(
                "Сабтаск 132",
                "123",
                1,
                TaskStatus.NEW,
                TaskType.SUBTASK,
                3
        );
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void cantSetSubtaskItsOwnEpic() {
        Subtask subtask = new Subtask(
                "Сабтаск 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                TaskType.SUBTASK,
                null
        );
        subtask.setEpicId(1);
        Assertions.assertNotEquals(subtask.getEpicId(), subtask.getId());
    }
}