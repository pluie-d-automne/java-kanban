package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTest {
    @Test
    public void checkEpicEqualsById(){
        Epic task1 = new Epic(
                "Эпик 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS,
                TaskType.EPIC,
                0,
                LocalDateTime.parse("1970-01-01T00:00:00")
        );
        Task task2 = new Epic(
                "Эпик 32",
                "123",
                1,
                TaskStatus.NEW,
                TaskType.EPIC,
                0,
                LocalDateTime.parse("1970-01-01T00:00:00")
        );
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void cantAddEpicAsItsOwnSubtask() {
        Epic epic = new Epic(
                "Собраться в отпуск",
                "Спланировать и подготовить всё что нужно для хорошего отпуска",
                1,
                TaskStatus.NEW,
                TaskType.EPIC,
                0,
                LocalDateTime.parse("1970-01-01T00:00:00")
        );
        List<Task> subtasks = new ArrayList<>();
        subtasks.add(epic);
        epic.setSubtasks(subtasks);
        List<Task> addedSubtasks = epic.getSubtasks();
        boolean result = false;

        for (Task task : addedSubtasks) {
            if (task.getId() == epic.getId()) {
                result = true;
                break;
            }
        }

        Assertions.assertFalse(result);
    }
}