package com.marcpg.faststats.compat;

import java.util.Arrays;

public final class PlatformManager {
    private static Compat compat;
    private static Platforms platform;

    public static void init(String modId) {
        if (compat != null)
            return;

        Platforms foundPlatform = Arrays.stream(Platforms.values())
                .filter(Platforms::isAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No mod loader platform found!"));

        try {
            compat = (Compat) Class.forName(foundPlatform.implementation).getConstructor(String.class).newInstance(modId);
            platform = foundPlatform;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Compat compat() { return compat; }
    public static Platforms platform() { return platform; }
}
