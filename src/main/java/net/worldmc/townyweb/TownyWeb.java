package net.worldmc.townyweb;

import io.javalin.http.HttpResponseException;
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

        // Towns
        // Public
        app.get("/towns", context -> new Towns().getTowns(context)); // Gets a list of towns in the server
        app.get("/towns/{uuid}", context -> new Towns().getTown(context)); // Gets town information
        app.get("/towns/{uuid}/residents", context -> new Towns().getTownResidents(context)); // Gets a list of residents in a town

        // Authenticated with session and permissions
        app.post("/towns/create", context -> new Towns().createTown(context)); // Creates a new town
        app.post("/towns/{uuid}/residents", context -> context.status(200)); // Joins a town
        app.get("/towns/{uuid}/join-requests", context -> context.status(200)); // Gets a list of join requests for a town
        app.post("/towns/{uuid}/join-requests", context -> context.status(200)); // Accepts town join requests, batch if no resident UUID specified
        app.delete("/towns/{uuid}/join-requests", context -> context.status(200)); // Deletes town join requests, batch if no resident UUID specified

        // Residents
        // Public
        app.get("/residents", context -> new Residents().getResidents(context));
        app.get("/residents/{uuid}", context -> new Residents().getResident(context));
        app.get("/residents/{uuid}/friends", context -> new Residents().getResidentFriends(context));
    }
}