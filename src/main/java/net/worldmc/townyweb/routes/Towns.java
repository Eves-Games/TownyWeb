package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.adapters.SerializerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Towns {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final int PAGE_SIZE = 10;

    public Towns() {
        SerializerFactory serializerFactory = new SerializerFactory();

        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
        this.partialObjectMapper = serializerFactory.getPartialObjectMapper();
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

        int totalResults = filteredTowns.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / PAGE_SIZE));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalResults);

        List<Town> paginatedTowns = filteredTowns.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", partialObjectMapper.valueToTree(paginatedTowns));
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        ctx.json(response);
    }

    public void getTownResidents(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Town town = getTownByUUID(uuidParam);

        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        List<Resident> allResidents = town.getResidents();

        int totalResults = allResidents.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / PAGE_SIZE));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalResults);

        List<Resident> paginatedResidents = allResidents.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", partialObjectMapper.valueToTree(paginatedResidents));
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        ctx.json(response);
    }
}