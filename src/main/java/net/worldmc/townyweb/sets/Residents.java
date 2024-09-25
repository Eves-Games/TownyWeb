package net.worldmc.townyweb.sets;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Residents {

    public static Map<String, Object> getPartialResident(Resident resident) {
        Map<String, Object> partial = new HashMap<>();

        if (resident != null) {
            partial.put("name", resident.getName());
            partial.put("UUID", resident.getUUID().toString());
            partial.put("isOnline", resident.isOnline());

            if (resident.getTownOrNull() != null) {
                partial.put("town", Towns.getPartialTown(resident.getTownOrNull()));
            }
        }

        return partial;
    }

    public static Map<String, Object> getResident(Resident resident) {
        Map<String, Object> full = new HashMap<>(getPartialResident(resident));

        if (resident != null) {
            full.put("surname", resident.getSurname());
            full.put("title", resident.getTitle());
            full.put("formattedName", resident.getFormattedName());
            full.put("formattedTitleName", resident.getFormattedTitleName());
            full.put("plotsCount", resident.getTownBlocks().size());
            full.put("bankAccount", resident.getAccount().getCachedBalance());
            full.put("about", resident.getAbout());
            full.put("registered", resident.getRegistered());
            full.put("lastOnline", resident.getLastOnline());
            full.put("isNPC", resident.isNPC());
            full.put("isMayor", resident.isMayor());
            full.put("isKing", resident.isKing());
            full.put("isAdmin", resident.isAdmin());
            full.put("joinedTownAt", resident.getJoinedTownAt());

            TownyPermission permissions = resident.getPermissions();
            Map<String, Boolean> permissionsMap = new HashMap<>();
            permissionsMap.put("pvp", permissions.pvp);
            permissionsMap.put("fire", permissions.fire);
            permissionsMap.put("explosion", permissions.explosion);
            permissionsMap.put("mobs", permissions.mobs);
            full.put("permissions", permissionsMap);

            full.put("modes", new ArrayList<>(resident.getModes()));
            full.put("townRanks", new ArrayList<>(resident.getTownRanks()));
            full.put("nationRanks", new ArrayList<>(resident.getNationRanks()));

            List<Map<String, Object>> friends = resident.getFriends().stream()
                    .map(Residents::getPartialResident)
                    .collect(Collectors.toList());
            full.put("friends", friends);

            if (resident.getNationOrNull() != null) {
                full.put("nation", Nations.getPartialNation(resident.getNationOrNull()));
            }

            Map<String, Object> jailStatus = new HashMap<>();
            jailStatus.put("isJailed", resident.isJailed());
            jailStatus.put("jailHours", resident.getJailHours());
            jailStatus.put("jailBailCost", resident.getJailBailCost());
            full.put("jailStatus", jailStatus);
        }

        return full;
    }
}