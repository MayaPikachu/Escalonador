package program;

public class BlockInstruction extends Instruction{
    private final int duration;

    public BlockInstruction(int duration) {
        super(InstructionType.BLOCK);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
