package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandHandler {
    private final Map<String, Command> commands;
    private final RecommendationSystem recommendationSystem;
    private boolean running;

    public CommandHandler() {
        this.commands = new HashMap<>();
        this.recommendationSystem = new RecommendationSystem(this);
        registerCommands();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void start() {
        running = true;
        try (Scanner scanner = new Scanner(System.in)) {
            String line;
            while (running && (line = scanner.nextLine()) != null) {
                String[] splitCommand = line.split(" ");
                String cmdName = splitCommand[0];
                String[] args = Arrays.copyOfRange(splitCommand, 1, splitCommand.length);
                Command command = commands.get(cmdName);
                if (command == null) {
                    System.out.printf("Unknown command: \"%s\"%n", cmdName);
                    continue;
                }

                CommandResult result = command.execute(recommendationSystem, args);


                String message = switch (result.resultType()) {
                    case SUCCESS -> result.message();
                    case FAILURE -> "Error, %s".formatted(result.message());
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
        commands.put("quit", new QuitCommand());
        commands.put("export", new ExportCommand());
        commands.put("edges", new EdgesCommand());
        commands.put("nodes", new NodesCommand());
        commands.put("add", new AddCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("load", new LoadCommand());
        commands.put("recommend", new RecommendCommand());
    }
}
