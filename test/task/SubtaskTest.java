package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


public class SubtaskTest {
    @Test
    public void checkSubtaskEqualsById() {
        Subtask task1 = new Subtask(
                "Сабтаск 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                TaskType.SUBTASK,
                10,
                LocalDateTime.parse("2025-12-12T09:00:00"),
                0
        );
        Task task2 = new Subtask(
                "Сабтаск 132",
                "123",
                1,
                TaskStatus.NEW,
                TaskType.SUBTASK,
                45,
                LocalDateTime.parse("2025-12-12T15:00:00"),
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
                10,
                LocalDateTime.parse("2025-12-12T09:00:00"),
                null
        );
        subtask.setEpicId(1);
        Assertions.assertNotEquals(subtask.getEpicId(), subtask.getId());
    }
}