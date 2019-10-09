package engine;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.text.DecimalFormat;

public class Purchase implements Embedabble {

    private String name;
    private int amount;
    private double costPerUnit;
    private double totalCost;

    private static DecimalFormat df = new DecimalFormat("#.##");

    public Purchase(int amount, double costPerUnit, String name) {
        this.amount = amount;
        this.costPerUnit = costPerUnit;
        this.name = name;
        totalCost = Double.parseDouble(df.format(amount * costPerUnit));
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(int costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public boolean isMe(String purchaseName) {
        return this.name.equalsIgnoreCase(purchaseName.trim());
    }

    @Override
    public MessageEmbed displayInfo() {
        EmbedBuilder bldr = new EmbedBuilder();

        bldr.setAuthor("Purchase Info", "https://github.com/Urdenwaz/rocketbot", "https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setThumbnail("https://cdn.discordapp.com/attachments/499820723192463370/504725105889378304/rocket.png");
        bldr.setColor(Color.green);

        bldr.addField("Part Name:", name, false);
        bldr.addField("Part Amount:", amount + "", false);
        bldr.addField("Cost per Unit:", "$" + costPerUnit, false);
        bldr.addField("Total Cost:", "$" + totalCost, false);

        bldr.setFooter("Bot created and managed by Zak", null);

        return bldr.build();
    }

    @Override
    public String toString() {
        return name + " " + amount + " " +  costPerUnit;
    }
}
