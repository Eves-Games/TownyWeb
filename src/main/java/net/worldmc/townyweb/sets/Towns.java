package net.worldmc.townyweb.sets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Towns {
    private static final Gson gson = new GsonBuilder().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();

    public static Map<String, Object> getPartialTown(Town town) {
        Map<String, Object> partial = new HashMap<>();

        if (town != null) {
            partial.put("name", town.getName());
            partial.put("UUID", town.getUUID().toString());
            partial.put("numResidents", town.getNumResidents());

            StringDataField bannerMetadata = (StringDataField) town.getMetadata("banner");
            if (bannerMetadata != null) {
                try {
                    Map<String, Object> bannerMap = gson.fromJson(bannerMetadata.getValue(), MAP_TYPE);
                    partial.put("bannerMeta", bannerMap);
                } catch (Exception e) {
                    partial.put("bannerMeta", bannerMetadata.getValue());
                }
            }
        }

        return partial;
    }

    public static Map<String, Object> getTown(Town town) {
        Map<String, Object> full = new HashMap<>(getPartialTown(town));

        if (town != null) {
            full.put("level", town.getLevelNumber());
            full.put("bankAccount", town.getAccount().getCachedBalance());
            full.put("board", town.getBoard());
            full.put("registered", town.getRegistered());
            full.put("founder", town.getFounder());
            full.put("isPublic", town.isPublic());
            full.put("isNeutral", town.isNeutral());
            full.put("isOpen", town.isOpen());

            Map<String, Boolean> settings = new HashMap<>();
            settings.put("pvp", town.isPVP());
            settings.put("fire", town.isFire());
            settings.put("mobs", town.hasMobs());
            settings.put("explosions", town.isExplosion());
            settings.put("taxpercent", town.isTaxPercentage());
            full.put("settings", settings);

            full.put("mayor", Residents.getPartialResident(town.getMayor()));

            if (town.getNationOrNull() != null) {
                full.put("nation", Nations.getPartialNation(town.getNationOrNull()));
            }

            full.put("numResidents", town.getNumResidents());
            full.put("trustedResidents", town.getTrustedResidents().size());

            Map<String, Double> spawn = new HashMap<>();
            spawn.put("x", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getX() : 0);
            spawn.put("y", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getY() : 0);
            spawn.put("z", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getZ() : 0);
            full.put("spawn", spawn);

            List<Map<String, Object>> townBlocks = new ArrayList<>();
            for (TownBlock townBlock : town.getTownBlocks()) {
                Map<String, Object> block = new HashMap<>();
                block.put("name", townBlock.getName());
                block.put("type", townBlock.getTypeName());
                block.put("isHomeBlock", townBlock.isHomeBlock());
                block.put("plotTax", townBlock.getPlotTax());

                if (townBlock.getResidentOrNull() != null) {
                    block.put("resident", Residents.getPartialResident(townBlock.getResidentOrNull()));
                }

                Map<String, Integer> coordinates = new HashMap<>();
                coordinates.put("x", townBlock.getX());
                coordinates.put("z", townBlock.getZ());
                block.put("coordinates", coordinates);

                townBlocks.add(block);
            }
            full.put("townBlocks", townBlocks);

            List<Map<String, Object>> residents = new ArrayList<>();
            for (Resident resident : town.getResidents()) {
                residents.add(Residents.getPartialResident(resident));
            }
            full.put("residents", residents);

            StringDataField discordMetadata = (StringDataField) town.getMetadata("discordLink");
            if (discordMetadata != null) {
                full.put("discordLink", discordMetadata.getValue());
            }
        }

        return full;
    }
}