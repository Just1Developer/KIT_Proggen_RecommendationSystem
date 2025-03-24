package net.justonedev.command;

import net.justonedev.model.Parser;
import net.justonedev.model.ParseGraphResult;
import net.justonedev.model.RecommendationSystem;
import net.justonedev.model.stream.DataStream;

import java.util.List;
import java.util.StringJoiner;

/**
 * The load command attempts to load a database graph from a specified file. The command prints the file contents
 * regardless of success/failure.
 * @author uwwfh
 */
public class LoadCommand implements Command {
    private static final String ERROR_NOT_ENOUGH_ARGUMENTS = "Not enough arguments: Required \"load database <filepath>\"";
    private static final String ERROR_FAILED_FILE_PARSE = "Failed to parse database from file (Path: %s)";

    private static final int MINIMUM_ARGUMENT_LENGTH = 2;
    private static final int FILEPATH_ARG_INDEX_BEGIN = 1;

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length < MINIMUM_ARGUMENT_LENGTH) {
            return CommandResult.failure(ERROR_NOT_ENOUGH_ARGUMENTS);
        }
        String filePath = getFilepath(args);
        ParseGraphResult parseGraphResult = Parser.parseGraph(filePath);
        if (parseGraphResult.graph() != null) {
            system.loadGraph(parseGraphResult.graph());
            return CommandResult.success(formatLines(parseGraphResult.fileLines()));
        }
        return CommandResult.failure(ERROR_FAILED_FILE_PARSE.formatted(filePath),
                formatLines(parseGraphResult.fileLines()));
    }

    private static String formatLines(List<String> lines) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        DataStream.of(lines).forEach(joiner::add);
        return joiner.toString();
    }

    private static String getFilepath(String[] args) {
        StringJoiner joiner = new StringJoiner(CommandHandler.ARGUMENT_DELIMITER);
        for (int i = FILEPATH_ARG_INDEX_BEGIN; i < args.length; i++) {
            joiner.add(args[i]);
        }
        return joiner.toString();
    }
}
