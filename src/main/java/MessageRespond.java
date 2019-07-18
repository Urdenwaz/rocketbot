import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRespond extends ListenerAdapter {

    private List<Prompt> prompts;
    private String design; // allows for design BufferedReader
    private static final String GLORIOUS_CREATOR = "191367988224458752";
    private static final String PREFIX = "r!";
    private Set trusteds;
    private static File trustedfile = new File("trusteds.txt");

    public MessageRespond() throws IOException { // reads file so I don't put our design folder online again
        BufferedReader rdr = new BufferedReader(new FileReader("designfolder.txt"));
        design = rdr.readLine();

        String types[] = { "twe", "eval", "disc" };
        prompts = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            prompts.add(new Prompt(types[i]));
        }

        BufferedReader rdr2 = new BufferedReader(new FileReader(trustedfile));
        trusteds = new HashSet<String>();
        String line;
        while ((line = rdr2.readLine()) != null) {
            trusteds.add(line.trim());
        }
        System.out.println("Read in " + trusteds.size() + " trusteds.");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) { // receives a message
        String text = e.getMessage().getContentStripped();
        String name = e.getAuthor().getName();
        String userId = e.getAuthor().getId();
        Message message = e.getMessage();
        Guild guild = e.getGuild();

        if (!userId.equals("504527272061960193")) {
            if (text.toLowerCase().equals("f")  ) {
                e.getChannel().sendMessage("https://cdn.discordapp.com/emojis/410317239859019776.gif?v=1").complete();
            }
        }

        if (text.startsWith(PREFIX)) {
            Scanner messageInput = new Scanner(text.substring(PREFIX.length()));
            String command = messageInput.next();

            switch (command) {
                case "help":
                    help(e);
                    break;

                case "rqdoc":
                    rqdoc(e);
                    break;

                case "openrocket":
                    openRocket(e);
                    break;

                case "specs":
                    specs(e);
                    break;

                case "imperial":
                    imperial(e);
                    break;

                case "paper3":
                    paper3(e, text);
                    break;

                case "legacy":
                    String legacy = messageInput.next();
                    if (legacy.equals("launchdata")) {
                        legacyLaunchData(e);
                    } else if (legacy.equals("info")) {
                        legacyInfo(e);
                    } else if (legacy.equals("design")) {
                        legacyDesign(e);
                    }
                    break;

                case "hounds":
                    hounds(e, userId, text, message, guild);
                    break;

                case "say":
                    say(e, userId, text, message);
                    break;
            }

        }

    }

    public void help(GuildMessageReceivedEvent e) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("rocketbot Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.red);

        bldr.addField("r!rqdoc", "TARC Requirements Document", false);
        bldr.addField("r!specs", "TARC Rocket Specifications", false);
        bldr.addField("r!design", "Link to Current OpenRocket Design", false);
        bldr.addField("r!openrocket", "Link to OpenRocket", false);
        bldr.addField("r!paper3 <twe, eval, disc> <prompt>", "Generate a Paper 3 Prompt", false);
        bldr.addField("r!legacy <launchdata/info/design>", "Legacy Commands from the 2018-19 year", false);
        bldr.addBlankField(false);
        bldr.addField("People who Zak decides only:", "(Erin, Eugenia, Zak, Aaryea)", false);
        bldr.addField("r!hounds <number> <mentionedUser>", "Releases the hounds", false);

        bldr.setFooter("Bot created and managed by @Urdenwaz#5217", null);

        e.getChannel().sendMessage(bldr.build()).complete();
    }

    public void rqdoc(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**TARC Requirements Doc:**__ \nhttps://3384f12ld0l0tjlik1fcm68s-wpengine.netdna-ssl.com/wp-content/uploads/2018/08/Event-Rules-TARC-2019-FINAL.pdf").complete();
    }

    public void openRocket(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("http://openrocket.info/").complete();
    }

    public void specs(GuildMessageReceivedEvent e) {
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

        bldr.setFooter("Bot created and managed by @Urdenwaz#5217", null);

        e.getChannel().sendMessage(bldr.build()).complete();
    }

    public void imperial(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("打倒美帝国主义").complete();
    }

    public void paper3(GuildMessageReceivedEvent e, String text) {
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

    public void legacyDesign(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage(design).complete();
    }

    public void legacyInfo(GuildMessageReceivedEvent e) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Team Info", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/540934268021506097/IMG_9113-best-waffle-recipe-square.png");
        bldr.setColor(Color.ORANGE);

        bldr.addField("Team Name:", "Belguim", false);
        bldr.addField("Rocket Name:", "(Luft)Waffle", false);
        bldr.addField("Paint Job:", "Waffle Pattern + Color", false);

        bldr.setFooter("Bot created and managed by @Urdenwaz#5217", null);

        e.getChannel().sendMessage(bldr.build()).complete();
    }

    public void legacyLaunchData(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**Launch Data:**__\nhttps://docs.google.com/spreadsheets/d/1-AmNGxfYK_w8szDZ4_q8ZvgIo_il2fcmgZHNkt5BrVY/edit#gid=0").complete();
    }

    public void hounds(GuildMessageReceivedEvent e, String userId, String text, Message message, Guild guild) {
        if (trusteds.contains(userId)) {
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
    }

    public void say(GuildMessageReceivedEvent e, String userId, String text, Message message) {
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
    }

}
