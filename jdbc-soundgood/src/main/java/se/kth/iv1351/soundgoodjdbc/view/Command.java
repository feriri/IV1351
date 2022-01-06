package se.kth.iv1351.soundgoodjdbc.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 */
public enum Command {
    /**
     * Lists all existing accounts.
     */
    LIST,
    RENT,
    TERMINATE,
    HELP,
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}
