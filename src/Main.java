import program.FileProgramLoader;
import program.RandomProgramLoader;
import simulation.LongTermScheduler;
import simulation.ShortTermScheduler;
import simulation.UserInterface;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: Escalonador <quantum> <max-load> <use-random-program-generator?>");
            System.exit(0);
        }
        int quantum = Integer.parseInt(args[0]);
        int maxLoad = Integer.parseInt(args[1]);
        boolean useRandom = false;
        if (args.length > 2) {
            useRandom = Boolean.parseBoolean(args[2]);
        }
        ShortTermScheduler shortTermScheduler = new ShortTermScheduler(quantum);
        LongTermScheduler longTermScheduler = new LongTermScheduler(maxLoad, shortTermScheduler, useRandom ? new RandomProgramLoader() : new FileProgramLoader());
        UserInterface userInterface = new UserInterface(longTermScheduler, shortTermScheduler);
        shortTermScheduler.setNotificationObserver(userInterface);
        longTermScheduler.setNotificationObserver(userInterface);
        new Thread(shortTermScheduler).start();
        new Thread(longTermScheduler).start();
        userInterface.run();
    }
}