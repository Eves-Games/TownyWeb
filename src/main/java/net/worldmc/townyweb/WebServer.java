package net.worldmc.townyweb;

import io.javalin.Javalin;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.routes.Shops;
import net.worldmc.townyweb.routes.Nations;
import net.worldmc.townyweb.routes.Residents;
import net.worldmc.townyweb.routes.Towns;
import org.bukkit.configuration.file.FileConfiguration;

public class WebServer {
    private final FileConfiguration config;
    private final TownyWeb townyWeb;

    public WebServer(TownyWeb plugin) {
        townyWeb = plugin;
        config = plugin.getConfig();

        this.start();
    }

    public void start() {
        int port = config.getInt("port", 7700);
        String apiKey = config.getString("apiKey", "");

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.useVirtualThreads = true;
        }).start(port);

        app.before(ctx -> {
            if (!apiKey.isEmpty()) {
                String requestApiKey = ctx.header("apiKey");
                if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                    throw new HttpResponseException(401, "Unauthorized: Invalid API key");
                };
            };
         });

        Nations nations = new Nations(townyWeb);
        Towns towns = new Towns(townyWeb);
        Residents residents = new Residents(townyWeb);
        Shops shops = new Shops(townyWeb);

        app.get("/nations", nations::getNations);
        app.get("/nations/{uuid}", nations::getNation);

        app.get("/towns", towns::getTowns);
        app.get("/towns/{uuid}", towns::getTown);

        app.get("/residents", residents::getResidents);
        app.get("/residents/{uuid}", residents::getResident);

        app.get("/shops", shops::getShops);
    }
}
