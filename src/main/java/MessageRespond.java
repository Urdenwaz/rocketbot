import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import engine.Prompt;
import engine.Purchase;
import engine.Quote;
import engine.Task;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageRespond extends ListenerAdapter {

    private static final String GLORIOUS_CREATOR = "191367988224458752";
    private static final String TASK_FILE = "tasklist.json";
    private static final String PURCHASE_FILE = "purchaselist.json";

    private static final String PREFIX = "r!";
    private static final String ADMIN_PREFIX = "a!";
    private static final String TASK_PREFIX = "t!";
    private static final String PURCHASE_PREFIX = "p!";

    private List<Quote> quotable;
    private List<Quote> notable;
    private List<Prompt> prompts;
    private List<Task> tasks;
    private List<Purchase> purchases;

    private String design; // allows for design BufferedReader
    private Set trusteds;

    public Guild server;
    public TextChannel sayChannel;

    private StackTraceElement[] recentError;
    private String recentErrorMsg;

    public MessageRespond() throws IOException { // reads file so I don't put our design folder online again
        readQuotes();
        readDesign();
        readPrompts();
        readTrusteds();
        readTasks();
        readPurchases();
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

        FileWriter wrtr = new FileWriter(new File(TASK_FILE));
        wrtr.write(gson.toJson(tasks));
        wrtr.close();

        System.out.println("Tasks successfully saved.");
    }

    private void readPurchases() throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(PURCHASE_FILE));
        Type listType = new TypeToken<ArrayList<Purchase>>(){}.getType();
        purchases = new Gson().fromJson(rdr, listType);

        if (purchases == null) {
            purchases = new ArrayList<>();
        }
    }

    private void writePurchases() throws IOException {
        GsonBuilder bldr = new GsonBuilder().setPrettyPrinting();
        Gson gson = bldr.create();

        FileWriter wrtr = new FileWriter(new File(PURCHASE_FILE));
        wrtr.write(gson.toJson(purchases));
        wrtr.close();

        System.out.println("Purchases successfully saved.");
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
        BufferedReader rdr = new BufferedReader(new FileReader("trusteds.txt"));
        trusteds = new HashSet<String>();
        String line;
        while ((line = rdr.readLine()) != null) {
            trusteds.add(line.trim());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String userId = e.getAuthor().getId();
        String text = e.getMessage().getContentRaw();
        if (userId.equals(GLORIOUS_CREATOR)) {
            Scanner scan = new Scanner(text);
            String command = scan.next();
            if (command.equalsIgnoreCase("cch ")) {
                String channelId = scan.next();
                sayChannel = server.getTextChannelById(channelId);
            } else if (command.equalsIgnoreCase("say")) {
                String[] tokens = text.split(" ");
                sayChannel.sendMessage(Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "))).complete();
            }
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
        TextChannel channel = e.getChannel();

        server = e.getGuild();

        if (channel.getName().equals("give-name")) {
            PrivateChannel pChannel = user.openPrivateChannel().complete();
            message.delete().complete();
            if (!text.contains(" ")) {
                System.out.println("it worked " + text);

                String temp1 = text.substring(0, 1).toUpperCase();
                String temp2 = text.substring(1).toLowerCase();
                text = temp1 + temp2;
                System.out.println(text);

                controller.setNickname(member, text).complete();
                controller.addSingleRoleToMember(member, guild.getRoleById(583056331049533456L)).complete();
                pChannel.sendMessage("**Thank you for verifying your name!**\n\nYou now have access to the rest of the Discord Server.\n\nHappy Rocketeering!").complete();
            } else {
                System.out.println("it didn't work " + text);
                pChannel.sendMessage("**Please give a valid name!**\n\n__**Example:**__\n``Zak``\n``Elon``\n``Stephen``").complete();
            }
        }

        if (!userId.equals("504527272061960193")) {
            if (text.toLowerCase().equals("f")  ) {
                channel.sendMessage("https://cdn.discordapp.com/emojis/410317239859019776.gif?v=1").complete();
            } else if (text.toLowerCase().equals("france") ||
                text.toLowerCase().equals("french")) {
                channel.sendMessage("*surrender monkeys").complete();
            }
        }

        if (userId.equals(GLORIOUS_CREATOR)) {
            if (text.equalsIgnoreCase("hello bot")) {
                channel.sendMessage("hello master").complete();
            } else if (text.equalsIgnoreCase("good bot")) {
                channel.sendMessage("danke master").complete();
            } else if (text.equalsIgnoreCase("good night bot")) {
                channel.sendMessage("good nigh- no wait don't turn me off").complete();
            } else if (text.equalsIgnoreCase("i'm so great")) {
                channel.sendMessage("this is true master").complete();
            } else if (text.equalsIgnoreCase("good morning bot")) {
                channel.sendMessage("good morning master").complete();
            } else if (text.equalsIgnoreCase("bad bot")) {
                channel.sendMessage("sorry master").complete();
            } else if (text.equalsIgnoreCase("isn't that right bot")) {
                channel.sendMessage("yes master").complete();
            }
        }
        if (text.startsWith(TASK_PREFIX)) {
            if (trusteds.contains(userId)) {
                Scanner messageInput = new Scanner(text.substring(TASK_PREFIX.length()));
                String command = messageInput.next();

                switch (command) {
                    case "help":
                        taskHelp(channel);
                        break;

                    case "create":
                        createTask(channel, text, guild);
                        break;

                    case "delete":
                        deleteTask(channel, text, guild);
                        break;

                    case "save":
                        taskSave(channel, userId);
                        break;

                    case "list":
                        listTasks(channel);
                        break;

                    case "info":
                        taskInfo(channel, text);
                        break;

                    case "reload":
                        updateTaskList(guild);
                        channel.sendMessage("Task List successfully reloaded!").complete();
                        break;

                    case "remind":
                        remind(channel, text, message, guild);
                        break;

                    default:
                        channel.sendMessage("Give a real command!").complete();
                        break;

                }
            } else {
                channel.sendMessage("You do not wield enough power for this command!").complete();
            }
        } else if (text.startsWith(PURCHASE_PREFIX)) {
            if (trusteds.contains(userId)) {
                Scanner messageInput = new Scanner(text.substring(PURCHASE_PREFIX.length()));
                String command = messageInput.next();

                switch (command) {
                    case "help":
                        purchaseHelp(channel);
                        break;

                    case "save":
                        purchaseSave(channel);
                        break;

                    case "info":
                        purchaseInfo(channel, text);
                        break;

                    case "reload":
                        updatePurchaseList(guild);
                        channel.sendMessage("Purchase List successfully updated!").complete();
                        break;

                    case "add":
                        addPurchase(channel, text, guild);
                        break;

                    case "remove":
                        deletePurchase(channel, text, guild);
                        break;

                    default:
                        channel.sendMessage("Give a real command!").complete();
                        break;
                }
            } else {
                channel.sendMessage("You do not wield enough power for this command!").complete();
            }
        } else if (text.startsWith(ADMIN_PREFIX)) {
            Scanner messageInput = new Scanner(text.substring(ADMIN_PREFIX.length()));
            String command = messageInput.next();

            switch (command) {
                case "adminhelp":
                    adminHelp(channel, userId);
                    break;

                case "say":
                    say(channel, user, text, message);
                    break;

                case "clear":
                    clear(channel, text);
                    break;

                case "kill":
                    kill(channel, user);
                    break;

                case "hounds":
                    hounds(channel, text, message, guild);
                    break;

                case "errorinfo":
                    errorInfo(channel, userId);
                    break;

                default:
                    channel.sendMessage("Give a real command!").complete();
                    break;
            }
        } else if (text.startsWith(PREFIX)) {
            Scanner messageInput = new Scanner(text.substring(PREFIX.length()));
            String command = messageInput.next();

            switch (command) {
                case "help":
                    help(channel);
                    break;

                case "rqdoc":
                    rqdoc(channel);
                    break;

                case "openrocket":
                    openRocket(channel);
                    break;

                case "specs":
                    specs(channel);
                    break;

                case "imperial":
                    imperial(channel);
                    break;

                case "paper3":
                    paper3(channel, text);
                    break;

                case "legacy":
                    String legacy = messageInput.next();
                    switch (legacy) {
                        case "launchdata":
                            legacyLaunchData(channel);
                            break;

                        case "info":
                            legacyInfo(channel);
                            break;

                        case "design":
                            legacyDesign(channel);
                            break;

                        default:
                            channel.sendMessage("Please give a valid legacy command!").complete();
                            break;
                    }
                    break;

                case "dates":
                    dates(channel);
                    break;

                case "quote":
                    quote(channel, messageInput);
                    break;

                default:
                    channel.sendMessage("Give a real command!").complete();
                    break;
            }

        }

    }

    private void help(TextChannel channel) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("rocketbot Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.red);

        bldr.addField("r!rqdoc", "TARC Requirements Document", false);
        bldr.addField("r!specs", "TARC Rocket Specifications", false);
        bldr.addField("r!openrocket", "Link to OpenRocket", false);
        bldr.addField("r!paper3 <twchannel, eval, disc> <prompt>", "Generate a Paper 3 Prompt", false);
        bldr.addField("r!dates", "Important Dates", false);
        bldr.addField("r!quote <aaryea, else>", "Gives a random quote", false);
        bldr.addField("r!legacy <launchdata, info, design>", "Legacy Commands from the 2018-19 year", false);

        bldr.setFooter("Bot created and managed by Zak", null);

        channel.sendMessage(bldr.build()).complete();
    }

    private void rqdoc(TextChannel channel) {
        channel.sendMessage("__**TARC Requirements Doc:**__ \nhttps://rocketcontest.org/wp-content/uploads/2019/05/Event-Rules-TARC-2020-Final-as-of-18-May-2019.doc.pdf").complete();
    }

    private void openRocket(TextChannel channel) {
        channel.sendMessage("http://openrocket.info/").complete();
    }

    private void specs(TextChannel channel) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("TARC Rocket Specifications", "https://rocketcontest.org/","https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504817600790921256/TARC_logo2_9-23-08_400x400.png");
        bldr.setColor(Color.red);

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

        bldr.setFooter("Bot created and managed by Zak", null);

        channel.sendMessage(bldr.build()).complete();
    }

    private void imperial(TextChannel channel) {
        channel.sendMessage("打倒美帝国主义").complete();
    }

    private void paper3(TextChannel channel, String text) {
        String[] tokens = text.split(" ");
        if (tokens.length > 2) {
            String msg = Arrays.stream(tokens).skip(2).collect(Collectors.joining(" "));
            String prompttype = (tokens.length >= 2) ? tokens[1] : "ERROR";
            for (Prompt prompt : prompts) {
                if (prompt.isPrompt(prompttype)) {
                    channel.sendMessage("\"" + msg + "\" \n \n" + prompt.getPrompt()).complete();
                }
            }
        }
    }

    private void legacyDesign(TextChannel channel) {
        channel.sendMessage("__**Design**__\n" + design).complete();
    }

    private void legacyInfo(TextChannel channel) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Team Info", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/540934268021506097/IMG_9113-best-waffle-recipe-square.png");
        bldr.setColor(Color.cyan);

        bldr.addField("Team Name:", "Belguim", false);
        bldr.addField("Rocket Name:", "(Luft)Waffle", false);
        bldr.addField("Paint Job:", "Waffle Pattern + Color", false);

        bldr.setFooter("Bot created and managed by @Urdenwaz#5217", null);

        channel.sendMessage(bldr.build()).complete();
    }

    private void legacyLaunchData(TextChannel channel) {
        channel.sendMessage("__**Launch Data:**__\nhttps://docs.google.com/spreadsheets/d/1-AmNGxfYK_w8szDZ4_q8ZvgIo_il2fcmgZHNkt5BrVY/edit#gid=0").complete();
    }

    private void hounds(TextChannel channel, String text, Message message, Guild guild) {
        String[] tokens = text.split(" ");
        List<User> mentionedUsers = message.getMentionedUsers();
        int x = 0;
        try {
            x = Integer.parseInt(tokens[1]);
        } catch (Exception exception) {
            recentError = exception.getStackTrace();
            recentErrorMsg = exception.getMessage();
            channel.sendMessage("you dun fucked up now kiddo.").complete();
        }
        for (User user : mentionedUsers) {
            String id = guild.getMember(user).getUser().getId();
            if (id.equals(GLORIOUS_CREATOR)) {
                channel.sendMessage("You dare release the hounds upon their creator? Foolish.").complete();
            } else {
                for (int i = 0; i < x; i++) {
                    channel.sendMessage("<@" + id + "> ").complete();
                }
            }
        }
    }

    private void say(TextChannel channel, User user, String text, Message message) {
        if (user.getId().equals(GLORIOUS_CREATOR)) {
            String[] tokens = text.split(" ");

            int numMentions = 0;
            if (text.contains("@")) {
                List<User> users = message.getMentionedUsers();
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].contains("@")) {
                        tokens[i] = users.get(numMentions).getAsMention();
                        numMentions++;
                    }
                }
            }

            try {
                message.delete().complete();
            } catch (InsufficientPermissionException exception) {
                recentError = exception.getStackTrace();
                recentErrorMsg = exception.getMessage();
                channel.sendMessage("gimme permissions dumbass.").complete();
            }

            String msg = Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "));
            channel.sendMessage(msg).complete();
        } else {
            for (int i = 0; i < 5; i++) {
                channel.sendMessage(user.getAsMention() + " don't do that again").complete();
            }
        }
    }

    private void dates(TextChannel channel) {
        channel.sendMessage("__**Important Dates:**__\n" +
                "September 1, 2019 - Team Registration\n" +
                "April 6, 2020 - Flight Scores Due\n" +
                "May 16, 2020 - Nationals").complete();
    }

    private void quote(TextChannel channel, Scanner tokens) {
        boolean success = false;
        try {
            String name = tokens.next();
            if (name.equalsIgnoreCase("else")) {
                Random random = new Random();
                int index = random.nextInt(4);
                Quote quote = quotable.get(index);
                channel.sendMessage("\"" + quote.getQuote() + "\" -" + quote.getName()).complete();
                success = true;
            } else {
                for (Quote quote : notable) {
                    if (quote.isMe(name)) {
                        channel.sendMessage("\"" + quote.getQuote() + "\" -" + quote.getName()).complete();
                        success = true;
                    }
                }
            }
            if (!success) {
                channel.sendMessage("Give a real name.").complete();
            }
        } catch (NoSuchElementException ex) {
            recentError = ex.getStackTrace();
            recentErrorMsg = ex.getMessage();
            channel.sendMessage("Give a name or else.").complete();
        }

    }

    private void adminHelp(TextChannel channel, String userId) {
        if (trusteds.contains(userId)) {
            EmbedBuilder bldr = new EmbedBuilder();

            bldr.setAuthor("Admin Commands", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setColor(Color.green);

            bldr.addField("a!hounds <number> <user>", "Releases the hounds on a specific user", false);
            bldr.addField("a!clear <number>", "Clears the specified number of messages", false);
            bldr.addField("a!kill", "Turns off the bot. Only use in the event of a massive break/bug", false);
            bldr.addField("t!help", "Gives help for Task commands", false);
            bldr.addField("p!help", "Gives help for Purchase commands", false);
            bldr.addField("a!errorinfo (Zak Only)", "Retrieves info of last thrown exception", false);

            bldr.setFooter("Bot created and managed by Zak", null);

            channel.sendMessage(bldr.build()).complete();
        } else {
            channel.sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void clear(TextChannel channel, String text) {
        MessageHistory history = new MessageHistory(channel);
        List<Message> msgs;

        String[] tokens = text.split(" ");
        int temp = Integer.parseInt(tokens[1]);

        try {
            msgs = history.retrievePast(temp + 1).complete();
            channel.deleteMessages(msgs).complete();
            channel.sendMessage(temp + " messages successfully deleted!").complete();
        } catch (IllegalArgumentException ex) {
            recentError = ex.getStackTrace();
            recentErrorMsg = ex.getMessage();
            channel.sendMessage("Please specify a value that is less than 100!").complete();
        }
    }

    private void kill(TextChannel channel, User user) {
        if (trusteds.contains(user.getId())) {
            channel.sendMessage("Goodbye, going offline now. Shut down by " + user.getAsMention()).complete();
            try {
                writeTasks();
                writePurchases();
                System.exit(-1);
            } catch (IOException ex) {
                channel.sendMessage("oh fuck").complete();
                System.out.println("oh fuck");
            }
        } else {
            channel.sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void errorInfo(TextChannel channel, String userId) {
        if (userId.equals(GLORIOUS_CREATOR)) {
            channel.sendMessage(recentErrorMsg + "\n\n" + Arrays.toString(recentError)).complete();
        } else {
            channel.sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void taskSave(TextChannel channel, String userId) {
        if (trusteds.contains(userId)) {
            try {
                writeTasks();
                channel.sendMessage("Tasks successfully saved!").complete();
            } catch (IOException ex) {
                recentError = ex.getStackTrace();
                recentErrorMsg = ex.getMessage();
                channel.sendMessage("Something went wrong, tasks not saved.").complete();
            }
        } else {
            channel.sendMessage("You do not wield enough power for this command!").complete();
        }
    }

    private void listTasks(TextChannel channel) {
        if (tasks.isEmpty()) {
            channel.sendMessage("No tasks are currently saved.").complete();
        } else {
            EmbedBuilder bldr = new EmbedBuilder();

            bldr.setAuthor("Task List", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
            bldr.setColor(Color.blue);

            for (Task t : tasks) {
                bldr.addField("Task: " + t.getTaskName(), "Responsible: " + t.getResponsible() + "\nPriority: " + t.getPriority(),false);
            }

            bldr.setFooter("Bot created and managed by Zak", null);

            channel.sendMessage(bldr.build()).complete();
        }
    }

    private void taskHelp(TextChannel channel) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Task Help", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.blue);

        bldr.addField("t!create <responsible person, priority factor, task name>", "Creates a task", false);
        bldr.addField("t!delete <task name> or <number>", "Deletes a task from the list", false);
        bldr.addField("t!save", "Saves all tasks to the storage file", false);
        bldr.addField("t!list", "Lists all current stored tasks", false);
        bldr.addField("t!info <task name>", "Displays info about requested task", false);
        bldr.addField("t!reload", "Reloads the Task List", false);
        bldr.addField("t!remind <priority factor> <user mention>", "Reminds a user of their tasks", false);

        bldr.addBlankField(false);

        bldr.addField("Priority Factors:", "a, w, m, channel, asap, week, month, eventually", false);

        bldr.setFooter("Bot created and managed by Zak", null);

        channel.sendMessage(bldr.build()).complete();
    }

    private void createTask(TextChannel channel, String text, Guild guild) {
        boolean success = false;
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
            channel.sendMessage("Error: Task not created.").complete();
        }
        if (success) {
            channel.sendMessage("Task successfully created!").complete();
            updateTaskList(guild);
        }
    }

    private void deleteTask(TextChannel channel, String text, Guild guild) {
        String[] tokens = text.split(" ");
        boolean success = false;
        if (tokens.length > 3) {
            String name = Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "));
            for (Task t : tasks) {
                if (t.isMe(name)) {
                    tasks.remove(t);
                    success = true;
                    break;
                }
            }
            if (!success) {
                channel.sendMessage("Task did not exist!").complete();
            } else {
                channel.sendMessage("Task successfully removed!").complete();
                updateTaskList(guild);
            }
        } else {
            try {
                int remove = Integer.parseInt(tokens[1]) - 1;
                tasks.remove(remove);
                updateTaskList(guild);
                success = true;
            } catch (NumberFormatException ex) {
                success = false;
                String name = tokens[1];
                for (Task t : tasks) {
                    if (t.isMe(name)) {
                        tasks.remove(t);
                        success = true;
                        break;
                    }
                }
            }

            if (!success) {
                channel.sendMessage("Give a real number!").complete();
            } else {
                channel.sendMessage("Task successfully removed!").complete();
                updateTaskList(guild);
            }
        }
    }

    private void taskInfo(TextChannel channel, String text) {
        String[] tokens = text.split(" ");
        String name = Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "));
        boolean success = false;
        for (Task t : tasks) {
            if (t.isMe(name)) {
                channel.sendMessage(t.displayInfo()).complete();
                success = true;
                break;
            }
        }
        if (!success) {
            channel.sendMessage("Error: Task not found.").complete();
        }
    }

    private void updateTaskList(Guild guild) {
        TextChannel listChannel = guild.getTextChannelById(630275714641428541L);
        MessageHistory history = new MessageHistory(listChannel);
        List<Message> msgs;

        msgs = history.retrievePast(2).complete();
        listChannel.deleteMessages(msgs).complete();

        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Task List", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.blue);

        for (int i = 0; i < tasks.size(); i++) {
            bldr.addField(i + 1 + ". " + tasks.get(i).getTaskName(), "Responsible: " + tasks.get(i).getResponsible() + "\nPriority: " + tasks.get(i).getPriority(),false);
        }

        if (tasks.size() == 0) {
            bldr.addField("No tasks currently listed.", "Good job staying on task!", false);
        }

        bldr.setFooter("Bot created and managed by Zak", null);

        listChannel.sendMessage(bldr.build()).complete();
        listChannel.sendMessage("Last Updated: " + new Date(System.currentTimeMillis())).complete();
    }

    private void remind(TextChannel channel, String text, Message message, Guild guild) {
        try {
            String[] tokens = text.split(" ");
            List<Task> temp = new ArrayList<>();
            List<User> user = message.getMentionedUsers();
            int count = 0;
            Member member = guild.getMember(user.get(0));
            String name = member.getNickname();
            String priority = tokens[1];

            for (Task t : tasks) {
                if (t.getPriority().equalsIgnoreCase(priority) &&
                        (t.getResponsible().equalsIgnoreCase("anyone") || t.getResponsible().equalsIgnoreCase("everyone") || t.getResponsible().contains(name))) {
                    temp.add(t);
                    count++;
                }
            }

            if (count > 0) {
                StringBuilder sendBuilder = new StringBuilder(user.get(0).getAsMention() + ", you have " + count + " tasks left to complete with " + priority + " priority!```");
                for (Task t : temp) {
                    sendBuilder.append("\n- ").append(t.getTaskName());
                }
                String send = sendBuilder.toString();
                send += "```";

                channel.sendMessage(send).complete();
            } else {
                channel.sendMessage(user.get(0).getAsMention() + ", you have 0 tasks left to complete with " + priority + " priority! Good job staying on task!").complete();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            channel.sendMessage("Please give a valid priority factor!").complete();
        } catch (IndexOutOfBoundsException ex) {
            channel.sendMessage("Please ping the desired user!").complete();
        }
    }

    private void purchaseHelp(TextChannel channel) {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Purchase Help", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.green);

        bldr.addField("p!add <amount> <cost> <item> ", "Adds a purchase to the purchase list", false);
        bldr.addField("p!remove <item> or <item number>", "Removes the designated item from the list", false);
        bldr.addField("p!save", "Saves the current purchase list to the save file", false);
        bldr.addField("p!info <part>", "Displays info about designated part", false);
        bldr.addField("p!reload", "Reloads the purchase list", false);

        bldr.setFooter("Bot created and managed by Zak", null);

        channel.sendMessage(bldr.build()).complete();
    }

    private void purchaseSave(TextChannel channel) {
        try {
            writePurchases();
            channel.sendMessage("Purchases successfully saved!").complete();
        } catch (IOException ex) {
            channel.sendMessage("Error: Purchases not saved.").complete();
            recentErrorMsg = ex.getMessage();
            recentError = ex.getStackTrace();
        }
    }

    private void updatePurchaseList(Guild guild) {
        TextChannel listChannel = guild.getTextChannelById(586241646816133124L);
        MessageHistory history = new MessageHistory(listChannel);
        List<Message> msgs;

        msgs = history.retrievePast(2).complete();
        listChannel.deleteMessages(msgs).complete();

        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Purchase List", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.green);

        for (int i = 0; i < purchases.size(); i++) {
            bldr.addField(i + 1 + ". " + purchases.get(i).getName(), "Amount: " + purchases.get(i).getAmount()
                    + "\nCost Per Unit: $" + purchases.get(i).getCostPerUnit()
                    + "\nTotal Cost: $" + purchases.get(i).getTotalCost(),false);
        }

        if (purchases.size() == 0) {
            bldr.addField("No purchases currently listed", "Hopefully this is a good thing...", false);
        }

        bldr.setFooter("Bot created and managed by Zak", null);

        listChannel.sendMessage(bldr.build()).complete();
        listChannel.sendMessage("Last Updated: " + new Date(System.currentTimeMillis())).complete();
    }

    private void purchaseInfo(TextChannel channel, String text) {
        String[] tokens = text.split(" ");
        String name = Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "));
        boolean success = false;
        for (Purchase p : purchases) {
            if (p.isMe(name)) {
                channel.sendMessage(p.displayInfo()).complete();
                success = true;
                break;
            }
        }
        if (!success) {
            channel.sendMessage("Error: Purchase not found.").complete();
        }
    }

    private void addPurchase(TextChannel channel, String text, Guild guild) {
        boolean success = false;
        try {
            String[] tokens = text.split(" ");
            int amount = Integer.parseInt(tokens[1]);
            String name = Arrays.stream(tokens).skip(3).collect(Collectors.joining(" "));

            double costPerUnit;
            if (tokens[2].contains("$")) {
                costPerUnit = Double.parseDouble(tokens[2].substring(1));
            } else {
                costPerUnit = Double.parseDouble(tokens[2]);
            }

            System.out.println("a: " + amount);
            System.out.println("cpu: " + costPerUnit);
            System.out.println("n: " + name);
            purchases.add(new Purchase(amount, costPerUnit, name));
            success = true;
        } catch (NumberFormatException ex) {
            channel.sendMessage("Error: Purchase not created. Please enter a valid amount or cost per unit!").complete();
        } catch (IllegalArgumentException ex) {
            recentError = ex.getStackTrace();
            recentErrorMsg = ex.getMessage();
            channel.sendMessage("Error: Purchase not created.").complete();
        }
        if (success) {
            channel.sendMessage("Purchase successfully created!").complete();
            updatePurchaseList(guild);
        }
    }

    private void deletePurchase(TextChannel channel, String text, Guild guild) {
        String[] tokens = text.split(" ");
        boolean success = false;
        if (tokens.length > 3) {
            String name = Arrays.stream(tokens).skip(1).collect(Collectors.joining(" "));
            for (Purchase p : purchases) {
                if (p.isMe(name)) {
                    purchases.remove(p);
                    success = true;
                    break;
                }
            }
            if (!success) {
                channel.sendMessage("Purchase did not exist!").complete();
            } else {
                channel.sendMessage("Purchase successfully removed!").complete();
                updatePurchaseList(guild);
            }
        } else {
            try {
                int remove = Integer.parseInt(tokens[1]) - 1;
                purchases.remove(remove);
                updatePurchaseList(guild);
                success = true;
            } catch (NumberFormatException ex) {
                success = false;
                String name = tokens[1];
                for (Purchase p : purchases) {
                    if (p.isMe(name)) {
                        purchases.remove(p);
                        success = true;
                        break;
                    }
                }
            }

            if (!success) {
                channel.sendMessage("Give a real number!").complete();
            } else {
                channel.sendMessage("Purchase successfully removed!").complete();
            }
        }
    }

}
