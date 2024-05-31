package program;

import java.util.List;

public class Program {
    private final List<Instruction> body;
    private final int countExecute;
    private final int countBlock;
    private int waitTime;

    public Program(List<Instruction> body) {
        this.body = body;
        int executes = 0;
        int blocks = 0;
        for(Instruction instruction: body){
            if(instruction.getType().equals(InstructionType.EXECUTE)){
                executes++;
            }
            if(instruction.getType().equals(InstructionType.BLOCK)){
                blocks++;
            }
        }
        countExecute = executes;
        countBlock = blocks;
    }

    public void incrementWaitTime(){
        waitTime++;
    }

    public List<Instruction> getBody() {
        return body;
    }

    public int getCountExecute() {
        return countExecute;
    }

    public int getCountBlock() {
        return countBlock;
    }

    public int getWaitTime() {
        return waitTime;
    }
}
