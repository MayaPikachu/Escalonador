package simulation;

import interfaces.ControlInterface;
import interfaces.InterSchedulerInterface;
import interfaces.NotificationInterface;

import java.util.*;

/**
 * Implements the short term scheduler
 */
public class ShortTermScheduler implements Runnable, ControlInterface, InterSchedulerInterface {

    private final Map<SimulatedProcess, SchedulerProcessData> processDataMap = new HashMap<>();
    private NotificationInterface notificationObserver;
    private final Queue<SimulatedProcess> blockedQueue;
    private final Queue<SimulatedProcess> ioBoundReadyQueue;
    private final Queue<SimulatedProcess> cpuBoundReadyQueue;
    private final int quantum;
    private boolean running;
    private final Object mutex = new Object();

    public ShortTermScheduler(int quantum) {
        blockedQueue = new LinkedList<>();
        ioBoundReadyQueue = new LinkedList<>();
        cpuBoundReadyQueue = new LinkedList<>();
        this.quantum = quantum;
        notificationObserver = null;
        running = false;
    }

    public void setNotificationObserver(NotificationInterface notificationObserver) {
        this.notificationObserver = notificationObserver;
    }

    /**
     * @param process A process that was unblocked.
     * Puts a process that was just unblocked in the correct queue.
     */
    private void addReadyProcess(SimulatedProcess process) {
        if (processDataMap.get(process).isIoBound()) {
            ioBoundReadyQueue.add(process);
        } else {
            cpuBoundReadyQueue.add(process);
        }
    }

    /**
     * @return true if the process could be run.
     * Attempts to run a process in the CPU-bound queue, if there are any.
     */
    private boolean attemptRunCpuBoundProcess() {
        if (cpuBoundReadyQueue.isEmpty()) {
            return false;
        }
        SimulatedProcess process = cpuBoundReadyQueue.remove();
        process.runInstruction(quantum);
        if (process.isDone()) {
            finalizeProcess(process);
            return true;
        }
        if (process.isBlocked()) {
            processDataMap.get(process).blockCount++;
            blockedQueue.add(process);
        } else {
            processDataMap.get(process).executeCount++;
            addReadyProcess(process);
        }
        return true;
    }

    /**
     * @return true if a process could be run.
     * Attempts to run a process in the IO-bound queue, if there are any.
     */
    private boolean attemptRunIoBoundProcess() {
        if (ioBoundReadyQueue.isEmpty()) {
            return false;
        }
        SimulatedProcess process = ioBoundReadyQueue.remove();
        process.runInstruction(quantum);
        if (process.isDone()) {
            finalizeProcess(process);
            return true;
        }
        if (process.isBlocked()) {
            processDataMap.get(process).blockCount++;
            blockedQueue.add(process);
        } else {
            processDataMap.get(process).executeCount++;
            addReadyProcess(process);
        }
        return true;
    }

    /**
     * Goes through the entire blocked queue and checks for each process whether it should be unblocked or not.
     */
    private void updateBlockedQueue() {
        int n = blockedQueue.size();
        for (int i=0;i<n;i++) {
            SimulatedProcess process = blockedQueue.remove();
            process.decrementBlockTime();
            if (process.isBlocked()) {
                blockedQueue.add(process);
            } else {
                addReadyProcess(process);
            }
        }
    }

    /**
     * @param process The process to be finalized.
     * Erases the data for a simulated process.
     * Should be used after the process is done running.
     */
    private void finalizeProcess(SimulatedProcess process) {
        processDataMap.remove(process);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            synchronized (mutex) {
                if (!running) {
                    continue;
                }
                boolean shouldSimulateBlock = true;
                if (attemptRunCpuBoundProcess()) {
                    updateBlockedQueue();
                    shouldSimulateBlock = false;
                }
                if (attemptRunCpuBoundProcess()) {
                    updateBlockedQueue();
                    shouldSimulateBlock = false;
                }
                if (attemptRunIoBoundProcess()) {
                    updateBlockedQueue();
                    shouldSimulateBlock = false;
                }
                if (shouldSimulateBlock) {
                    try {
                        Thread.sleep(quantum);
                    } catch (InterruptedException e) {
                    }
                    updateBlockedQueue();
                }
            }
        }
    }

    @Override
    public void startSimulation() {
        synchronized (mutex) {
            running = true;
        }
    }

    @Override
    public void suspendSimulation() {
        synchronized (mutex) {
            running = false;
        }
    }

    @Override
    public void resumeSimulation() {
        synchronized (mutex) {
            running = true;
        }
    }

    @Override
    public void stopSimulation() {
        synchronized (mutex) {
            running = false;
            blockedQueue.clear();
            ioBoundReadyQueue.clear();
            cpuBoundReadyQueue.clear();
            processDataMap.clear();
        }
    }

    @Override
    public void displayProcessQueues() {
        synchronized (mutex) {
            if (notificationObserver != null) {
                StringBuilder message = new StringBuilder();
                message.append("Blocked queue:");
                for (var process : blockedQueue) {
                    message.append(" (").append(process.getPid()).append(", ").append(process.getName()).append(")");
                }
                message.append("\n");
                message.append("IO-bound queue:");
                for (var process : ioBoundReadyQueue) {
                    message.append(" (").append(process.getPid()).append(", ").append(process.getName()).append(")");
                }
                message.append("\n");
                message.append("CPU-bound queue:");
                for (var process : cpuBoundReadyQueue) {
                    message.append(" (").append(process.getPid()).append(", ").append(process.getName()).append(")");
                }
                message.append("\n");
                notificationObserver.display(message.toString());
            }
        }
    }

    /**
     * @param process The simulated process.
     * Adds a process to the current load of the simulation.
     */
    @Override
    public void addProcess(SimulatedProcess process) {
        synchronized (mutex) {
            cpuBoundReadyQueue.add(process);
            processDataMap.put(process, new SchedulerProcessData());
        }
    }

    /**
     * @return The current process load, if the simulation is running.
     * If the simulation is not currently running, it returns "Infinity" (which means you can't add any processes to the load)
     */
    @Override
    public int getProcessLoad() {
        synchronized (mutex) {
            if (!running) {
                return Integer.MAX_VALUE;
            }
            return blockedQueue.size() + cpuBoundReadyQueue.size() + ioBoundReadyQueue.size();
        }
    }

    /**
     * A class that stores data about the simulation of a process.
     * It is used to check whether the process should be considered IO-bound or CPU-bound.
     */
    private static class SchedulerProcessData {
        public int executeCount = 0;
        public int blockCount = 0;

        public boolean isIoBound() {
            return blockCount > executeCount;
        }
    }
}
