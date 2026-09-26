package com.marcpg.faststats.compat.implementations;

import com.marcpg.faststats.compat.Compat;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class ForgeCompat extends Compat {
    private @Nullable MinecraftServer server = null;

    public ForgeCompat(String modId) {
        super();

        this.platformVersion = ModList.getModContainerById("forge")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
        this.configDir = FMLPaths.CONFIGDIR.get();

        ModContainer mod = ModList.getModContainerById(modId).orElseThrow(() -> new IllegalArgumentException("No mod found with ID: " + modId));
        this.modName = mod.getModInfo().getDisplayName();
        this.modVersion = mod.getModInfo().getVersion().toString();

        if (!this.isClient)
            waitForServer(() -> this.server = ServerLifecycleHooks.getCurrentServer());
    }

    @Override
    public MinecraftServer server() {
        assert server != null : "Server not initialized";
        return server;
    }

    @Override
    public void registerLifecycleEvents(Runnable ready, Runnable shutdown) {
        if (isClient) {
            ready.run();
        } else {
            waitForServer(ready);
        }
        // Because we can't use goofy forge events (super volatile event API for some reason), we just hook straight into the JVM:
        Runtime.getRuntime().addShutdownHook(new Thread(shutdown));
    }

    private void waitForServer(Runnable ready) {
        Thread.startVirtualThread(() -> {
            // Require 2 encounters to give the server some time (1-2 seconds) to actually start:
            int availableCounter = 0;
            while (2 > availableCounter) {
                if (ServerLifecycleHooks.getCurrentServer() != null)
                    availableCounter++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            server = ServerLifecycleHooks.getCurrentServer();
            ready.run();
        });
    }
}
