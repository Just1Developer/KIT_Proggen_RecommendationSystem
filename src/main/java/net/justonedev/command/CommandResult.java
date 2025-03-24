package net.justonedev.command;

/**
 * The result of commands. Includes the result type (success or failure), the message, and something to print
 * beforehand anyway. The printRawAnyway parameter will be printed only if not empty, and does not receive
 * the error prefix.
 * It is intended to print something before the error line is printed on FAILURE.
 *
 * @author uwwfh
 *
 * @param resultType The result type, success or failure.
 * @param message The message to print. Will receive error prefix on failure.
 * @param printRawAnyway Print something before the message parameter anyway. Will not receive error prefix on failure.
 */
public record CommandResult(ResultType resultType, String message, String printRawAnyway) {
    /**
     * Returns a successful command result, no message will be printed.
     * @return A successful command result.
     */
    public static CommandResult success() {
        return success("");
    }

    /**
     * Returns a successful command result, the given message will be printed as-is, if not an empty string.
     * Spaces do count as a non-empty string.
     * @param message The message to print.
     * @return A successful command result.
     */
    public static CommandResult success(String message) {
        return new CommandResult(ResultType.SUCCESS, message, "");
    }

    /**
     * Returns a failed command result. Will print the given message with the common "Error, " prefix.
     * @param message The error message.
     * @return The failure command result, with error message.
     */
    public static CommandResult failure(String message) {
        return new CommandResult(ResultType.FAILURE, message, "");
    }

    /**
     * Returns a failed command result. Will print the given message with the common "Error, " prefix.
     * Will also print a different given message before that, which will not receive the Error prefix automatically.
     * @param message The error message.
     * @param printRawAnyway The message to print anyway beforehand. Will not receive the error prefix.
     * @return The failure command result, with error message and the print anyway message.
     */
    public static CommandResult failure(String message, String printRawAnyway) {
        return new CommandResult(ResultType.FAILURE, message, printRawAnyway);
    }
}
