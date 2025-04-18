package com.github.klainstom.microstom.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;

import java.io.IOException;

public class RestartCommand extends Command {
    public RestartCommand() {
        super("restart");
        setCondition((sender, commandString) -> (sender instanceof ConsoleSender));
        setDefaultExecutor((sender, context) -> {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (System.getProperty("os.name").startsWith("Windows")) {
                        new ProcessBuilder("./start.bat").start();
                        MinecraftServer.LOGGER.info("Start new server.");
                    }
                    else {
                        new ProcessBuilder("./start.sh").start();
                        MinecraftServer.LOGGER.info("Start new server.");
                    }
                } catch (IOException e) {
                    if (!(sender instanceof ConsoleSender)) sender.sendMessage("Could not restart server.");
                    MinecraftServer.LOGGER.error("Could not restart server.", e);
                }
            }, "RestartHook"));
            MinecraftServer.stopCleanly();
        });
    }
}
