package net.worldmc.townyweb;

import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.routes.Nations;
import net.worldmc.townyweb.routes.Residents;
import net.worldmc.townyweb.routes.Towns;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.javalin.Javalin;

public final class TownyWeb extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        startWebServer();
    }

    private void startWebServer() {
        FileConfiguration pluginConfig = getConfig();
        int port = pluginConfig.getInt("port", 7700);
        String apiKey = pluginConfig.getString("apiKey", "");

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

        Nations nations = new Nations();
        Towns towns = new Towns();
        Residents residents = new Residents();

        // Nations
        app.get("/nations", nations::getNations);
        app.get("/nation/{uuid}", nations::getNation);
        app.get("/nation/{uuid}/towns", nations::getNationTowns);

        // Towns
        app.get("/towns", towns::getTowns);
        app.get("/towns/{uuid}", towns::getTown);
        app.get("/towns/{uuid}/residents", towns::getTownResidents);

        // Residents
        app.get("/residents", residents::getResidents);
        app.get("/residents/{uuid}", residents::getResident);
        app.get("/residents/{uuid}/friends", residents::getResidentFriends);
    }
}