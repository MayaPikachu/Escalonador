package simulation;

import program.BlockInstruction;
import program.Program;

/**
 * Implements a process being simulated in the program.
 * Encapsulates information about the program to make sure
 * the short term scheduler has to have its own logic for
 * determining whether the program is CPU-bound or IO-bound.
 * It also stores the program data, and the current instruction.
 * It provides an interface for checking whether the program is blocked or not,
 * and implements the logic for executing an instruction.
 */
public class SimulatedProcess {
    private final Program program;
    private final long processId;
    private int currentInstruction;
    private int blockTime;
    SimulatedProcess(Program program, long processId) {
        this.program = program;
        currentInstruction = 0;
        this.processId = processId;
        blockTime = 0;
    }

    public String getName() {
        return program.getFilename();
    }

    public long getPid() {
        return processId;
    }

    public void runInstruction(int quantum) {
        System.out.printf("[%d] %s: Executing\n", getPid(), getName());
        try {
            Thread.sleep(quantum);
        } catch (Exception e) {
            System.err.println("Eu quero dormir!");
        }
        if (program.getBody().get(currentInstruction) instanceof BlockInstruction blockInstruction) {
            System.out.printf("[%d] %s: Blocking for %d\n", getPid(), getName(), blockInstruction.getDuration());
            blockTime = blockInstruction.getDuration();
        }
        currentInstruction++;
    }
    public boolean isDone() {
        return currentInstruction >= program.getBody().size();
    }
    public boolean isBlocked() {
        return blockTime > 0;
    }
    public void decrementBlockTime() {
        blockTime--;
    }
}
