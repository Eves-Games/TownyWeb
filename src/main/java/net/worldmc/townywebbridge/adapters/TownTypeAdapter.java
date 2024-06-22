package net.worldmc.townywebbridge.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import java.io.IOException;

public class TownTypeAdapter extends TypeAdapter<Town> {
    @Override
    public void write(JsonWriter out, Town town) throws IOException {
        out.beginObject();
        out.name("name").value(town.getName());
        out.name("UUID").value(String.valueOf(town.getUUID()));
        out.name("level").value(town.getLevelNumber());
        out.name("board").value(town.getBoard());
        out.name("registered").value(town.getRegistered());
        out.name("founder").value(town.getFounder());
        out.name("townBlocks").value(town.getNumTownBlocks());
        out.name("isPublic").value(town.isPublic());
        out.name("isRuined").value(town.isRuined());
        out.name("settings").beginObject();
        out.name("hasPVP").value(town.isPVP());
        out.name("hasFire").value(town.isFire());
        out.name("hasMobs").value(town.hasMobs());
        out.name("hasExplosions").value(town.isExplosion());
        out.endObject();
        out.name("mayor").beginObject();
        out.name("name").value(town.getMayor().getName());
        out.name("UUID").value(String.valueOf(town.getMayor().getUUID()));
        out.name("title").value(town.getMayor().getTitle());
        out.endObject();
        try {
            Nation nation = town.getNation();
            out.name("nation").beginObject();
            out.name("name").value(nation.getName());
            out.name("UUID").value(String.valueOf(nation.getUUID()));
            out.endObject();
        } catch (NotRegisteredException ignored) {};
        out.endObject();
    }

    @Override
    public Town read(JsonReader in) throws IOException {
        return null;
    }
}
