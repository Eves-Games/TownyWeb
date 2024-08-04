package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.WebServer;
import net.worldmc.townyweb.adapters.*;
import net.worldmc.townyweb.utils.PaginationUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Nations {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final PaginationUtil<Nation> nationPaginationUtil;
    private final PaginationUtil<Town> townPaginationUtil;

    public Nations(WebServer webServer) {
        SerializerFactory serializerFactory = webServer.getSerializerFactory();
        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
        this.partialObjectMapper = serializerFactory.getPartialObjectMapper();
        this.nationPaginationUtil = webServer.getNationPaginationUtil();
        this.townPaginationUtil = webServer.getTownPaginationUtil();
    }

    private Nation getNationByUUID(String uuidParam) {
        if (uuidParam == null || uuidParam.isEmpty()) {
            throw new HttpResponseException(400, "Required path parameter 'uuid' is missing.");
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidParam);
        } catch (IllegalArgumentException e) {
            throw new HttpResponseException(400, "Invalid UUID format");
        }

        Nation nation = TownyAPI.getInstance().getNation(uuid);
        if (nation == null) {
            throw new HttpResponseException(404, "Nation not found");
        }

        return nation;
    }

    public void getNation(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        ctx.json(fullObjectMapper.valueToTree(nation));
    }

    public void getNations(Context ctx) {
        String searchQuery = ctx.queryParam("search");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Nation> allNations = TownyAPI.getInstance().getNations();

        List<Nation> filteredNations = allNations;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredNations = allNations.stream()
                    .filter(nation -> nation.getName().toLowerCase().contains(lowerCaseSearch))
                    .collect(Collectors.toList());
        }

        Map<String, Object> paginatedResult = nationPaginationUtil.paginateList(filteredNations, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }

    public void getNationTowns(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Town> allTowns = nation.getTowns();
        Map<String, Object> paginatedResult = townPaginationUtil.paginateList(allTowns, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }

    public void getNationRelationships(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        String type = ctx.queryParam("type");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Nation> relatedNations;
        if ("allies".equalsIgnoreCase(type)) {
            relatedNations = nation.getAllies();
        } else if ("enemies".equalsIgnoreCase(type)) {
            relatedNations = nation.getEnemies();
        } else {
            throw new HttpResponseException(400, "Invalid relationship type. Must be 'allies' or 'enemies'.");
        }

        Map<String, Object> paginatedResult = nationPaginationUtil.paginateList(relatedNations, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }

    public void getNationReceivedAllyInvites(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        ctx.json(fullObjectMapper.valueToTree(nation.getReceivedInvites()));
    }

    public void getNationSentAllyInvites(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        ctx.json(fullObjectMapper.valueToTree(nation.getSentAllyInvites()));
    }

    public void getNationSentInvites(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        ctx.json(fullObjectMapper.valueToTree(nation.getSentInvites()));
    }
}