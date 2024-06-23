package program;

import java.util.List;

/**
 * A data class that represents a program to be simulated.
 * It stores the list of instructions, as well as the program name.
 */
public class Program {
    private final String filename;
    private final List<Instruction> body;

    public Program(List<Instruction> body, String filename) {
        this.body = body;
        this.filename = filename;
    }
    public List<Instruction> getBody() {
        return body;
    }
    public String getFilename() {
        return filename;
    }
}
