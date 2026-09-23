package com.marcpg.faststats;

import com.google.gson.JsonObject;
import com.marcpg.faststats.compat.Compat;
import com.marcpg.faststats.compat.PlatformManager;
import dev.faststats.Metrics;
import dev.faststats.SimpleContext;
import dev.faststats.SimpleMetrics;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("UnstableApiUsage")
final class ModMetrics extends SimpleMetrics {
    ModMetrics(Factory factory) throws IllegalStateException {
        super(factory);
    }

    @Override
    protected void appendDefaultData(JsonObject metrics) {
        Compat compat = PlatformManager.compat();

        metrics.addProperty("online_mode", compat.isOnline());
        metrics.addProperty("player_count", compat.playerCount());
        metrics.addProperty("game_version", SharedConstants.getCurrentVersion().name());
        metrics.addProperty("platform_version", compat.platformVersion());
        metrics.addProperty("plugin_version", compat.modVersion());
        metrics.addProperty("server_type", PlatformManager.platform().name + (compat.isClient() ? " Client" : ""));
    }

    @Override
    public boolean isClientApplication() {
        return PlatformManager.compat().isClient();
    }

    public static final class Factory extends SimpleMetrics.Factory {
        Factory(SimpleContext context, String modId) {
            super(context);
        }

        @Override
        public @NonNull Metrics create() throws IllegalStateException {
            return new ModMetrics(this);
        }
    }
}
