package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The CommandHandler. Processes all IO Communication.
 * @author uwwfh
 */
public class CommandHandler {
    /**
     * The delimiter for command line arguments.
     */
    public static final String ARGUMENT_DELIMITER = " ";
    /**
     * The error message for when no arguments were expected, but some were provided.
     */
    public static final String ERROR_INVALID_ARGUMENTS_ZERO = "Invalid number of arguments: %d (Expected 0)";
    /**
     * The amount of arguments expected on an argument-less command.
     */
    public static final int NO_ARGUMENTS_LENGTH = 0;

    private static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_UNKNOWN_COMMAND = ERROR_PREFIX + "Unknown command: \"%s\"%n";

    private static final String COMMAND_NAME_ADD = "add";
    private static final String COMMAND_NAME_EDGES = "edges";
    private static final String COMMAND_NAME_EXPORT = "export";
    private static final String COMMAND_NAME_LOAD = "load";
    private static final String COMMAND_NAME_NODES = "nodes";
    private static final String COMMAND_NAME_QUIT = "quit";
    private static final String COMMAND_NAME_RECOMMEND = "recommend";
    private static final String COMMAND_NAME_REMOVE = "remove";

    private static final int COMMAND_NAME_INDEX = 0;
    private static final int COMMAND_ARGUMENTS_BEGIN_INDEX = 1;

    private final Map<String, Command> commands;
    private final RecommendationSystem recommendationSystem;
    private boolean running;

    /**
     * Creates a new CommandHandler and instantiates a new RecommendationSystem internally.
     * All Commands are automatically registered.
     */
    public CommandHandler() {
        this.commands = new HashMap<>();
        this.recommendationSystem = new RecommendationSystem(this);
        registerCommands();
    }

    /**
     * Terminates the program in a controlled manner.
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Starts processing commands from the IO command line. Reads and executes commands until 'quit' is entered or
     * the {@linkplain #terminate()} method is called.
     */
    public void start() {
        running = true;
        try (Scanner scanner = new Scanner(System.in)) {
            String line;
            while (running && (line = scanner.nextLine()) != null) {
                String[] splitCommand = line.split(ARGUMENT_DELIMITER);
                String cmdName = splitCommand[COMMAND_NAME_INDEX];
                String[] args = Arrays.copyOfRange(splitCommand, COMMAND_ARGUMENTS_BEGIN_INDEX, splitCommand.length);
                Command command = commands.get(cmdName);
                if (command == null) {
                    System.out.printf(ERROR_UNKNOWN_COMMAND, cmdName);
                    continue;
                }

                CommandResult result = command.execute(recommendationSystem, args);


                String message = switch (result.resultType()) {
                    case SUCCESS -> result.message();
                    case FAILURE -> ERROR_PREFIX + result.message();
                };
                if (!result.printRawAnyway().isEmpty()) {
                    System.out.println(result.printRawAnyway());
                }
                if (!message.isEmpty()) {
                    System.out.println(message.trim());
                }
            }
        }
    }

    private void registerCommands() {
        commands.put(COMMAND_NAME_ADD, new AddCommand());
        commands.put(COMMAND_NAME_EDGES, new EdgesCommand());
        commands.put(COMMAND_NAME_EXPORT, new ExportCommand());
        commands.put(COMMAND_NAME_LOAD, new LoadCommand());
        commands.put(COMMAND_NAME_NODES, new NodesCommand());
        commands.put(COMMAND_NAME_QUIT, new QuitCommand());
        commands.put(COMMAND_NAME_RECOMMEND, new RecommendCommand());
        commands.put(COMMAND_NAME_REMOVE, new RemoveCommand());
    }
}
