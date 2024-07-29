package net.worldmc.townyweb.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import java.io.IOException;

public class TownTypeAdapter extends TypeAdapter<Town> {
    private final NationTypeAdapter.PartialNationAdapter partialNationAdapter = new NationTypeAdapter.PartialNationAdapter();
    private final ResidentTypeAdapter.PartialResidentAdapter partialResidentAdapter = new ResidentTypeAdapter.PartialResidentAdapter();

    @Override
    public void write(JsonWriter out, Town town) throws IOException {
        out.beginObject();
        out.name("name").value(town.getFormattedName());
        out.name("UUID").value(town.getUUID().toString());
        out.name("level").value(town.getLevelNumber());
        out.name("bankAccount").value(town.getAccount().getCachedBalance());
        out.name("board").value(town.getBoard());
        out.name("registered").value(town.getRegistered());
        out.name("founder").value(town.getFounder());
        out.name("townBlocks").value(town.getNumTownBlocks());
        out.name("isPublic").value(town.isPublic());
        out.name("isNeutral").value(town.isNeutral());
        out.name("isOpen").value(town.isOpen());

        out.name("settings").beginObject();
        out.name("pvp").value(town.isPVP());
        out.name("fire").value(town.isFire());
        out.name("mobs").value(town.hasMobs());
        out.name("explosions").value(town.isExplosion());
        out.name("taxpercent").value(town.isTaxPercentage());
        out.endObject();

        out.name("mayor");
        partialResidentAdapter.write(out, town.getMayor());

        Nation nation = town.getNationOrNull();
        if (nation != null) {
            out.name("nation");
            partialNationAdapter.write(out, nation);
        }

        out.name("residents").value(town.getNumResidents());
        out.name("trustedResidents").value(town.getTrustedResidents().size());

        out.name("spawn").beginObject();
        out.name("x").value(town.getSpawnOrNull() != null ? town.getSpawnOrNull().getX() : 0);
        out.name("y").value(town.getSpawnOrNull() != null ? town.getSpawnOrNull().getY() : 0);
        out.name("z").value(town.getSpawnOrNull() != null ? town.getSpawnOrNull().getZ() : 0);
        out.endObject();

        out.name("plotGroups").beginArray();
        town.getPlotGroups().forEach(group -> {
            try {
                out.value(group.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        out.endArray();

        out.endObject();
    }

    @Override
    public Town read(JsonReader in) throws IOException {
        return null;
    }

    public static class PartialTownAdapter extends TypeAdapter<Town> {
        @Override
        public void write(JsonWriter out, Town town) throws IOException {
            out.beginObject();
            out.name("name").value(town.getName());
            out.name("UUID").value(town.getUUID().toString());
            out.endObject();
        }

        @Override
        public Town read(JsonReader in) throws IOException {
            return null;
        }
    }
}