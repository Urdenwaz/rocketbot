package engine;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

public class Task implements Embedabble {

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

    @Override
    public MessageEmbed displayInfo() {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Task Info", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.blue);

        bldr.addField("Task: ", taskName, false);
        bldr.addField("Responsible Person(s): ", responsible, false);
        bldr.addField("Priority: ", priority, false);

        bldr.setFooter("Bot created and managed by Zak", null);

        return bldr.build();
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
            case "eventually": this.priority = PRIORITY_CHOICES[3]; break;

            default: throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return taskName + " " + responsible + " " + priority;
    }

}
