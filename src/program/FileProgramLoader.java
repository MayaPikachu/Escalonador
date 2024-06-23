package program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the program loader interface by loading a program from a file
 */
public class FileProgramLoader implements ProgramLoader {
    /**
     * @param fileName The name of the file from which the program will be loaded
     * @return The program that was loaded from the specified file
     */
    @Override
    public Program loadProgram(String fileName) {
        try {
            byte[] contentBytes = Files.readAllBytes(Paths.get(fileName));
            String content = new String(contentBytes);
            return parseProgram(content);
        } catch (IOException e) {
            System.err.printf("Failed to load program '%s'\nException: '%s'\n", fileName, e.getMessage());
        } catch (ParseException e) {
            System.err.printf("Failed to parse program '%s'\nException: '%s'\n", fileName, e.getMessage());
        }
        return null;
    }

    /**
     * @param programText The code for that program
     * @return The program parsed from the code
     * @throws ParseException If it fails to parse the program, it throws this exception with a message
     */
    private Program parseProgram(String programText) throws ParseException {
        List<String> lines = programText.lines().filter(line -> !line.isBlank()).toList();
        if (lines.size() < 3) {
            throw new ParseException("Too few lines in program", 0);
        }
        String filename = parseProgramHeader(lines.get(0).strip());
        if (!lines.get(1).strip().equals("begin")) {
            throw new ParseException("Missing program begin", 0);
        }
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 2; i < lines.size() - 1; i++) {
            Instruction ins = parseInstruction(lines.get(i));
            instructions.add(ins);
        }
        if (!lines.getLast().strip().equals("end")) {
            throw new ParseException("Missing program end. Program is possibly truncated.", 0);
        }
        return new Program(instructions, filename);
    }

    /**
     * @param programHeader The first line of the program code (referred to as header)
     * @return The name of the program that was parsed in the code
     * @throws ParseException If it fails to parse the program header, it throws this exception with a message
     */
    private String parseProgramHeader(String programHeader) throws ParseException {
        try {
            // Return the file name
            return programHeader.split(" ")[1];
        } catch (Exception e) {
            throw new ParseException("Failed to parse program header: " + e.getMessage(), 0);
        }
    }

    /**
     * @param line The line of code in the program
     * @return The corresponding instruction in that line
     * @throws ParseException If it fails to parse the line, it throws an exception with a message
     */
    private Instruction parseInstruction(String line) throws ParseException {
        line = line.strip();
        if (line.equals("execute")) {
            return new ExecuteInstruction();
        }
        String[] parts = line.split(" ");
        if (!parts[0].equals("block")) {
            System.err.printf("Weird instruction type found: '%s'\n", parts[0]);
            throw new ParseException("Weird instruction type found. Stopping...", 0);
        }
        try {
            int duration = Integer.parseInt(parts[1]);
            return new BlockInstruction(duration);
        } catch (NumberFormatException e) {
            throw new ParseException("Failed to parse block duration: " + e.getMessage(), 0);
        }
    }
}
