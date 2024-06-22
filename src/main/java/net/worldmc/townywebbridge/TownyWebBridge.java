package net.worldmc.townywebbridge;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.community.ssl.SslPlugin;
import io.javalin.http.HttpResponseException;
import net.worldmc.townywebbridge.routes.Residents;
import net.worldmc.townywebbridge.routes.Towns;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.javalin.Javalin;

import java.io.File;
import java.util.List;

public final class TownyWebBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        startWebServer();
    }

    private void startWebServer() {
        FileConfiguration pluginConfig = getConfig();
        int port = pluginConfig.getInt("port", 7700);
        String keyStorePath = pluginConfig.getString("tls.keyStore", "keystore.jks");
        String keyStorePassword = pluginConfig.getString("tls.keyStorePassword", "");
        String apiKey = pluginConfig.getString("apiKey", "");
        List<String> corsHosts = pluginConfig.getStringList("cors");

        final String fullKeyStorePath = getDataFolder().getAbsolutePath() + File.separator + keyStorePath;

        SslPlugin sslPlugin = new SslPlugin(conf -> {
            conf.keystoreFromPath(fullKeyStorePath, keyStorePassword);
            conf.http2 = false;
            conf.insecure = false;
            conf.secure = true;
            conf.securePort = port;
            conf.sniHostCheck = false;
        });

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(sslPlugin);
            config.showJavalinBanner = false;
            config.bundledPlugins.enableCors(cors -> cors.addRule(corsRule -> {
                if (corsHosts.contains("*")) {
                  corsRule.anyHost();
                } else {
                    corsHosts.forEach(corsRule::allowHost);
                };
            }));
        }).start(7700);

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

        getLogger().info("JavalinPlugin is enabled");
    }
}