package com.marcpg.faststats.compat;

public enum Platforms {
    FABRIC("Fabric", ""), // TODO: Set checked classes!
    FORGE("Forge", ""),
    NEOFORGE("NeoForge", "");

    private static final String BASE_PACKAGE = "com.marcpg.faststats.compat.implementations";

    public final String name;
    public final String checkedClass;
    public final String implementation;

    Platforms(String name, String checkedClass) {
        this.name = name;
        this.checkedClass = checkedClass;
        this.implementation = BASE_PACKAGE + name + "Compat";
    }

    public boolean isAvailable() {
        String resource = checkedClass.replace('.', '/') + ".class";
        try { // Use this weird checking procedure to really only check instead of trying to initialize.
            return Thread.currentThread().getContextClassLoader().getResource(resource) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
