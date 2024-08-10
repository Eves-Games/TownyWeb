package net.worldmc.townyweb;

import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.Javalin;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.utils.SerializerFactory;
import net.worldmc.townyweb.routes.Nations;
import net.worldmc.townyweb.routes.Residents;
import net.worldmc.townyweb.routes.Towns;
import net.worldmc.townyweb.utils.PaginationUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class WebServer {
    private final SerializerFactory serializerFactory;
    private final PaginationUtil<Nation> nationPaginationUtil;
    private final PaginationUtil<Town> townPaginationUtil;
    private final PaginationUtil<Resident> residentPaginationUtil;
    private final PaginationUtil<Invite> invitePaginationUtil;
    private final FileConfiguration config;

    public WebServer(TownyWeb plugin) {
        config = plugin.getConfig();
        serializerFactory = new SerializerFactory();
        nationPaginationUtil = new PaginationUtil<>();
        townPaginationUtil = new PaginationUtil<>();
        residentPaginationUtil = new PaginationUtil<>();
        invitePaginationUtil = new PaginationUtil<>();
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

        Nations nations = new Nations(this);
        Towns towns = new Towns(this);
        Residents residents = new Residents(this);

        app.get("/nations", nations::getNations);
        app.get("/nations/{uuid}", nations::getNation);

        app.get("/towns", towns::getTowns);
        app.get("/towns/{uuid}", towns::getTown);

        app.get("/residents", residents::getResidents);
        app.get("/residents/{uuid}", residents::getResident);
    }

    public SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }

    public PaginationUtil<Nation> getNationPaginationUtil() {
        return nationPaginationUtil;
    }

    public PaginationUtil<Town> getTownPaginationUtil() {
        return townPaginationUtil;
    }

    public PaginationUtil<Resident> getResidentPaginationUtil() {
        return residentPaginationUtil;
    }

    public PaginationUtil<Invite> getInvitePaginationUtil() {
        return invitePaginationUtil;
    }
}
