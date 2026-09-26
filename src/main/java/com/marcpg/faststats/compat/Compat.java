package com.marcpg.faststats.compat;

import com.marcpg.faststats.compat.envdependant.ClientEnv;
import com.marcpg.faststats.compat.envdependant.EnvDependant;
import com.marcpg.faststats.compat.envdependant.ServerEnv;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public abstract class Compat {
    protected boolean isClient;
    protected EnvDependant env;

    protected String platformVersion;
    protected Path configDir;
    protected String modName;
    protected String modVersion;

    public Compat() {
        try {
            isClient = Thread.currentThread().getContextClassLoader().getResource("net/minecraft/client/Minecraft.class") != null;
        } catch (Exception e) {
            isClient = false;
        }

        if (isClient) {
            env = new ClientEnv();
        } else {
            env = new ServerEnv();
        }
    }

    public abstract MinecraftServer server();
    public abstract void registerLifecycleEvents(Runnable ready, Runnable shutdown);

    // Semi-final

    public String platformVersion() { return platformVersion; }
    public Path configDir() { return configDir; }

    public String modName() { return modName; }
    public String modVersion() { return modVersion; }
    public boolean isClient() { return isClient; }

    // Other Metrics

    public final boolean isOnline() { return env.isOnline(); }
    public final int playerCount() { return env.playerCount(); }
}
