package net.worldmc.townyweb.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyPermission;

import java.io.IOException;

public class ResidentTypeAdapter extends TypeAdapter<Resident> {
    private final TownTypeAdapter.PartialTownAdapter partialTownAdapter = new TownTypeAdapter.PartialTownAdapter();
    private final NationTypeAdapter.PartialNationAdapter partialNationAdapter = new NationTypeAdapter.PartialNationAdapter();
    private final ResidentTypeAdapter.PartialResidentAdapter partialResidentAdapter = new ResidentTypeAdapter.PartialResidentAdapter();

    @Override
    public void write(JsonWriter out, Resident resident) throws IOException {
        out.beginObject();
        out.name("name").value(resident.getName());
        out.name("UUID").value(String.valueOf(resident.getUUID()));
        out.name("title").value(resident.getTitle());
        out.name("surname").value(resident.getSurname());
        out.name("formattedName").value(resident.getFormattedName());
        out.name("formattedTitleName").value(resident.getFormattedTitleName());
        out.name("plotsCount").value(resident.getTownBlocks().size());
        out.name("about").value(resident.getAbout());
        out.name("registered").value(resident.getRegistered());
        out.name("lastOnline").value(resident.getLastOnline());
        out.name("isNPC").value(resident.isNPC());
        out.name("isOnline").value(resident.isOnline());
        out.name("isMayor").value(resident.isMayor());
        out.name("isKing").value(resident.isKing());
        out.name("isAdmin").value(resident.isAdmin());
        out.name("joinedTownAt").value(resident.getJoinedTownAt());

        TownyPermission permissions = resident.getPermissions();
        out.name("permissions").beginObject();
        out.name("pvp").value(permissions.pvp);
        out.name("fire").value(permissions.fire);
        out.name("explosion").value(permissions.explosion);
        out.name("mobs").value(permissions.mobs);
        out.endObject();

        out.name("modes").beginArray();
        for (String mode : resident.getModes()) {
            out.value(mode);
        }
        out.endArray();

        out.name("townRanks").beginArray();
        for (String rank : resident.getTownRanks()) {
            out.value(rank);
        }
        out.endArray();

        out.name("nationRanks").beginArray();
        for (String rank : resident.getNationRanks()) {
            out.value(rank);
        }
        out.endArray();

        out.name("friends").beginArray();
        for (Resident friend : resident.getFriends()) {
            partialResidentAdapter.write(out, friend);
        }
        out.endArray();

        Town town = resident.getTownOrNull();
        if (town != null) {
            out.name("town");
            partialTownAdapter.write(out, town);
        }

        Nation nation = resident.getNationOrNull();
        if (nation != null) {
            out.name("nation");
            partialNationAdapter.write(out, nation);
        }

        out.name("jailStatus").beginObject();
        out.name("isJailed").value(resident.isJailed());
        out.name("jailHours").value(resident.getJailHours());
        out.name("jailBailCost").value(resident.getJailBailCost());
        out.endObject();

        out.endObject();
    }

    @Override
    public Resident read(JsonReader in) throws IOException {
        return null;
    }

    public static class PartialResidentAdapter extends TypeAdapter<Resident> {
        @Override
        public void write(JsonWriter out, Resident resident) throws IOException {
            out.beginObject();
            out.name("name").value(resident.getName());
            out.name("UUID").value(resident.getUUID().toString());
            out.name("title").value(resident.getTitle());
            out.endObject();
        }

        @Override
        public Resident read(JsonReader in) throws IOException {
            return null;
        }
    }
}