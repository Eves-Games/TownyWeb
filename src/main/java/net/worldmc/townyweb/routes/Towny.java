package net.worldmc.townyweb.routes;

import io.javalin.http.Context;
import net.worldmc.townyweb.sets.Nations;
import net.worldmc.townyweb.sets.Towns;
import net.worldmc.townyweb.sets.Residents;
import net.worldmc.townyweb.utils.PaginationUtil;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;

import java.util.*;
import java.util.stream.Collectors;

import static net.worldmc.townyweb.WebServer.MAX_PAGE_SIZE;

public class Towny {
    public static void getNations(Context ctx) {
        String searchQuery = ctx.queryParam("query");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = Math.min(ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(10), MAX_PAGE_SIZE);

        List<Nation> allNations = new ArrayList<>(TownyAPI.getInstance().getNations());
        List<Nation> filteredNations = allNations;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredNations = allNations.stream()
                    .filter(nation -> nation.getName().toLowerCase().contains(lowerCaseSearch))
                    .collect(Collectors.toList());
        }

        Map<String, Object> paginatedResult = PaginationUtil.paginateList(filteredNations, page, pageSize);

        List<?> dataList = (List<?>) paginatedResult.get("data");
        List<Map<String, Object>> nationMaps = dataList.stream()
                .filter(item -> item instanceof Nation)
                .map(item -> Nations.getPartialNation((Nation) item))
                .collect(Collectors.toList());

        paginatedResult.put("data", nationMaps);
        ctx.json(paginatedResult);
    }

    public static void getNation(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        try {
            UUID uuid = UUID.fromString(uuidParam);
            Nation nation = TownyAPI.getInstance().getNation(uuid);
            if (nation != null) {
                ctx.json(Nations.getNation(nation));
            } else {
                ctx.status(404).json(Map.of("error", "Nation not found"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format"));
        }
    }

    public static void getTowns(Context ctx) {
        String searchQuery = ctx.queryParam("query");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = Math.min(ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(10), MAX_PAGE_SIZE);

        List<Town> allTowns = new ArrayList<>(TownyAPI.getInstance().getTowns());
        List<Town> filteredTowns = allTowns;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredTowns = allTowns.stream()
                    .filter(town -> town.getName().toLowerCase().contains(lowerCaseSearch))
                    .collect(Collectors.toList());
        }

        Map<String, Object> paginatedResult = PaginationUtil.paginateList(filteredTowns, page, pageSize);

        List<?> dataList = (List<?>) paginatedResult.get("data");
        List<Map<String, Object>> townMaps = dataList.stream()
                .filter(item -> item instanceof Town)
                .map(item -> Towns.getPartialTown((Town) item))
                .collect(Collectors.toList());

        paginatedResult.put("data", townMaps);
        ctx.json(paginatedResult);
    }

    public static void getTown(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        try {
            UUID uuid = UUID.fromString(uuidParam);
            Town town = TownyAPI.getInstance().getTown(uuid);
            if (town != null) {
                ctx.json(Towns.getTown(town));
            } else {
                ctx.status(404).json(Map.of("error", "Town not found"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format"));
        }
    }

    public static void getResidents(Context ctx) {
        String searchQuery = ctx.queryParam("query");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = Math.min(ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(10), MAX_PAGE_SIZE);

        List<Resident> allResidents = new ArrayList<>(TownyAPI.getInstance().getResidents());
        List<Resident> filteredResidents = allResidents;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            String lowerCaseSearch = searchQuery.toLowerCase();
            filteredResidents = allResidents.stream()
                    .filter(resident -> resident.getName().toLowerCase().contains(lowerCaseSearch))
                    .collect(Collectors.toList());
        }

        Map<String, Object> paginatedResult = PaginationUtil.paginateList(filteredResidents, page, pageSize);

        List<?> dataList = (List<?>) paginatedResult.get("data");
        List<Map<String, Object>> residentMaps = dataList.stream()
                .filter(item -> item instanceof Resident)
                .map(item -> Residents.getPartialResident((Resident) item))
                .collect(Collectors.toList());

        paginatedResult.put("data", residentMaps);
        ctx.json(paginatedResult);
    }

    public static void getResident(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        try {
            UUID uuid = UUID.fromString(uuidParam);
            Resident resident = TownyAPI.getInstance().getResident(uuid);
            if (resident != null) {
                ctx.json(Residents.getResident(resident));
            } else {
                ctx.status(404).json(Map.of("error", "Resident not found"));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "Invalid UUID format"));
        }
    }
}