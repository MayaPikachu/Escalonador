import interfaces.ControlInterface;
import interfaces.InterSchedulerInterface;
import program.Program;

import java.util.Queue;

public class ShortTermScheduler implements Runnable, ControlInterface, InterSchedulerInterface {

    private final Queue<Program> blockedQueue;
    private final Queue<Program> readyQueue;
    private final int quantum;

    public ShortTermScheduler(Queue<Program> blockedQueue, Queue<Program> readyQueue, int quantum) {
        this.blockedQueue = blockedQueue;
        this.readyQueue = readyQueue;
        this.quantum = quantum;
    }

    @Override
    public void run() {

    }

    @Override
    public void startSimulation() {

    }

    @Override
    public void suspendSimulation() {

    }

    @Override
    public void resumeSimulation() {

    }

    @Override
    public void stopSimulation() {

    }

    @Override
    public void displayProcessQueues() {

    }

    @Override
    public void addProcess(Program program) {

    }

    @Override
    public int getProcessLoad() {
        return 0;
    }
}
