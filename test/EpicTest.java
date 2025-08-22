import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

class EpicTest {
    @Test
    void checkEpicEqualsById(){
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
    void cantAddEpicAsSubtaskToEpic() {
        Epic firstEpic = new Epic(
                "Собраться в отпуск",
                "Спланировать и подготовить всё, что нужно для хорошего отпуска",
                1,
                TaskStatus.NEW);
        Epic secondEpic = new Epic(
                "Подготовиться к Новому году",
                "Подготовительные мероприятия к Новому году",
                2,
                TaskStatus.NEW);
        List<Task> subtasks = new ArrayList<>();
        subtasks.add(secondEpic);
        firstEpic.setSubtasks(subtasks);
        List<Task> addedSubtasks = firstEpic.getSubtasks();
        boolean result = false;
        for (Task task : addedSubtasks) {
            if (task.getId() == 2) {
                result = true;
                break;
            }
        }
        Assertions.assertFalse(result);
    }
}