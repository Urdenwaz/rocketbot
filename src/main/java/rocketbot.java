import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
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

}
