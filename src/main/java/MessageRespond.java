import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRespond extends ListenerAdapter {

    private List<Quote> quotable;
    private List<Quote> notable;
    private List<Prompt> prompts;
    private String design; // allows for design BufferedReader
    private static final String GLORIOUS_CREATOR = "191367988224458752";
    private static final String PREFIX = "r!";
    private Set trusteds;
    private static File trustedfile = new File("trusteds.txt");

    public MessageRespond() throws IOException { // reads file so I don't put our design folder online again
        readQuotes();
        readDesign();
        readPrompts();
        readTrusteds();
        System.out.println("Read in " + trusteds.size() + " trusteds.");
    }

    public void readQuotes() throws IOException {
        String[] notablenames = { "Aaryea" };
        notable = new ArrayList<>();
        for (int i = 0; i < notablenames.length; i++) {
            notable.add(new Quote(notablenames[i], "quotes" + notablenames[i].toLowerCase() + ".txt"));
        }

        String[] names = { "Erin", "Zak", "Eugenia", "Jason" };
        quotable = new ArrayList<>();
        for (int i = 0; i < names.length ; i++) {
            quotable.add(new Quote(names[i], "quotes" + names[i].toLowerCase() + ".txt"));
        }

    }

    public void readDesign() throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader("designfolder.txt"));
        design = rdr.readLine();
    }

    public void readPrompts() {
        String types[] = { "twe", "eval", "disc" };
        prompts = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            prompts.add(new Prompt(types[i]));
        }
    }

    public void readTrusteds() throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(trustedfile));
        trusteds = new HashSet<String>();
        String line;
        while ((line = rdr.readLine()) != null) {
            trusteds.add(line.trim());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) { // receives a message
        User user = e.getAuthor();
        Member member = e.getMember();
        String text = e.getMessage().getContentStripped();
        String userId = user.getId();
        Message message = e.getMessage();
        Guild guild = e.getGuild();
        GuildController controller = guild.getController();

        if (e.getChannel().getName().equals("give-name")) {
            PrivateChannel channel = user.openPrivateChannel().complete();
            message.delete().complete();
            if (!text.contains(" ")) {
                System.out.println("it worked " + text);

                String temp1 = text.substring(0, 0).toUpperCase();
                String temp2 = text.substring(1).toLowerCase();
                text = temp1 + temp2;

                controller.setNickname(member, text).complete();
                controller.addSingleRoleToMember(member, guild.getRoleById(583056331049533456L)).complete();
                channel.sendMessage("**Thank you for verifying your name!**\n\nYou now have access to the rest of the Discord Server.").complete();
            } else {
                System.out.println("it didn't work " + text);
                channel.sendMessage("**Please give a valid name!**\n\n__**Example:**__\n``Zak``\n``Elon``\n``Stephen``").complete();
            }
        }

        if (!userId.equals("504527272061960193")) {
            if (text.toLowerCase().equals("f")  ) {
                e.getChannel().sendMessage("https://cdn.discordapp.com/emojis/410317239859019776.gif?v=1").complete();
            } else if (text.toLowerCase().contains("france") ||
                text.toLowerCase().contains("french")) {
                e.getChannel().sendMessage("*surrender monkeys").complete();
            }
        }

        if (userId.equals(GLORIOUS_CREATOR)) {
            if (text.equalsIgnoreCase("hello bot")) {
                e.getChannel().sendMessage("hello master").complete();
            } else if (text.equalsIgnoreCase("good bot")) {
                e.getChannel().sendMessage("danke master").complete();
            } else if (text.equalsIgnoreCase("good night bot")) {
                e.getChannel().sendMessage("good nigh- no wait don't turn me off").complete();
            } else if (text.equalsIgnoreCase("i'm so great")) {
                e.getChannel().sendMessage("this is true master").complete();
            } else if (text.equalsIgnoreCase("good morning bot")) {
                e.getChannel().sendMessage("good morning master").complete();
            } else if (text.equalsIgnoreCase("bad bot")) {
                e.getChannel().sendMessage("sorry master").complete();
            } else if (text.equalsIgnoreCase("isn't that right bot")) {
                e.getChannel().sendMessage("yes master").complete();
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
                    switch (legacy) {
                        case "launchdata":
                            legacyLaunchData(e);
                            break;

                        case "info":
                            legacyInfo(e);
                            break;

                        case "design":
                            legacyDesign(e);
                            break;

                        default:
                            e.getChannel().sendMessage("Please give a valid legacy command!").complete();
                            break;
                    }
                    break;

                case "hounds":
                    hounds(e, userId, text, message, guild);
                    break;

                case "say":
                    say(e, userId, text, message);
                    break;

                case "dates":
                    dates(e);
                    break;

                case "quote":
                    quote(e, messageInput);
                    break;

                case "adminhelp":
                    adminHelp(e, userId);
                    break;

                case "clear":
                    clear(e, userId, text);
                    break;

                default:
                    e.getChannel().sendMessage("Give a real command!").complete();
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
        bldr.addField("r!openrocket", "Link to OpenRocket", false);
        bldr.addField("r!paper3 <twe, eval, disc> <prompt>", "Generate a Paper 3 Prompt", false);
        bldr.addField("r!dates", "Important Dates", false);
        bldr.addField("r!quote <aaryea, else>", "Gives a random quote", false);
        bldr.addField("r!legacy <launchdata, info, design>", "Legacy Commands from the 2018-19 year", false);

        bldr.setFooter("Bot created and managed by Zak", null);

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
        bldr.addField("Payload", "Contain one egg of 55 to 61 grams of weight and a diameter less than 45 mm, must return with no external damage", false);
        bldr.addField("Parachute", "Portion containing egg and altimeter must separate and descend with at least 2 parachutes", false);
        bldr.addField("Flight Duration", "40 - 43 seconds, starting from lift-off", false);
        bldr.addField("Flight Altitude", "800 feet", false);
        bldr.addField("Flight Altitude according to Erin", "856 meters", false);
        bldr.addField("Scoring", "Distance from target height + 4 x Distance from target time = points", false);

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
        e.getChannel().sendMessage("__**Design**__\n" + design).complete();
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
        } else {
            e.getChannel().sendMessage("You do not wield the power to release the hounds!").complete();
        }
    }

    public void say(GuildMessageReceivedEvent e, String userId, String text, Message message) {
        if (userId.equals(GLORIOUS_CREATOR)) {
            String[] tokens3 = text.split(" ");

            try {
                message.delete().complete();
            } catch (InsufficientPermissionException exception) {
                e.getChannel().sendMessage("gimme permissions dumbass.").complete();
            }

            String msg = Arrays.stream(tokens3).skip(1).collect(Collectors.joining(" "));
            e.getChannel().sendMessage(msg).complete();
        } else {
            for (int i = 0; i < 5; i++) {
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " don't do that again").complete();
            }
        }
    }

    public void dates(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**Important Dates:**__\n" +
                "September 1, 2019 - Team Registration\n" +
                "April 6, 2020 - Flight Scores Due\n" +
                "May 16, 2020 - Nationals").complete();
    }

    public void quote(GuildMessageReceivedEvent e, Scanner tokens) {
        boolean success = false;
        try {
            String name = tokens.next();
            if (name.equalsIgnoreCase("else")) {
                Random random = new Random();
                int index = random.nextInt(4);
                Quote quote = quotable.get(index);
                e.getChannel().sendMessage("\"" + quote.getQuote() + "\" -" + quote.getName()).complete();
                success = true;
            } else {
                for (Quote quote : notable) {
                    if (quote.isMe(name)) {
                        e.getChannel().sendMessage("\"" + quote.getQuote() + "\" -" + quote.getName()).complete();
                        success = true;
                    }
                }
            }
            if (!success) {
                e.getChannel().sendMessage("Give a real name.").complete();
            }
        } catch (NoSuchElementException ex) {
            e.getChannel().sendMessage("Give a name or else.").complete();
        }

    }

    public void adminHelp(GuildMessageReceivedEvent e, String userId) {
        if (trusteds.contains(userId)) {
            EmbedBuilder bldr = new EmbedBuilder();

            bldr.setAuthor("Admin Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setColor(Color.green);

            bldr.addField("r!hounds <number> <user>", "Releases the hounds on a specific user", false);
            bldr.addField("r!clear <number>", "Clears the specified number of messages", false);

            bldr.setFooter("Bot created and managed by Zak", null);

            e.getChannel().sendMessage(bldr.build()).complete();
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    public void clear(GuildMessageReceivedEvent e, String userId, String text) {
        if (trusteds.contains(userId)) {
            e.getMessage().delete().complete();
            TextChannel c = e.getChannel();
            MessageHistory history = new MessageHistory(c);
            List<Message> msgs;

            String[] tokens = text.split(" ");
            int temp = Integer.parseInt(tokens[1]);

            if (temp > 100) {
                e.getChannel().sendMessage("Please specify a value that is less than 100!").complete();
            } else {
                for (int i = 0; i < temp; i++) {
                    msgs = history.retrievePast(1).complete();
                    msgs.get(0).delete().complete();
                }
            }
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

}
