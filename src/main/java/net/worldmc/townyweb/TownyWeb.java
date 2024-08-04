package net.worldmc.townyweb;

import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWeb extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        WebServer webServer = new WebServer(this);
        webServer.start();
    }
}