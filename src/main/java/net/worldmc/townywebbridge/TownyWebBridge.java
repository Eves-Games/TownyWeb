package net.worldmc.townywebbridge;

import io.javalin.community.ssl.SslPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.javalin.Javalin;
import io.javalin.http.Context;

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
                    ctx.status(401).result("Unauthorized: Invalid API key");
                }
            };
        });

        app.get("/", this::handleRoot);

        getLogger().info("JavalinPlugin is enabled");
    }

    private void handleRoot(Context ctx) {
        ctx.result("Worked or something");
    }
}