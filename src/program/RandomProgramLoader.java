package program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomProgramLoader implements ProgramLoader {
    private static final int MAX_BLOCK_TIME = 20;
    @Override
    public Program loadProgram(String fileName) {
        if (fileName.startsWith("IO")) {
            int num = Integer.parseInt(fileName.substring(2));
            return randomIOBoundProgram(num);
        } else if (fileName.startsWith("CPU")) {
            int num = Integer.parseInt(fileName.substring(3));
            return randomCPUBoundProgram(num);
        }
        int num = Integer.parseInt(fileName);
        return randomProgram(num);
    }

    private Program randomIOBoundProgram(int size) {
        Random rng = new Random();
        List<Instruction> instructions = new ArrayList<>();
        int numBlock = Math.abs(rng.nextInt() % size);
        if (numBlock < size - numBlock) {
            numBlock = size - numBlock;
        }
        for (int i=0;i<numBlock;i++) {
            int duration = Math.abs(rng.nextInt() % MAX_BLOCK_TIME)+1;
            instructions.add(new BlockInstruction(duration));
        }
        for (int i=numBlock;i<size;i++) {
            instructions.add(new ExecuteInstruction());
        }
        Collections.shuffle(instructions);
        return new Program(instructions, "IO");
    }
    private Program randomCPUBoundProgram(int size) {
        Random rng = new Random();
        List<Instruction> instructions = new ArrayList<>();
        int numExecute = Math.abs(rng.nextInt() % size);
        if (numExecute < size - numExecute) {
            numExecute = size - numExecute;
        }
        for (int i=0;i<numExecute;i++) {
            instructions.add(new ExecuteInstruction());
        }
        for (int i=numExecute;i<size;i++) {
            int duration = Math.abs(rng.nextInt() % MAX_BLOCK_TIME)+1;
            instructions.add(new BlockInstruction(duration));
        }
        Collections.shuffle(instructions);
        return new Program(instructions, "CPU");
    }
    private Program randomProgram(int size) {
        Random rng = new Random();
        List<Instruction> instructions = new ArrayList<>();
        for (int i=0;i<size;i++) {
            if (rng.nextBoolean()) {
                instructions.add(new ExecuteInstruction());
            } else {
                int duration = Math.abs(rng.nextInt() % MAX_BLOCK_TIME)+1;
                instructions.add(new BlockInstruction(duration));
            }
        }
        return new Program(instructions, "Random");
    }
}
