package com.marcpg.faststats.compat.implementations;

import com.marcpg.faststats.compat.Compat;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jetbrains.annotations.Nullable;

public class NeoForgeCompat extends Compat {
    private @Nullable MinecraftServer server = null;

    public NeoForgeCompat(String modId) {
        super();

        this.platformVersion = ModList.get().getModContainerById("neoforge")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
        this.configDir = FMLPaths.CONFIGDIR.get();

        ModContainer mod = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalArgumentException("No mod found with ID: " + modId));
        this.modName = mod.getModInfo().getDisplayName();
        this.modVersion = mod.getModInfo().getVersion().toString();

        if (!this.isClient)
            NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> this.server = event.getServer());
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
            NeoForge.EVENT_BUS.addListener((ClientStoppingEvent event) -> shutdown.run());
        } else {
            NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> ready.run());
            NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> shutdown.run());
        }
    }
}
