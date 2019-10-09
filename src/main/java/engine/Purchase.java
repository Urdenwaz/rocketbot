package engine;

import java.text.DecimalFormat;

public class Purchase {

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
    public String toString() {
        return name + " " + amount + " " +  costPerUnit;
    }
}
