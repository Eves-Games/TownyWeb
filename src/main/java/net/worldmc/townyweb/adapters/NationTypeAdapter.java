package net.worldmc.townyweb.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import java.io.IOException;

public class NationTypeAdapter extends TypeAdapter<Nation> {
    private final TownTypeAdapter.PartialTownAdapter partialTownAdapter = new TownTypeAdapter.PartialTownAdapter();
    private final NationTypeAdapter.PartialNationAdapter partialNationAdapter = new NationTypeAdapter.PartialNationAdapter();
    private final ResidentTypeAdapter.PartialResidentAdapter partialResidentAdapter = new ResidentTypeAdapter.PartialResidentAdapter();

    @Override
    public void write(JsonWriter out, Nation nation) throws IOException {
        out.beginObject();
        out.name("name").value(nation.getFormattedName());
        out.name("UUID").value(nation.getUUID().toString());
        out.name("level").value(nation.getLevelNumber());
        out.name("bankAccount").value(nation.getAccount().getCachedBalance());
        out.name("registered").value(nation.getRegistered());
        out.name("numResidents").value(nation.getNumResidents());
        out.name("numTowns").value(nation.getNumTowns());
        out.name("numTownblocks").value(nation.getNumTownblocks());
        out.name("nationZoneSize").value(nation.getNationZoneSize());
        out.name("isPublic").value(nation.isPublic());
        out.name("isOpen").value(nation.isOpen());

        out.name("settings").beginObject();
        out.name("isTaxPercentage").value(nation.isTaxPercentage());
        out.name("taxes").value(nation.getTaxes());
        out.name("maxPercentTaxAmount").value(nation.getMaxPercentTaxAmount());
        out.name("conqueredTax").value(nation.getConqueredTax());
        out.endObject();

        out.name("king");
        partialResidentAdapter.write(out, nation.getKing());

        out.name("capital");
        partialTownAdapter.write(out, nation.getCapital());

        out.name("spawn").beginObject();
        out.name("x").value(nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getX() : 0);
        out.name("y").value(nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getY() : 0);
        out.name("z").value(nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getZ() : 0);
        out.endObject();

        out.name("towns").beginArray();
        for (Town town : nation.getTowns()) {
            partialTownAdapter.write(out, town);
        }
        out.endArray();

        out.name("allies").beginArray();
        for (Nation ally : nation.getAllies()) {
            partialNationAdapter.write(out, ally);
        }
        out.endArray();

        out.name("enemies").beginArray();
        for (Nation enemy : nation.getEnemies()) {
            partialNationAdapter.write(out, enemy);
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public Nation read(JsonReader in) throws IOException {
        return null;
    }

    public static class PartialNationAdapter extends TypeAdapter<Nation> {
        @Override
        public void write(JsonWriter out, Nation nation) throws IOException {
            out.beginObject();
            out.name("name").value(nation.getName());
            out.name("UUID").value(nation.getUUID().toString());
            out.endObject();
        }

        @Override
        public Nation read(JsonReader in) throws IOException {
            return null;
        }
    }
}