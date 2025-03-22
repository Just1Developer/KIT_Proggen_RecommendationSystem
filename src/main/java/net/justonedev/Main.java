package net.justonedev;

import net.justonedev.command.CommandHandler;

public class Main {
    public static void main(String[] args) {
        CommandHandler handler = new CommandHandler();
        handler.start();
    }
}