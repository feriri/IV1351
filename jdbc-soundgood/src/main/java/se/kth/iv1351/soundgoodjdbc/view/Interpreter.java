package se.kth.iv1351.soundgoodjdbc.view;

import java.util.List;
import java.util.Scanner;

import se.kth.iv1351.soundgoodjdbc.controller.Controller;
import se.kth.iv1351.soundgoodjdbc.model.InstrumentDTO;

/**
 * Reads and interprets user commands. This command interpreter is blocking, the user
 * interface does not react to user input while a command is being executed.
 */
public class Interpreter {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private Controller ctrl;
    private boolean keepReceivingCmds = false;

    /**
     * Creates a new instance that will use the specified controller for all operations.
     * 
     * @param ctrl The controller used by this instance.
     */
    public Interpreter(Controller ctrl) {
        this.ctrl = ctrl;
    }

    /**
     * Stops the commend interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    /**
     * Interprets and performs user commands. This method will not return until the
     * UI has been stopped. The UI is stopped either when the user gives the
     * "quit" command, or when the method <code>stop()</code> is called.
     */
    public void handleCmds() {
        keepReceivingCmds = true;
        while (keepReceivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case LIST:
                        List<? extends InstrumentDTO> instruments = null;
                        if (cmdLine.getParameter(0).equals("")) {
                            System.out.println("Please enter command:");
                        }
                        else {
                            instruments = ctrl.getAllAvailableInstruments(cmdLine.getParameter(0));
                        }
                        for (InstrumentDTO instr : instruments) {
                            System.out.println("ID: " + instr.getInstrumentID() + ", "
                                    + "Name: " + instr.getInstrument() + ", "
                                    + "Brand: " + instr.getBrand() + ", " + "Price: "
                                    + instr.getPrice());
                        }
                        break;
                    case RENT:
                        ctrl.rentInstrument(Integer.parseInt(cmdLine.getParameter(0)), Integer.parseInt(cmdLine.getParameter(1)));
                        break;
                    case TERMINATE:
                        ctrl.terminateRental(Integer.parseInt(cmdLine.getParameter(0)));
                        break;
                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case QUIT:
                        keepReceivingCmds = false;
                        break;
                    default:
                        System.out.println("illegal command");
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }
}
