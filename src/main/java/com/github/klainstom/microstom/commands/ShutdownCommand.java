package com.github.klainstom.microstom.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ServerSender;
import net.minestom.server.command.builder.Command;

public class ShutdownCommand extends Command {
    public ShutdownCommand() {
        super("shutdown", "end", "stop");
        setCondition((sender, commandString) -> (sender instanceof ServerSender));
        setDefaultExecutor((sender, context) -> MinecraftServer.stopCleanly());
    }
}
