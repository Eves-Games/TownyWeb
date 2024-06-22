package net.worldmc.townywebbridge.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import java.io.IOException;

public class ResidentTypeAdapter extends TypeAdapter<Resident> {
    @Override
    public void write(JsonWriter out, Resident resident) throws IOException {
        out.beginObject();
        out.name("name").value(resident.getName());
        out.name("UUID").value(String.valueOf(resident.getUUID()));
        out.name("title").value(resident.getTitle());
        out.name("plotsCount").value(resident.getTownBlocks().size());
        out.name("about").value(resident.getAbout());
        out.name("registered").value(resident.getRegistered());
        out.name("lastOnline").value(resident.getLastOnline());
        try {
            Town town = resident.getTown();
            out.name("town").beginObject();
            out.name("name").value(town.getName());
            out.name("UUID").value(String.valueOf(town.getUUID()));
            out.name("isMayor").value(resident.isMayor());
            out.endObject();
        } catch (NotRegisteredException ignored) {};
        out.endObject();
    }

    @Override
    public Resident read(JsonReader in) throws IOException {
        return null;
    }
}

