import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageRespond extends ListenerAdapter {

    private List<Prompt> prompts;
    private String design; // allows for design bufferedreader
    private static final String gloriousCreator = "191367988224458752";

    public MessageRespond() throws IOException { // reads file so I don't put our design folder online again
        BufferedReader rdr2 = new BufferedReader(new FileReader("designfolder.txt"));
        design = rdr2.readLine();

        String types[] = { "twe", "eval", "disc" };
        prompts = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            prompts.add(new Prompt(types[i]));
        }
    }

    public Set trusteds() { // bot permissions
        Set set = new HashSet<String>();

        set.add(gloriousCreator); // Zak
        set.add("323331293787979781"); // Erin
        set.add("502619027122946048"); // Eugenia
        set.add("318874242450063371"); // Aaryea

        return set;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) { // receives a message
        String text = e.getMessage().getContentStripped();
        String name = e.getAuthor().getName();
        String userId = e.getAuthor().getId();
        Message message = e.getMessage();
        Guild guild = e.getGuild();

        if (name.toLowerCase().equals("rocketbot")) {

        }

        else {
            if (text.toLowerCase().equals("r!help")) {
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
                bldr.addField("r!hounds <mentionedUser>", "Releases the hounds", false);

                e.getChannel().sendMessage(bldr.build()).complete();
            }

            if (text.toLowerCase().equals("r!rqdoc")) {
                e.getChannel().sendMessage("__**TARC Requirements Doc:**__ \nhttps://3384f12ld0l0tjlik1fcm68s-wpengine.netdna-ssl.com/wp-content/uploads/2018/08/Event-Rules-TARC-2019-FINAL.pdf").complete();
            }

            if (text.toLowerCase().equals("r!design")) {
                e.getChannel().sendMessage(design).complete();
            }

            if (text.toLowerCase().equals("r!openrocket")) {
                e.getChannel().sendMessage("http://openrocket.info/").complete();
            }

            if (text.toLowerCase().equals("r!specs")) {
                EmbedBuilder bldr = new EmbedBuilder();

                bldr.setAuthor("TARC Rocket Specifications", "https://rocketcontest.org/","https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504817600790921256/TARC_logo2_9-23-08_400x400.png");
                bldr.setColor(Color.blue);

                bldr.addField("Mass", "Cannot exceed 650 grams", false);
                bldr.addField("Length", "No less than 650 mm (25.6 inches)", false);
                bldr.addField("Paint", "In order to launch at finals, must be painted (5 point penalty if no paint)", false);
                bldr.addField("Motor", "F Class or lower, can use any number as long as impulse does not exceed 80 N*s", false);
                bldr.addField("Payload", "Three Raw Hen Eggs, 55 - 61 grams, 45 millimeters or less, must return with no external damage", false);
                bldr.addField("Parachute", "Portion containing egg and altimeter must separate and descend with at least 2 parachutes", false);
                bldr.addField("Flight Duration", "43 - 45 seconds, starting from lift-off", false);
                bldr.addField("Flight Altitude", "856 feet", false);
                bldr.addField("Flight Altitude according to Erin", "856 meters", false);

                e.getChannel().sendMessage(bldr.build()).complete();
            }

            if (text.toLowerCase().equals("r!imperial")) {
                e.getChannel().sendMessage("打倒美帝国主义").complete();
            }

            if (text.toLowerCase().equals("f")) {
                e.getChannel().sendMessage("https://cdn.discordapp.com/emojis/410317239859019776.gif?v=1").complete();
            }

            if (text.toLowerCase().startsWith("r!paper3")) {
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
            }

            if (trusteds().contains(userId)) {
                if (text.toLowerCase().startsWith("r!hounds")) {
                    String[] tokens =text.split(" ");
                    List<User> mentionedUsers = message.getMentionedUsers();
                    int x = Integer.parseInt(tokens[2]);
                    for (User user : mentionedUsers) {
                        Member member = guild.getMember(user);
                        String id = member.getUser().getId();
                        if (id.equals(gloriousCreator)) {
                            e.getChannel().sendMessage("You dare release the hounds upon their creator? Foolish.").complete();
                        } else {
                            for (int i = 0; i < x; i++) {
                                e.getChannel().sendMessage("<@" + id + ">").complete();
                            }
                        }
                    }
                }
            }
        }
    }

}
