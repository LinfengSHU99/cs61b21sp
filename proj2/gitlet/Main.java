package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.*;
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        Repository.loadStatus();
        // TODO: what if args is empty?
        if (args.length == 0) {
            printAndExit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                if (args.length > 1) {
                   printAndExit("Incorrect operands.");
                }
                Repository.init();
                break;
            case "add":
                if (args.length > 2) printAndExit("Incorrect operands.");
                // TODO: handle the `add [filename]` command
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length > 2) {
                    printAndExit("Incorrect operands");
                }
                else if (args.length < 2 || args[1].equals("")) printAndExit("Please enter a commit message.");
                Repository.commit(args[1]);
                break;
            case "status":
                if (args.length > 1) printAndExit("Incorrect operands.");
                Repository.status();
                break;
            case "rm":
                if (args.length != 2) printAndExit("Incorrect operands.");

            // TODO: FILL THE REST IN
        }
        Repository.saveStatus();
    }
}
