package com.marcpg.faststats.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

@SuppressWarnings("resource")
public abstract class Compat {
    protected String platformVersion;
    protected Path configDir;
    protected String modName;
    protected String modVersion;
    protected boolean isClient;

    protected abstract MinecraftServer server();
    public abstract void registerLifecycleEvents(Runnable ready, Runnable shutdown);

    // Semi-final

    public String platformVersion() { return platformVersion; }
    public Path configDir() { return configDir; }

    public String modName() { return modName; }
    public String modVersion() { return modVersion; }
    public boolean isClient() { return isClient; }

    // Other Metrics

    public boolean isOnline() {
        if (isClient) {
            return Minecraft.getInstance().getUser().getXuid().isPresent();
        } else {
            return server().usesAuthentication();
        }
    }

    public int playerCount() {
        if (isClient) {
            Minecraft client = Minecraft.getInstance();
            ClientPacketListener con = client.getConnection();
            if (con != null)
                return con.getOnlinePlayers().size();

            IntegratedServer server = client.getSingleplayerServer();
            if (server != null)
                return server.getPlayerCount();

            return client.player == null ? 0 : 1;
        } else {
            return server().getPlayerCount();
        }
    }
}
