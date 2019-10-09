import java.util.Scanner;

public class ConsoleThread extends Thread {

    @Override
    public void run() {
        Scanner console = new Scanner(System.in);
        System.out.println("Thread Started");
        while (true) {
            try {
                System.out.println("\nNext line?");
                String input = console.nextLine();
                if (input.startsWith("cch ")) {
                    rocketbot.mainMessageRespond.sayChannel = rocketbot.mainMessageRespond.server.getTextChannelById(input.split(" ")[1]);
                    System.out.println("Channel successfully changed!");
                } else {
                    rocketbot.mainMessageRespond.sayChannel.sendMessage(input).complete();
                }
            } catch (Exception e) {
                System.out.println("you fucked up");
            }
        }
    }

}
