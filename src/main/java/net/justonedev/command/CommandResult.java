package net.justonedev.command;

public record CommandResult(ResultType resultType, String message, String printRawAnyway) {
    public static CommandResult success() {
        return success("");
    }
    public static CommandResult success(String message) {
        return new CommandResult(ResultType.SUCCESS, message, "");
    }
    public static CommandResult failure(String message) {
        return new CommandResult(ResultType.FAILURE, message, "");
    }
    public static CommandResult failure(String message, String printRawAnyway) {
        return new CommandResult(ResultType.FAILURE, message, printRawAnyway);
    }
}
