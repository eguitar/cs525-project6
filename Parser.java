import java.io.*;
import java.util.ArrayList;


public class Parser {

    public static final int A_COMMAND = 0;
    public static final int C_COMMAND = 1;
    public static final int L_COMMAND = 2;

    private ArrayList<String> lines;
    private int currentIndex;
    private String currentCommand;

    public Parser(String filename) throws IOException {
        lines = new ArrayList<>();
        currentIndex = -1;
        currentCommand = null;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            int commentIndex = line.indexOf("//");
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex);
            }
            line = line.trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        reader.close();
    }

    public boolean hasMoreCommands() {
        return currentIndex < lines.size() - 1;
    }

    public void advance() {
        currentIndex++;
        currentCommand = lines.get(currentIndex);
    }

    public int commandType() {
        if (currentCommand.startsWith("@")) {
            return A_COMMAND;
        } else if (currentCommand.startsWith("(")) {
            return L_COMMAND;
        } else {
            return C_COMMAND;
        }
    }

    public String symbol() {
        if (commandType() == A_COMMAND) {
            return currentCommand.substring(1).trim();
        } else {
            return currentCommand.substring(1, currentCommand.length() - 1).trim();
        }
    }

    public String dest() {
        if (currentCommand.contains("=")) {
            return currentCommand.split("=")[0].trim();
        }
        return "null";
    }

    public String comp() {
        String cmd = currentCommand;
        if (cmd.contains("=")) {
            cmd = cmd.split("=")[1].trim();
        }
        if (cmd.contains(";")) {
            cmd = cmd.split(";")[0].trim();
        }
        return cmd;
    }

    public String jump() {
        if (currentCommand.contains(";")) {
            return currentCommand.split(";")[1].trim();
        }
        return "null";
    }
}