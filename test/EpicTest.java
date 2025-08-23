import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

class EpicTest {
    @Test
    void checkEpicEqualsById() {
        Epic task1 = new Epic(
                "Эпик 1",
                "-",
                1,
                TaskStatus.IN_PROGRESS
        );
        Task task2 = new Epic(
                "Эпик 32",
                "123",
                1,
                TaskStatus.NEW
        );
        Assertions.assertEquals(task1, task2);
    }

    @Test
    void cantAddEpicAsItsOwnSubtask() {
        Epic epic = new Epic(
                "Собраться в отпуск",
                "Спланировать и подготовить всё, что нужно для хорошего отпуска",
                1,
                TaskStatus.NEW);
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