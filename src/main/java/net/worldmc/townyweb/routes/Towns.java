package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.WebServer;
import net.worldmc.townyweb.utils.SerializerFactory;
import net.worldmc.townyweb.utils.PaginationUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Towns {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final PaginationUtil<Town> townPaginationUtil;
    private final PaginationUtil<Resident> residentPaginationUtil;

    public Towns(WebServer webServer) {
        SerializerFactory serializerFactory = webServer.getSerializerFactory();
        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
        this.partialObjectMapper = serializerFactory.getPartialObjectMapper();
        this.townPaginationUtil = webServer.getTownPaginationUtil();
        this.residentPaginationUtil = webServer.getResidentPaginationUtil();
    }

    private Town getTownByUUID(String uuidParam) {
        if (uuidParam == null || uuidParam.isEmpty()) {
            throw new HttpResponseException(400, "Required path parameter 'uuid' is missing.");
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidParam);
        } catch (IllegalArgumentException e) {
            throw new HttpResponseException(400, "Invalid UUID format");
        }

        Town town = TownyAPI.getInstance().getTown(uuid);
        if (town == null) {
            throw new HttpResponseException(404, "Town not found");
        }

        return town;
    }

    public void getTown(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Town town = getTownByUUID(uuidParam);
        ctx.json(fullObjectMapper.valueToTree(town));
    }

    public void getTowns(Context ctx) {
        String searchQuery = ctx.queryParam("search");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Town> allTowns = TownyAPI.getInstance().getTowns();

        List<Town> filteredTowns = allTowns;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredTowns = allTowns.stream()
                    .filter(town -> town.getName().toLowerCase().contains(lowerCaseSearch))
                    .collect(Collectors.toList());
        }

        Map<String, Object> paginatedResult = townPaginationUtil.paginateList(filteredTowns, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }

    public void getTownResidents(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Town town = getTownByUUID(uuidParam);

        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        List<Resident> allResidents = town.getResidents();

        Map<String, Object> paginatedResult = residentPaginationUtil.paginateList(allResidents, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }
}