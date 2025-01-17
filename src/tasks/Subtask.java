package tasks;

public class Subtask extends Task {
    private final Epic relatedEpic;

    public Subtask(String name, String description, Epic relatedEpic) {
        super(name, description);
        this.relatedEpic = relatedEpic;
        relatedEpic.addTask(this);
    }

    public Epic getRelatedEpic() {
        return relatedEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", relatedEpic=" + relatedEpic +
                ", status=" + status +
                '}';
    }
}