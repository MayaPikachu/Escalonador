package simulation;

import interfaces.ControlInterface;
import interfaces.InterSchedulerInterface;
import interfaces.NotificationInterface;

import java.util.*;

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

    private void updateBlockTime() {
        for (var process: blockedQueue) {
            process.decrementBlockTime();;
            if (!process.isBlocked()) {
                blockedQueue.remove(process);
                if (process.isDone()) {
                    processDataMap.remove(process);
                }

            }
        }
    }
    public void addReadyProcess(SimulatedProcess process) {
        if (processDataMap.get(process).isIoBound()) {
            ioBoundReadyQueue.add(process);
        } else {
            cpuBoundReadyQueue.add(process);
        }
    }
    private boolean attemptRunCpuBoundProcess() {
        if (cpuBoundReadyQueue.isEmpty()) {
            return false;
        }
        SimulatedProcess process = cpuBoundReadyQueue.remove();
        process.executeInstruction(quantum);
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
    private boolean attemptRunIoBoundProcess() {
        if (ioBoundReadyQueue.isEmpty()) {
            return false;
        }
        SimulatedProcess process = ioBoundReadyQueue.remove();
        process.executeInstruction(quantum);
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

    @Override
    public void addProcess(SimulatedProcess program) {
        synchronized (mutex) {
            cpuBoundReadyQueue.add(program);
            processDataMap.put(program, new SchedulerProcessData());
        }
    }

    @Override
    public int getProcessLoad() {
        synchronized (mutex) {
            if (!running) {
                return Integer.MAX_VALUE;
            }
            return blockedQueue.size() + cpuBoundReadyQueue.size() + ioBoundReadyQueue.size();
        }
    }

    private static class SchedulerProcessData {
        public int executeCount = 0;
        public int blockCount = 0;

        public boolean isIoBound() {
            return blockCount > executeCount;
        }
    }
}
