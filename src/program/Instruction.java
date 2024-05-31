package program;

public class Instruction {
    private final InstructionType type;

    public Instruction(InstructionType type) {
        this.type = type;
    }


    public InstructionType getType() {
        return type;
    }
}
