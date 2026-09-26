package com.marcpg.faststats.compat.envdependant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;

public class ClientEnv implements EnvDependant {
    @Override
    public boolean isOnline() {
        return Minecraft.getInstance().getUser().getXuid().isPresent();
    }

    @Override
    public int playerCount() {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener con = client.getConnection();
        if (con != null)
            return con.getOnlinePlayers().size();

        IntegratedServer server = client.getSingleplayerServer();
        if (server != null)
            return server.getPlayerCount();

        return client.player == null ? 0 : 1;
    }
}
