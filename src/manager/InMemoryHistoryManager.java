package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> taskHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
        return newNode;
    }

    private List<Task> getTasks() {
        final List<Task> historyList = new ArrayList<>(size);
        Node<Task> curNode = head;
        while (curNode != null) {
            Task curTask = curNode.data;
            historyList.add(curTask);
            curNode = curNode.next;
        }
        return historyList;
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> nodePrev = node.prev;
        final Node<Task> nodeNext = node.next;
        if (nodePrev == null) {
            if (nodeNext != null) {
                head = nodeNext;
            } else {
                head = null;
            }
        } else {
            nodePrev.next = nodeNext;
        }

        if (nodeNext == null) {
            if (nodePrev != null) {
                tail = nodePrev;
            } else {
                tail = null;
            }
        } else {
            nodeNext.prev = nodePrev;
        }
    }

    @Override
    public void add(Task task) {
        final int taskId = task.getId();
        Node<Task> newNode = linkLast(task);
        if (taskHistory.containsKey(taskId)) {
            Node<Task> oldNode = taskHistory.get(taskId);
            removeNode(oldNode);
        }
        taskHistory.put(taskId, newNode);
    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            Node<Task> oldNode = taskHistory.get(id);
            removeNode(oldNode);
            taskHistory.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
