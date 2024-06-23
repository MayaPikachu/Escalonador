package simulation;

import interfaces.InterSchedulerInterface;
import interfaces.NotificationInterface;
import interfaces.SubmissionInterface;
import program.Program;
import program.ProgramLoader;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class LongTermScheduler implements Runnable, SubmissionInterface {

    private NotificationInterface notificationObserver;
    private final int maxLoad;
    private boolean running;
    private final InterSchedulerInterface interSchedulerInterface;
    private final Queue<Program> submissionQueue;
    private final ProgramLoader loader;
    private long nextPid;

    public LongTermScheduler(int maxLoad, InterSchedulerInterface interSchedulerInterface, ProgramLoader loader) {
        this.maxLoad = maxLoad;
        this.interSchedulerInterface = interSchedulerInterface;
        this.loader = loader;
        submissionQueue = new LinkedList<>();
        this.running = true;
        this.notificationObserver = null;
        nextPid = 1;
    }

    public void setNotificationObserver(NotificationInterface notificationObserver) {
        this.notificationObserver = notificationObserver;
    }

    @Override
    public void run() {
        while (running) {
            int currentLoad = interSchedulerInterface.getProcessLoad();
            if (currentLoad < maxLoad) {
                if (submissionQueue.isEmpty()) {
                    continue;
                }
                interSchedulerInterface.addProcess(new SimulatedProcess(submissionQueue.remove(), useNextPid()));
            }
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public boolean submitJob(String fileName) {

        Program program = loader.loadProgram(fileName);
        if (program == null) {
            return false;
        }
        submissionQueue.add(program);
        return true;
    }

    @Override
    public void displaySubmissionQueue() {
        if (notificationObserver != null) {
            StringBuilder message = new StringBuilder("Submission queue:");
            for (var program : submissionQueue) {
                message.append(" | ").append(program.getFilename());
            }
            notificationObserver.display(message.toString());
        }
    }

    private long useNextPid() {
        return nextPid++;
    }
}
