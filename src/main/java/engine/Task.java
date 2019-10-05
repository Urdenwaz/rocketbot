package engine;

public class Task {

    private String taskName;
    private String responsible;
    private String priority;

    private static final String[] PRIORITY_CHOICES = { "asap", "week", "month", "eventually" };

    public Task(String taskName, String responsible, String priority) {
        this.taskName = taskName;
        this.responsible = responsible;

        switch (priority) {
            case "a": this.priority = PRIORITY_CHOICES[0]; break;
            case "w": this.priority = PRIORITY_CHOICES[1]; break;
            case "m": this.priority = PRIORITY_CHOICES[2]; break;
            case "e": this.priority = PRIORITY_CHOICES[3]; break;

            case "asap": this.priority = PRIORITY_CHOICES[0]; break;
            case "week": this.priority = PRIORITY_CHOICES[1]; break;
            case "month": this.priority = PRIORITY_CHOICES[2]; break;
            case "eventually": this.priority = PRIORITY_CHOICES[3]; break;

            default: throw new IllegalArgumentException();
        }
    }

    public boolean isMe(String taskName) {
        return this.taskName.equalsIgnoreCase(taskName.trim());
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        switch (priority) {
            case "a": this.priority = PRIORITY_CHOICES[0]; break;
            case "w": this.priority = PRIORITY_CHOICES[1]; break;
            case "m": this.priority = PRIORITY_CHOICES[2]; break;
            case "e": this.priority = PRIORITY_CHOICES[3]; break;

            case "asap": this.priority = PRIORITY_CHOICES[0]; break;
            case "week": this.priority = PRIORITY_CHOICES[1]; break;
            case "month": this.priority = PRIORITY_CHOICES[2]; break;
            case "eventually": this.priority = PRIORITY_CHOICES[3];

            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return taskName + " " + responsible + " " + priority;
    }

}
