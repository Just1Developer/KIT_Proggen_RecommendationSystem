package net.justonedev;

import net.justonedev.command.CommandHandler;

/**
 * The main class and entry point of the application.
 * @author uwwfh
 */
public final class Main {
    private Main() { }

    /**
     * The main entry point of the recommendation system.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        CommandHandler handler = new CommandHandler();
        handler.start();
    }
}
