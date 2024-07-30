package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyObject;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.adapters.TownyEntityAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Residents {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final int PAGE_SIZE = 10;

    public Residents() {
        this.fullObjectMapper = new ObjectMapper();
        this.partialObjectMapper = new ObjectMapper();

        SimpleModule fullModule = new SimpleModule();
        fullModule.addSerializer(TownyObject.class, new TownyEntityAdapter());
        fullObjectMapper.registerModule(fullModule);

        SimpleModule partialModule = new SimpleModule();
        partialModule.addSerializer(TownyObject.class, new TownyEntityAdapter.Partial());
        partialObjectMapper.registerModule(partialModule);
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

        int totalResults = filteredResidents.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / PAGE_SIZE));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalResults);

        List<Resident> paginatedResidents = filteredResidents.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", partialObjectMapper.valueToTree(paginatedResidents));
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        ctx.json(response);
    }

    public void getResidentFriends(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Resident resident = getResidentByUUID(uuidParam);

        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        List<Resident> allFriends = resident.getFriends();

        int totalResults = allFriends.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / PAGE_SIZE));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalResults);

        List<Resident> paginatedFriends = allFriends.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", partialObjectMapper.valueToTree(paginatedFriends));
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        ctx.json(response);
    }
}