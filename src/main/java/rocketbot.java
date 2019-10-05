import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class rocketbot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, IOException { // creates bot, should be left untouched
        try (BufferedReader rdr = new BufferedReader(new FileReader("token.txt"))) {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(rdr.readLine())
                    .setGame(Game.playing("type r!help for commands"))
                    .addEventListener(new rocketbot())
                    .addEventListener(new MessageRespond())
                    .build();
            String invite = jda.asBot().getInviteUrl();
            System.out.println(invite);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        User user = e.getUser();
        PrivateChannel channel = user.openPrivateChannel().complete();

        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Skyline Rocketry Club", "http://www.physicsrocks.com/rocketry-club.html", "https://cdn.discordapp.com/attachments/499820723192463370/626292450016755712/Fluffy_Belgian_Waffles.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/626292355691053056/1200px-Waffles_with_Strawberries.png");
        bldr.setColor(Color.RED);

        bldr.addField("Hello, and welcome to the Skyline Rocketry Club Discord!","Please message your first name in the ``#give-name`` channel.", false);
        bldr.addBlankField(false);
        bldr.addField("Bot Help:", "Once you are confirmed, use r!help in ``#bot`` for bot commands, or message one of the board members if you have any questions.", false);
        bldr.setFooter("Bot created by Zak", "https://cdn.discordapp.com/attachments/191585321522036746/504686161189535764/image0.jpg");

        channel.sendMessage(bldr.build()).complete();
    }

}
