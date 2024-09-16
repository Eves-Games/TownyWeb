package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.TownyWeb;
import net.worldmc.townyweb.SerializerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Residents {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final SerializerFactory serializerFactory;

    public Residents(TownyWeb townyWeb) {
        this.serializerFactory = SerializerFactory.getInstance();
        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
        this.partialObjectMapper = serializerFactory.getPartialObjectMapper();
    }

    public void getResident(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");

        if (uuidParam.isEmpty()) {
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

        ctx.json(fullObjectMapper.valueToTree(resident));
    }

    public void getResidents(Context ctx) {
        String searchQuery = ctx.queryParam("search");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Resident> allResidents = TownyAPI.getInstance().getResidents();

        List<Resident> filteredResidents = allResidents;
        if (searchQuery != null) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredResidents = allResidents.stream()
                    .filter(resident -> resident.getName().toLowerCase().contains(lowerCaseSearch))
                    .toList();
        }

        Map<String, Object> paginatedResult = serializerFactory.paginateList(filteredResidents, page);
        paginatedResult.put("data", partialObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }
}