package se.kth.iv1351.soundgoodjdbc.startup;

import se.kth.iv1351.soundgoodjdbc.controller.Controller;
import se.kth.iv1351.soundgoodjdbc.integration.SoundGoodDBException;
import se.kth.iv1351.soundgoodjdbc.view.Interpreter;

/**
 * Starts the school client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new Interpreter(new Controller()).handleCmds();
        } catch(SoundGoodDBException sdbe) {
            System.out.println("Could not connect to SoundGood db.");
            sdbe.printStackTrace();
        }
    }
}
