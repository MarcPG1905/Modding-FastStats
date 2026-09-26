package com.marcpg.faststats.compat.implementations;

import com.marcpg.faststats.compat.Compat;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class FabricCompat extends Compat {
    private @Nullable MinecraftServer server = null;

    public FabricCompat(String modId) {
        super();

        FabricLoader loader = FabricLoader.getInstance();

        // noinspection SpellCheckingInspection - because of "fabricloader"
        this.platformVersion = loader.getModContainer("fabricloader")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        this.configDir = loader.getConfigDir();

        ModContainer mod = loader.getModContainer(modId).orElseThrow(() -> new IllegalArgumentException("No mod found with ID: " + modId));
        this.modName = mod.getMetadata().getName();
        this.modVersion = mod.getMetadata().getVersion().getFriendlyString();

        if (!this.isClient)
            ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
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
            ClientLifecycleEvents.CLIENT_STOPPING.register(client -> shutdown.run());
        } else {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> ready.run());
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> shutdown.run());
        }
    }
}
