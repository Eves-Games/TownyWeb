package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.WebServer;
import net.worldmc.townyweb.adapters.SerializerFactory;
import net.worldmc.townyweb.utils.PaginationUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Residents {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final PaginationUtil<Resident> residentPaginationUtil;

    public Residents(WebServer webServer) {
        SerializerFactory serializerFactory = webServer.getSerializerFactory();
        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
        this.partialObjectMapper = serializerFactory.getPartialObjectMapper();
        this.residentPaginationUtil = webServer.getResidentPaginationUtil();
    }

    private Resident getResidentByUUID(String uuidParam) {
        if (uuidParam == null || uuidParam.isEmpty()) {
            throw new HttpResponseException(400, "Required path parameter 'uuid' is missing.");
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidParam);
        } catch (IllegalArgumentException e) {
            throw new HttpResponseException(400, "Invalid UUID format");
        }

        Resident resident = TownyAPI.getInstance().getResident(uuid);
        if (resident == null) {
            throw new HttpResponseException(404, "Resident not found");
        }

        return resident;
    }

    public void getResident(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Resident resident = getResidentByUUID(uuidParam);
        ctx.json(fullObjectMapper.valueToTree(resident));
    }

    public void getResidents(Context ctx) {
        String searchQuery = ctx.queryParam("search");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Resident> allResidents = TownyAPI.getInstance().getResidents();

        List<Resident> filteredResidents = allResidents;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredResidents = allResidents.stream()
                    .filter(resident -> resident.getName().toLowerCase().contains(lowerCaseSearch))
                    .collect(Collectors.toList());
        }

        Map<String, Object> paginatedResult = residentPaginationUtil.paginateList(filteredResidents, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }

    public void getResidentFriends(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Resident resident = getResidentByUUID(uuidParam);

        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        List<Resident> allFriends = resident.getFriends();

        Map<String, Object> paginatedResult = residentPaginationUtil.paginateList(allFriends, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }
}