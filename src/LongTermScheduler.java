import Interfaces.InterSchedulerInterface;
import Interfaces.SubmissionInterface;
import program.Program;
import program.ProgramLoader;

import java.util.ArrayDeque;
import java.util.Queue;

public class LongTermScheduler implements Runnable, SubmissionInterface {

    private final int maxLoad;
    private boolean running;
    private final InterSchedulerInterface interSchedulerInterface;
    private final Queue<Program> processQueue;
    private final ProgramLoader loader;

    public LongTermScheduler(int maxLoad, InterSchedulerInterface interSchedulerInterface, ProgramLoader loader) {
        this.maxLoad = maxLoad;
        this.interSchedulerInterface = interSchedulerInterface;
        this.loader = loader;
        processQueue = new ArrayDeque<Program>();
        this.running = true;
    }


    @Override
    public void run() {
        while(running){
            int currentLoad = interSchedulerInterface.getProcessLoad();
            if(currentLoad < maxLoad) {
                if (processQueue.isEmpty()) {
                    continue;
                }
                interSchedulerInterface.addProcess(processQueue.element());
            }
        }
    }

    public void stop(){
        running = false;
    }
    @Override
    public boolean submitJob(String fileName) {

        Program program = loader.loadProgram(fileName);
        if(program == null){
            return false;
        }
        processQueue.add(program);
        return true;
    }

    @Override
    public void displaySubmissionQueue() {
        System.out.println("O número de processos na fila de submetidos mas não encaminhados ao escalonador de curto prazo é " + processQueue.size());
    }
}
