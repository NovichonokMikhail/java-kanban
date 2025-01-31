package managers;

import tasks.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> historyMap;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        final Node<Task> taskNode = new Node<>(null, task, null);
        linkLast(taskNode);
        historyMap.put(task.getId(), taskNode);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();

        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void linkLast(Node<Task> node) {
        if (head == null) {
            head = node;
            tail = node;
        } else {
            final Node<Task> oldTail = tail;
            tail = node;
            tail.prev = oldTail;
            oldTail.next = tail;
        }
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> prevNode = node.prev;
        final Node<Task> nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        } else if (prevNode == null) {
            nextNode.prev = null;
            head = nextNode;
        } else if (nextNode == null) {
            prevNode.next = null;
            tail = prevNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }
}