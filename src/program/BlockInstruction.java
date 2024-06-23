package program;

/**
 * A data class that represents a block instruction, and stores the duration
 */
public class BlockInstruction extends Instruction {
    private final int duration;

    public BlockInstruction(int duration) {
        super(InstructionType.BLOCK);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
