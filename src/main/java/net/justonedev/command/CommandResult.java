package net.justonedev.command;

public record CommandResult(ResultType resultType, String message) {
    public static CommandResult success() {
        return success("");
    }
    public static CommandResult success(String message) {
        return new CommandResult(ResultType.SUCCESS, message);
    }
    public static CommandResult failure(String message) {
        return new CommandResult(ResultType.FAILURE, message);
    }
}
