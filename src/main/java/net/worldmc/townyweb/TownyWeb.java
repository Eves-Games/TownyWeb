package net.worldmc.townyweb;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import net.worldmc.townyweb.towny.commands.NationBannerCommand;
import net.worldmc.townyweb.towny.commands.TownBannerCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWeb extends JavaPlugin {
    private SerializerFactory serializerFactory;
    private WebServer webServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        serializerFactory = new SerializerFactory();
        webServer = new WebServer(this);

        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.TOWN_SET, "banner", new TownBannerCommand(this));
        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.NATION_SET, "banner", new NationBannerCommand(this));
    }

    public SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }

    public WebServer getWebServer() {
        return webServer;
    }
}