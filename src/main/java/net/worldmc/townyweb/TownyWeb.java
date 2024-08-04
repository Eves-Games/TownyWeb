package net.worldmc.townyweb;

import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWeb extends JavaPlugin {
    private final WebServer webServer;

    public TownyWeb() {
        webServer = new WebServer();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        webServer.start();
    }
}