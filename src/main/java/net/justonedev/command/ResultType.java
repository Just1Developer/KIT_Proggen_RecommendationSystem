package net.justonedev.command;

/**
 * The result type of any command.
 * @author uwwfh
 */
public enum ResultType {
    /**
     * The command was successfully executed.
     */
    SUCCESS,
    /**
     * An error occurred during execution, there will be an error message.
     */
    FAILURE
}
