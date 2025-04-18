package com.github.klainstom.microstom.terminal;

import net.minecrell.terminalconsole.SimpleTerminalConsole;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;

public class TerminalConsole extends SimpleTerminalConsole {

    private final CommandManager commandManager = MinecraftServer.getCommandManager();

    @Override
    protected boolean isRunning() {
        return MinecraftServer.isStarted() && !MinecraftServer.isStopping();
    }

    @Override
    protected void runCommand(String command) {
        commandManager.execute(commandManager.getConsoleSender(), command);
    }

    @Override
    protected void shutdown() {
        MinecraftServer.LOGGER.info("Shutting down server...");
        try {
            MinecraftServer.stopCleanly();
            System.exit(0);
        } catch (Throwable t) {
            System.exit(1);
        }
    }

}