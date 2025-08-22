import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Subtask;
import task.Task;
import task.TaskStatus;


class SubtaskTest {
    @Test
    void checkEpicEqualsById() {
        Subtask task1 = new Subtask(
                "Сабтаск 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                0
        );
        Task task2 = new Subtask(
                "Сабтаск 132",
                "123",
                1,
                TaskStatus.NEW,
                3
        );
        Assertions.assertEquals(task1, task2);
    }
    @Test
    void cantSetSubtaskItsOwnEpic(){
        Subtask subtask = new Subtask(
                "Сабтаск 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                null
        );
        subtask.setEpicId(1);
        Assertions.assertNotEquals(subtask.getEpicId(), subtask.getId());
    }
}