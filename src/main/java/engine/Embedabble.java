package engine;

import net.dv8tion.jda.core.entities.MessageEmbed;

public interface Embedabble {

    MessageEmbed displayInfo();
    boolean isMe(String other);

}
