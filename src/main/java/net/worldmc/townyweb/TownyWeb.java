package net.worldmc.townyweb;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import net.worldmc.townyweb.towny.commands.NationBannerCommand;
import net.worldmc.townyweb.towny.commands.TownBannerCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.javalin.Javalin;

import java.util.Map;

import static net.worldmc.townyweb.WebServer.setupRoutes;

public class TownyWeb extends JavaPlugin {
    @Override
    public void onEnable() {
        initializeJavalin();

        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.TOWN_SET, "banner", new TownBannerCommand());
        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.NATION_SET, "banner", new NationBannerCommand());
    }

    private void initializeJavalin() {
        FileConfiguration config = getConfig();
        int port = config.getInt("port", 7700);
        String apiKey = config.getString("apiKey", "");

        Javalin app = Javalin.create(jConfig -> {
            jConfig.showJavalinBanner = false;
            jConfig.useVirtualThreads = true;
        });

        app.before(ctx -> {
            if (!apiKey.isEmpty()) {
                String requestApiKey = ctx.header("apiKey");
                if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                    ctx.status(401).json(Map.of("error", "Unauthorized: Invalid API key"));
                }
            }
        });

        setupRoutes(app);

        app.start(port);
    }
}