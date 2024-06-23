import program.RandomProgramLoader;
import simulation.LongTermScheduler;
import simulation.ShortTermScheduler;
import simulation.UserInterface;

public class Main {
    public static void main(String[] args) {
        ShortTermScheduler shortTermScheduler = new ShortTermScheduler(500);
        LongTermScheduler longTermScheduler = new LongTermScheduler(5, shortTermScheduler, new RandomProgramLoader());
        UserInterface userInterface = new UserInterface(longTermScheduler, shortTermScheduler);
        shortTermScheduler.setNotificationObserver(userInterface);
        longTermScheduler.setNotificationObserver(userInterface);
        new Thread(shortTermScheduler).start();
        new Thread(longTermScheduler).start();
        userInterface.run();
    }
}