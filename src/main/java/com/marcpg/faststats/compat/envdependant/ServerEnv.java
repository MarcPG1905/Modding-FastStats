package com.marcpg.faststats.compat.envdependant;

import com.marcpg.faststats.compat.PlatformManager;

@SuppressWarnings("resource")
public class ServerEnv implements EnvDependant {
    @Override
    public boolean isOnline() {
        return PlatformManager.compat().server().usesAuthentication();
    }

    @Override
    public int playerCount() {
        return PlatformManager.compat().server().getPlayerCount();
    }
}
