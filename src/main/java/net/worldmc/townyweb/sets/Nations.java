package net.worldmc.townyweb.sets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nations {
    private static final Gson gson = new GsonBuilder().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();

    public static Map<String, Object> getPartialNation(Nation nation) {
        Map<String, Object> partial = new HashMap<>();

        if (nation != null) {
            partial.put("name", nation.getName());
            partial.put("UUID", nation.getUUID().toString());
            partial.put("numResidents", nation.getNumResidents());

            StringDataField bannerMetadata = (StringDataField) nation.getMetadata("banner");
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

    public static Map<String, Object> getNation(Nation nation) {
        Map<String, Object> full = new HashMap<>(getPartialNation(nation));

        if (nation != null) {
            full.put("level", nation.getLevelNumber());
            full.put("bankAccount", nation.getAccount().getCachedBalance());
            full.put("board", nation.getBoard());
            full.put("registered", nation.getRegistered());
            full.put("numTowns", nation.getNumTowns());
            full.put("numTownblocks", nation.getNumTownblocks());
            full.put("nationZoneSize", nation.getNationZoneSize());
            full.put("isPublic", nation.isPublic());
            full.put("isOpen", nation.isOpen());

            Map<String, Object> settings = new HashMap<>();
            settings.put("isTaxPercentage", nation.isTaxPercentage());
            settings.put("taxes", nation.getTaxes());
            settings.put("maxPercentTaxAmount", nation.getMaxPercentTaxAmount());
            settings.put("conqueredTax", nation.getConqueredTax());
            full.put("settings", settings);

            full.put("king", Residents.getPartialResident(nation.getKing()));
            full.put("capital", Towns.getPartialTown(nation.getCapital()));

            Map<String, Double> spawn = new HashMap<>();
            spawn.put("x", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getX() : 0);
            spawn.put("y", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getY() : 0);
            spawn.put("z", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getZ() : 0);
            full.put("spawn", spawn);

            List<Map<String, Object>> towns = new ArrayList<>();
            for (Town town : nation.getTowns()) {
                towns.add(Towns.getPartialTown(town));
            }
            full.put("towns", towns);

            List<Map<String, Object>> allies = new ArrayList<>();
            for (Nation ally : nation.getAllies()) {
                allies.add(getPartialNation(ally));
            }
            full.put("allies", allies);

            List<Map<String, Object>> enemies = new ArrayList<>();
            for (Nation enemy : nation.getEnemies()) {
                enemies.add(getPartialNation(enemy));
            }
            full.put("enemies", enemies);

            StringDataField discordMetadata = (StringDataField) nation.getMetadata("discordLink");
            if (discordMetadata != null) {
                full.put("discordLink", discordMetadata.getValue());
            }
        }

        return full;
    }
}