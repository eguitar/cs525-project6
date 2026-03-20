import java.io.*;


public class Assembler {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Assembler <filename.asm>");
            System.exit(1);
        }

        String inputFile = args[0];
        if (!inputFile.endsWith(".asm")) {
            System.out.println("Error: Input file must have a .asm extension.");
            System.exit(1);
        }

        String outputFile = inputFile.replace(".asm", ".hack");
        SymbolTable symbolTable = new SymbolTable();
        Parser firstPass = new Parser(inputFile);
        int romAddress = 0;

        while (firstPass.hasMoreCommands()) {
            firstPass.advance();
            int type = firstPass.commandType();
            
            if (type == Parser.A_COMMAND || type == Parser.C_COMMAND) {
                romAddress++;
            } else if (type == Parser.L_COMMAND) {
                String label = firstPass.symbol();
                if (!symbolTable.contains(label)) {
                    symbolTable.addEntry(label, romAddress);
                }
            }
        }

        Parser secondPass = new Parser(inputFile);
        int nextVariableAddress = 16;
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        while (secondPass.hasMoreCommands()) {
            secondPass.advance();
            int type = secondPass.commandType();

            if (type == Parser.A_COMMAND) {
                String symbol = secondPass.symbol();
                int value;

                if (symbol.matches("\\d+")) {
                    value = Integer.parseInt(symbol);
                } else {
                    if (symbolTable.contains(symbol)) {
                        value = symbolTable.getAddress(symbol);
                    } else {
                        symbolTable.addEntry(symbol, nextVariableAddress);
                        value = nextVariableAddress;
                        nextVariableAddress++;
                    }
                }

                String binary = String.format("%16s", Integer.toBinaryString(value))
                                      .replace(' ', '0');
                writer.write(binary);
                writer.newLine();

            } else if (type == Parser.C_COMMAND) {
                String compBits = Code.comp(secondPass.comp());
                String destBits = Code.dest(secondPass.dest());
                String jumpBits = Code.jump(secondPass.jump());

                String binary = "111" + compBits + destBits + jumpBits;
                writer.write(binary);
                writer.newLine();
            }
        }

        writer.close();
        System.out.println("Assembly complete! Output written to: " + outputFile);
    }
}