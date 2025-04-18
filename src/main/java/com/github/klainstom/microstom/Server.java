package com.github.klainstom.microstom;

import com.github.klainstom.microstom.commands.Commands;
import com.github.klainstom.microstom.terminal.TerminalConsole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.Git;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class Server {
    private static final String START_SCRIPT_FILENAME_LINUX = "start.sh";
    private static final String START_SCRIPT_FILENAME_WINDOWS = "start.bat";

    private static void setupStartupScript() throws IOException {
        final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        final String scriptFileName = isWindows ? START_SCRIPT_FILENAME_WINDOWS : START_SCRIPT_FILENAME_LINUX;
        final String startCommand = isWindows ? "start.bat" : "start.sh";

        File startScriptFile = new File(scriptFileName);

        if (startScriptFile.isDirectory()) MinecraftServer.LOGGER.warn("Can't create startup script!");
        if (!startScriptFile.isFile()) {
            MinecraftServer.LOGGER.info("Create startup script.");
            Files.copy(
                    Objects.requireNonNull(Server.class.getClassLoader().getResourceAsStream(scriptFileName)),
                    startScriptFile.toPath());
            MinecraftServer.LOGGER.info("Use '{}' to start the server.", startCommand);
            System.exit(0);
        }
    }

    private static void printVersions(String[] args) {
        if (!(args.length > 0 && args[0].equalsIgnoreCase("-q"))) {
            MinecraftServer.LOGGER.info("====== VERSIONS ======");
            MinecraftServer.LOGGER.info("Java: {}", Runtime.version());
            MinecraftServer.LOGGER.info("&Name: &version");
            MinecraftServer.LOGGER.info("Minestom commit: {}", Git.commit());
            MinecraftServer.LOGGER.info("Supported protocol: %d (%s)".formatted(MinecraftServer.PROTOCOL_VERSION, MinecraftServer.VERSION_NAME));
            MinecraftServer.LOGGER.info("======================");
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("-v")) System.exit(0);
    }

    private static void registerCommands() {
        var commandManager = MinecraftServer.getCommandManager();
        commandManager.register(Commands.SHUTDOWN);
        commandManager.register(Commands.RESTART);
    }

    private static void startServer() {
        Settings.read();
        if (Settings.getTps() != null)
            System.setProperty("minestom.tps", Settings.getTps());
        if (Settings.getChunkViewDistance() != null)
            System.setProperty("minestom.chunk-view-distance", Settings.getChunkViewDistance());
        if (Settings.getEntityViewDistance() != null)
            System.setProperty("minestom.entity-view-distance", Settings.getEntityViewDistance());
        if (Settings.isTerminalDisabled())
            System.setProperty("minestom.terminal.disabled", "");

        MinecraftServer server = MinecraftServer.init();

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if (MinecraftServer.getInstanceManager().getInstances().isEmpty())
                event.getPlayer().kick(Component.text("There is no instance available!", NamedTextColor.RED));
        });

        switch (Settings.getMode()) {
            case OFFLINE:
                break;
            case ONLINE:
                MojangAuth.init();
                break;
            case BUNGEECORD:
                BungeeCordProxy.enable();
                break;
            case VELOCITY:
                if (!Settings.hasVelocitySecret()) {
                    throw new IllegalArgumentException("The velocity secret is mandatory.");
                }
                VelocityProxy.enable(Settings.getVelocitySecret());
        }

        registerCommands();

        MinecraftServer.LOGGER.info("Running in " + Settings.getMode() + " mode.");
        MinecraftServer.LOGGER.info("Listening on " + Settings.getServerIp() + ":" + Settings.getServerPort());

        server.start(Settings.getServerIp(), Settings.getServerPort());

        new TerminalConsole().start();
    }

    public static void main(String[] args) throws IOException {
        printVersions(args);
        setupStartupScript();
        startServer();
    }
}