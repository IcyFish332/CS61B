package gitlet;

import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Siyuan Lu
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository repo = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                checkIfInit();
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                checkIfInit();
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                checkIfInit();
                Repository.rm(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                checkIfInit();
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                checkIfInit();
                Repository.global_log();
                break;
            case "find":
                validateNumArgs(args, 2);
                checkIfInit();
                Repository.find(args[1]);
                break;
            case "branch":
                validateNumArgs(args, 2);
                checkIfInit();
                Repository.branch(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                checkIfInit();
                Repository.status();
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }

    }

    /**
     * Checks the number of arguments versus the expected number,
     * prints out warning and exits if they do not match.
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Checks whether current working directory is initialed.
     * prints out warning and exits if it is not.
     */
    public static void checkIfInit() {
        if (!GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
