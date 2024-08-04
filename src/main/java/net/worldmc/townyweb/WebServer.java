package net.worldmc.townyweb;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.Javalin;
import net.worldmc.townyweb.adapters.SerializerFactory;
import net.worldmc.townyweb.routes.Nations;
import net.worldmc.townyweb.routes.Residents;
import net.worldmc.townyweb.routes.Towns;
import net.worldmc.townyweb.utils.PaginationUtil;
import org.bukkit.configuration.file.FileConfiguration;

import static com.palmergames.bukkit.towny.TownySettings.getConfig;

public class WebServer {
    private final SerializerFactory serializerFactory;
    private final PaginationUtil<Nation> nationPaginationUtil;
    private final PaginationUtil<Town> townPaginationUtil;
    private final PaginationUtil<Resident> residentPaginationUtil;

    public WebServer() {
        serializerFactory = new SerializerFactory();
        nationPaginationUtil = new PaginationUtil<>();
        townPaginationUtil = new PaginationUtil<>();
        residentPaginationUtil = new PaginationUtil<>();
    }

    public void start() {
        FileConfiguration pluginConfig = getConfig();
        int port = pluginConfig.getInt("port", 7700);
        String apiKey = pluginConfig.getString("apiKey", "");

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.useVirtualThreads = true;
        }).start(port);

        /**app.before(ctx -> {
         if (!apiKey.isEmpty()) {
         String requestApiKey = ctx.header("apiKey");
         if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
         throw new HttpResponseException(401, "Unauthorized: Invalid API key");
         };
         };
         });*/

        Nations nations = new Nations(this);
        Towns towns = new Towns(this);
        Residents residents = new Residents(this);

        // Nations
        app.get("/nations", nations::getNations); // page and search query
        app.get("/nation/{uuid}", nations::getNation);
        app.get("/nation/{uuid}/towns", nations::getNationTowns);
        app.get("/nation/{uuid}/relationships", nations::getNationRelationships); // type query (allies or enemies)

        // Towns
        app.get("/towns", towns::getTowns); // page and search query
        app.get("/towns/{uuid}", towns::getTown);
        app.get("/towns/{uuid}/residents", towns::getTownResidents);

        // Residents
        app.get("/residents", residents::getResidents); // page and search query
        app.get("/residents/{uuid}", residents::getResident);
        app.get("/residents/{uuid}/friends", residents::getResidentFriends);
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
}
