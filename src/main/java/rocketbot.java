import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class rocketbot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, IOException {
        try (BufferedReader rdr = new BufferedReader(new FileReader("token.txt"))) {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(rdr.readLine())
                    .setGame(Game.playing("with OpenRocket"))
                    .addEventListener(new rocketbot())
                    .build();
            String invite = jda.asBot().getInviteUrl();
            System.out.println(invite);
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String text = e.getMessage().getContentStripped();
        String name = e.getAuthor().getName();

        if (name.toLowerCase().equals("rocketbot")) {

        }

        else {
            if (text.toLowerCase().equals("`help")) {
                EmbedBuilder bldr = new EmbedBuilder();

                bldr.setAuthor("rocketbot Commands", "http://www.physicsrocks.com/rocketry-club.html", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                bldr.setColor(Color.red);

                bldr.addField("`rqdoc", "TARC Requirements Document", false);
                bldr.addField("`specs", "TARC Rocket Specifications", false);
                bldr.addField("`design", "Link to Current OpenRocket Design", false);
                bldr.addField("`openrocket", "Link to OpenRocket", false);

                e.getChannel().sendMessage(bldr.build()).complete();
            }

            if (text.toLowerCase().equals("`rqdoc")) {
                e.getChannel().sendMessage("__**TARC Requirements Doc:**__").complete();
                e.getChannel().sendMessage("https://3384f12ld0l0tjlik1fcm68s-wpengine.netdna-ssl.com/wp-content/uploads/2018/08/Event-Rules-TARC-2019-FINAL.pdf").complete();
            }

            if (text.toLowerCase().equals("`design")) {
                e.getChannel().sendMessage("<design google drive>").complete();
            }

            if (text.toLowerCase().equals("`openrocket")) {
                e.getChannel().sendMessage("http://openrocket.info/").complete();
            }

            if (text.toLowerCase().equals("`specs")) {
                EmbedBuilder bldr = new EmbedBuilder();

                bldr.setAuthor("TARC Rocket Specifications", "https://rocketcontest.org/","https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
                bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504817600790921256/TARC_logo2_9-23-08_400x400.png");
                bldr.setColor(Color.blue);

                bldr.addField("Mass", "Cannot exceed 650 grams", false);
                bldr.addField("Length", "No less than 650 mm (25.6 inches)", false);
                bldr.addField("Paint", "In order to launch at finals, must be painted (5 point penalty if no paint)", false);
                bldr.addField("Motor", "F Class or lower, can use any number as long as impulse does not exceed 80 N*s", false);
                bldr.addField("Payload", "Three Raw Hen Eggs, 55 - 61 grams, 45 millimeters or less, must return with no external damage", false);
                bldr.addField("Paracute", "Portion containing egg and altimeter must separate and descend with at least 2 parachutes", false);
                bldr.addField("Flight Duration", "43 - 45 seconds, starting from lift-off", false);
                bldr.addField("Flight Altitude", "856 feet", false);

                e.getChannel().sendMessage(bldr.build()).complete();
            }
        }
    }
}
