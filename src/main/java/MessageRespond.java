import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import engine.Prompt;
import engine.Quote;
import engine.Task;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MessageRespond extends ListenerAdapter {

    private static final String GLORIOUS_CREATOR = "191367988224458752";
    private static final String PREFIX = "r!";
    private static final String TASK_PREFIX = "t!";
    private static final String TASK_FILE = "tasklist.json";

    private static File trustedfile = new File("trusteds.txt");

    private List<Quote> quotable;
    private List<Quote> notable;
    private List<Prompt> prompts;
    private List<Task> tasks;
    private String design; // allows for design BufferedReader
    private Set trusteds;

    private StackTraceElement[] recentError;
    private String recentErrorMsg;

    public MessageRespond() throws IOException { // reads file so I don't put our design folder online again
        readQuotes();
        readDesign();
        readPrompts();
        readTrusteds();
        readTasks();
        System.out.println("Read in " + trusteds.size() + " trusteds.");
    }

    private void readTasks() throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(TASK_FILE));
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        tasks = new Gson().fromJson(rdr, listType);

        if (tasks == null) {
            tasks = new ArrayList<>();
        }
    }

    private void writeTasks() throws IOException {
        GsonBuilder bldr = new GsonBuilder().setPrettyPrinting();
        Gson gson = bldr.create();

        FileWriter wrtr = new FileWriter(new File("tasklist.json"));
        wrtr.write(gson.toJson(tasks));
        wrtr.close();
    }

    private void readQuotes() throws IOException {
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

    private void readDesign() throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader("designfolder.txt"));
        design = rdr.readLine();
    }

    private void readPrompts() {
        String types[] = { "twe", "eval", "disc" };
        prompts = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            prompts.add(new Prompt(types[i]));
        }
    }

    private void readTrusteds() throws IOException {
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

                String temp1 = text.substring(0, 1).toUpperCase();
                String temp2 = text.substring(1).toLowerCase();
                text = temp1 + temp2;
                System.out.println(text);

                controller.setNickname(member, text).complete();
                controller.addSingleRoleToMember(member, guild.getRoleById(583056331049533456L)).complete();
                channel.sendMessage("**Thank you for verifying your name!**\n\nYou now have access to the rest of the Discord Server.\n\nHappy Rocketeering!").complete();
            } else {
                System.out.println("it didn't work " + text);
                channel.sendMessage("**Please give a valid name!**\n\n__**Example:**__\n``Zak``\n``Elon``\n``Stephen``").complete();
            }
        }

        if (!userId.equals("504527272061960193")) {
            if (text.toLowerCase().equals("f")  ) {
                e.getChannel().sendMessage("https://cdn.discordapp.com/emojis/410317239859019776.gif?v=1").complete();
            } else if (text.toLowerCase().equals("france") ||
                text.toLowerCase().equals("french")) {
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
        if
        (text.startsWith(TASK_PREFIX)) {
            Scanner messageInput = new Scanner(text.substring(PREFIX.length()));
            String command = messageInput.next();

            switch (command) {
                case "help":
                    taskHelp(e, userId);
                    break;

                case "create":
                    createTask(e, text, userId);
                    break;

                case "delete":
                    deleteTask(e, text, userId);
                    break;

                case "save":
                    save(e, userId);
                    break;

                case "list":
                    listTasks(e, userId);
                    break;

                default:
                    e.getChannel().sendMessage("Give a real command!").complete();
                    break;
            }
        } else if (text.startsWith(PREFIX)) {
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

                case "kill":
                    kill(e, userId);
                    break;

                case "errorinfo":
                    errorInfo(e, userId);
                    break;

                default:
                    e.getChannel().sendMessage("Give a real command!").complete();
                    break;
            }

        }

    }

    private void help(GuildMessageReceivedEvent e) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("rocketbot Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.red);

        bldr.addField("r!rqdoc", "TARC Requirements Document", false);
        bldr.addField("r!specs", "TARC Rocket Specifications", false);
        bldr.addField("r!openrocket", "Link to OpenRocket", false);
        bldr.addField("r!paper3 <twe, eval, disc> <prompt>", "Generate a Paper 3 engine.Prompt", false);
        bldr.addField("r!dates", "Important Dates", false);
        bldr.addField("r!quote <aaryea, else>", "Gives a random quote", false);
        bldr.addField("r!legacy <launchdata, info, design>", "Legacy Commands from the 2018-19 year", false);

        bldr.setFooter("Bot created and managed by Zak", null);

        e.getChannel().sendMessage(bldr.build()).complete();
    }

    private void rqdoc(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**TARC Requirements Doc:**__ \nhttps://3384f12ld0l0tjlik1fcm68s-wpengine.netdna-ssl.com/wp-content/uploads/2018/08/Event-Rules-TARC-2019-FINAL.pdf").complete();
    }

    private void openRocket(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("http://openrocket.info/").complete();
    }

    private void specs(GuildMessageReceivedEvent e) {
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

    private void imperial(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("打倒美帝国主义").complete();
    }

    private void paper3(GuildMessageReceivedEvent e, String text) {
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

    private void legacyDesign(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**Design**__\n" + design).complete();
    }

    private void legacyInfo(GuildMessageReceivedEvent e) {
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

    private void legacyLaunchData(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**Launch Data:**__\nhttps://docs.google.com/spreadsheets/d/1-AmNGxfYK_w8szDZ4_q8ZvgIo_il2fcmgZHNkt5BrVY/edit#gid=0").complete();
    }

    private void hounds(GuildMessageReceivedEvent e, String userId, String text, Message message, Guild guild) {
        if (trusteds.contains(userId)) {
            String[] tokens2 = text.split(" ");
            List<User> mentionedUsers = message.getMentionedUsers();
            int x = 0;
            try {
                x = Integer.parseInt(tokens2[1]);
            } catch (Exception exception) {
                recentError = exception.getStackTrace();
                recentErrorMsg = exception.getMessage();
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

    private void say(GuildMessageReceivedEvent e, String userId, String text, Message message) {
        if (userId.equals(GLORIOUS_CREATOR)) {
            String[] tokens3 = text.split(" ");

            try {
                message.delete().complete();
            } catch (InsufficientPermissionException exception) {
                recentError = exception.getStackTrace();
                recentErrorMsg = exception.getMessage();
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

    private void dates(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("__**Important Dates:**__\n" +
                "September 1, 2019 - Team Registration\n" +
                "April 6, 2020 - Flight Scores Due\n" +
                "May 16, 2020 - Nationals").complete();
    }

    private void quote(GuildMessageReceivedEvent e, Scanner tokens) {
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
            recentError = ex.getStackTrace();
            recentErrorMsg = ex.getMessage();
            e.getChannel().sendMessage("Give a name or else.").complete();
        }

    }

    private void adminHelp(GuildMessageReceivedEvent e, String userId) {
        if (trusteds.contains(userId)) {
            EmbedBuilder bldr = new EmbedBuilder();

            bldr.setAuthor("Admin Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setColor(Color.green);

            bldr.addField("r!hounds <number> <user>", "Releases the hounds on a specific user", false);
            bldr.addField("r!clear <number>", "Clears the specified number of messages", false);
            bldr.addField("r!kill", "Turns off the bot. Only use in the event of a massive break/bug", false);
            bldr.addField("t!help", "Gives help for Task commands", false);
            bldr.addField("r!errorinfo (Zak Only)", "Retrieves info of last thrown exception", false);

            bldr.setFooter("Bot created and managed by Zak", null);

            e.getChannel().sendMessage(bldr.build()).complete();
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void clear(GuildMessageReceivedEvent e, String userId, String text) {
        if (trusteds.contains(userId)) {
            TextChannel c = e.getChannel();
            MessageHistory history = new MessageHistory(c);
            List<Message> msgs;

            String[] tokens = text.split(" ");
            int temp = Integer.parseInt(tokens[1]);

            try {
                msgs = history.retrievePast(temp + 1).complete();
                c.deleteMessages(msgs).complete();
                e.getChannel().sendMessage(temp + " messages successfully deleted!").complete();
            } catch (IllegalArgumentException ex) {
                recentError = ex.getStackTrace();
                recentErrorMsg = ex.getMessage();
                e.getChannel().sendMessage("Please specify a value that is less than 100!").complete();
            }
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void kill(GuildMessageReceivedEvent e, String userId) {
        if (trusteds.contains(userId)) {
            e.getChannel().sendMessage("Goodbye, going offline now. Shut down by " + e.getAuthor().getAsMention()).complete();
            try {
                writeTasks();
            } catch (IOException ex) {
                System.out.println("fuck");
            }
            System.exit(-1);
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void errorInfo(GuildMessageReceivedEvent e, String userId) {
        if (userId.equals(GLORIOUS_CREATOR)) {
            e.getChannel().sendMessage(recentErrorMsg + "\n\n" + Arrays.toString(recentError)).complete();
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void save(GuildMessageReceivedEvent e, String userId) {
        if (trusteds.contains(userId)) {
            try {
                writeTasks();
                e.getChannel().sendMessage("Tasks successfully saved!").complete();
            } catch (IOException ex) {
                recentError = ex.getStackTrace();
                recentErrorMsg = ex.getMessage();
                e.getChannel().sendMessage("Something went wrong, tasks not saved.").complete();
            }
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void listTasks(GuildMessageReceivedEvent e, String userId) {
        if (trusteds.contains(userId)) {
            if (tasks.isEmpty()) {
                e.getChannel().sendMessage("No tasks are currently saved.").complete();
            } else {
                EmbedBuilder bldr = new EmbedBuilder();

                bldr.setAuthor("Task List", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                bldr.setColor(Color.magenta);

                for (Task t : tasks) {
                    bldr.addField("Task: " + t.getTaskName(), "Responsible: " + t.getResponsible() + ", Priority: " + t.getPriority(),false);
                }

                bldr.setFooter("Bot created and managed by Zak", null);

                e.getChannel().sendMessage(bldr.build()).complete();
            }
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void taskHelp(GuildMessageReceivedEvent e, String userId) {
        if (trusteds.contains(userId)) {
            EmbedBuilder bldr = new EmbedBuilder();

            bldr.setAuthor("Task Help", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setColor(Color.cyan);

            bldr.addField("t!create <responsible person, priority factor, task name>", "Creates a task", false);
            bldr.addField("t!delete <task name>", "Deletes a task from the list", false);
            bldr.addField("t!save", "Saves all tasks to the storage file", false);
            bldr.addField("t!list", "Lists all current stored tasks", false);

            bldr.addBlankField(false);

            bldr.addField("Priority Factors:", "a, w, m, e, asap, week, month, eventually", false);

            bldr.setFooter("Bot created and managed by Zak", null);

            e.getChannel().sendMessage(bldr.build()).complete();
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void createTask(GuildMessageReceivedEvent e, String text, String userId) {
        boolean success = false;
        if (trusteds.contains(userId)) {
            String[] tokens = text.split(" ");
            String name = Arrays.stream(tokens).skip(3).collect(Collectors.joining(" "));
            String responsible = tokens[1];
            String priority = tokens[2];

            System.out.println("n: " + name);
            System.out.println("r: " + responsible);
            System.out.println("p: " + priority);
            try {
                tasks.add(new Task(name, responsible, priority));
                success = true;
            } catch (IllegalArgumentException ex) {
                recentError = ex.getStackTrace();
                recentErrorMsg = ex.getMessage();
                e.getChannel().sendMessage("Error: Task not saved.").complete();
            }
            if (success) {
                e.getChannel().sendMessage("Task successfully saved!").complete();
            }
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void deleteTask(GuildMessageReceivedEvent e, String text, String userId) {
        String[] tokens = text.split(" ");
        String name = Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "));
        boolean success = false;
        if (trusteds.contains(userId)) {
            for (Task t : tasks) {
                if (t.isMe(name)) {
                    tasks.remove(t);
                    success = true;
                    break;
                }
            }
            if (!success) {
                e.getChannel().sendMessage("Task did not exist!").complete();
            } else {
                e.getChannel().sendMessage("Task successfully removed!").complete();
            }
        } else {
            e.getChannel().sendMessage("You do not wield enough power for this command!").complete();
        }
    }

}
