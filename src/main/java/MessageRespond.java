import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRespond extends ListenerAdapter {

    private List<Prompt> prompts;
    private String design; // allows for design bufferedreader
    private static final String GLORIOUS_CREATOR = "191367988224458752";
    private static final String PREFIX = "r!";
    private Set trusteds() { // bot permissions
        Set set = new HashSet<String>();

        set.add(GLORIOUS_CREATOR); // Zak
        set.add("323331293787979781"); // Erin
        set.add("502619027122946048"); // Eugenia
        set.add("318874242450063371"); // Aaryea

        return set;
    }

    public MessageRespond() throws IOException { // reads file so I don't put our design folder online again
        BufferedReader rdr2 = new BufferedReader(new FileReader("designfolder.txt"));
        design = rdr2.readLine();

        String types[] = { "twe", "eval", "disc" };
        prompts = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            prompts.add(new Prompt(types[i]));
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) { // receives a message
        String text = e.getMessage().getContentStripped();
        String name = e.getAuthor().getName();
        String userId = e.getAuthor().getId();
        Message message = e.getMessage();
        Guild guild = e.getGuild();

        if (!userId.equals("504527272061960193")) {
            if (text.toLowerCase().equals("f")) {
                e.getChannel().sendMessage("https://cdn.discordapp.com/emojis/410317239859019776.gif?v=1").complete();
            }
        }

        if (text.startsWith(PREFIX)) {
            Scanner messageInput = new Scanner(text.substring(PREFIX.length()));
            String command = messageInput.next();

            switch (command) {
                case "help":
                    EmbedBuilder bldr = new EmbedBuilder();

                    bldr.setAuthor("rocketbot Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                    bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                    bldr.setColor(Color.red);

                    bldr.addField("r!rqdoc", "TARC Requirements Document", false);
                    bldr.addField("r!specs", "TARC Rocket Specifications", false);
                    bldr.addField("r!design", "Link to Current OpenRocket Design", false);
                    bldr.addField("r!openrocket", "Link to OpenRocket", false);
                    bldr.addField("r!paper3 <twe, eval, disc> <prompt>", "Generate a Paper 3 Prompt", false);
                    bldr.addBlankField(false);
                    bldr.addField("People who Zak decides only:", "(Erin, Eugenia, Zak, Aaryea)", false);
                    bldr.addField("r!hounds <number> <mentionedUser>", "Releases the hounds", false);

                    e.getChannel().sendMessage(bldr.build()).complete();
                    break;

                case "rqdoc":
                    e.getChannel().sendMessage("__**TARC Requirements Doc:**__ \nhttps://3384f12ld0l0tjlik1fcm68s-wpengine.netdna-ssl.com/wp-content/uploads/2018/08/Event-Rules-TARC-2019-FINAL.pdf").complete();
                    break;

                case "design":
                    e.getChannel().sendMessage(design).complete();
                    break;

                case "openrocket":
                    e.getChannel().sendMessage("http://openrocket.info/").complete();
                    break;

                case "specs":
                    EmbedBuilder bldr2 = new EmbedBuilder();

                    bldr2.setAuthor("TARC Rocket Specifications", "https://rocketcontest.org/","https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                    bldr2.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504817600790921256/TARC_logo2_9-23-08_400x400.png");
                    bldr2.setColor(Color.blue);

                    bldr2.addField("Mass", "Cannot exceed 650 grams", false);
                    bldr2.addField("Length", "No less than 650 mm (25.6 inches)", false);
                    bldr2.addField("Paint", "In order to launch at finals, must be painted (5 point penalty if no paint)", false);
                    bldr2.addField("Motor", "F Class or lower, can use any number as long as impulse does not exceed 80 N*s", false);
                    bldr2.addField("Payload", "Three Raw Hen Eggs, 55 - 61 grams, 45 millimeters or less, must return with no external damage", false);
                    bldr2.addField("Parachute", "Portion containing egg and altimeter must separate and descend with at least 2 parachutes", false);
                    bldr2.addField("Flight Duration", "43 - 45 seconds, starting from lift-off", false);
                    bldr2.addField("Flight Altitude", "856 feet", false);
                    bldr2.addField("Flight Altitude according to Erin", "856 meters", false);

                    e.getChannel().sendMessage(bldr2.build()).complete();
                    break;

                case "imperial":
                    e.getChannel().sendMessage("打倒美帝国主义").complete();
                    break;

                case "paper3":
                    String[] tokens = text.split(" ");
                    if (tokens.length > 2) {
                        String msg = Arrays.stream(tokens).skip(2).collect(Collectors.joining(" "));
                        String prompttype = (tokens.length >= 2) ? tokens[1] : "ERROR";
                        for (Prompt prompt : prompts) {
                            if (prompt.isPrompt(prompttype)) {
                                e.getChannel().sendMessage("\"" + msg + "\" \n \n" + prompt.getPrompt()).complete();
                            }
                        }
                    }
                    break;

                case "hounds":
                    if (trusteds().contains(userId)) {
                        String[] tokens2 = text.split(" ");
                        List<User> mentionedUsers = message.getMentionedUsers();
                        int x = 0;
                        try {
                            x = Integer.parseInt(tokens2[1]);
                        } catch (Exception exception) {
                            e.getChannel().sendMessage("you dun fucked up now kiddo.").complete();
                        }
                        for (User user : mentionedUsers) {
                            String id = guild.getMember(user).getUser().getId();
                            if (id.equals(GLORIOUS_CREATOR)) {
                                e.getChannel().sendMessage("You dare release the hounds upon their creator? Foolish.").complete();
                            } else {
                                for (int i = 0; i < x; i++) {
                                    e.getChannel().sendMessage("<@" + id + "> ").complete();
                                }
                            }
                        }
                    }
                    break;

                case "say":
                    if (userId.equals(GLORIOUS_CREATOR)) {
                        String[] tokens3 = text.split(" ");

                        try {
                            message.delete().complete();
                        } catch (net.dv8tion.jda.core.exceptions.InsufficientPermissionException exception) {
                            e.getChannel().sendMessage("gimme permissions dumbass.").complete();
                        }

                        String msg = Arrays.stream(tokens3).skip(1).collect(Collectors.joining(" "));
                        e.getChannel().sendMessage(msg).complete();
                    }
                    break;
            }

        }

    }

}
