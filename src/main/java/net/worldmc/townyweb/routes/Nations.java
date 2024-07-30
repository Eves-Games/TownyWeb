package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyObject;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.adapters.TownyEntityAdapter;
import net.worldmc.townyweb.adapters.InviteTypeAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Nations {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final int PAGE_SIZE = 10;

    public Nations() {
        this.fullObjectMapper = new ObjectMapper();
        this.partialObjectMapper = new ObjectMapper();

        SimpleModule fullModule = new SimpleModule();
        fullModule.addSerializer(TownyObject.class, new TownyEntityAdapter());
        fullModule.addSerializer(Invite.class, new InviteTypeAdapter());
        fullObjectMapper.registerModule(fullModule);

        SimpleModule partialModule = new SimpleModule();
        partialModule.addSerializer(TownyObject.class, new TownyEntityAdapter.Partial());
        partialModule.addSerializer(Invite.class, new InviteTypeAdapter());
        partialObjectMapper.registerModule(partialModule);
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

        int totalResults = filteredNations.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / PAGE_SIZE));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalResults);

        List<Nation> paginatedNations = filteredNations.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", partialObjectMapper.valueToTree(paginatedNations));
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        ctx.json(response);
    }

    public void getNationTowns(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Nation nation = getNationByUUID(uuidParam);
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        List<Town> allTowns = nation.getTowns();
        int totalResults = allTowns.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / PAGE_SIZE));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalResults);

        List<Town> paginatedTowns = allTowns.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", partialObjectMapper.valueToTree(paginatedTowns));
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        ctx.json(response);
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