package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.palmergames.bukkit.towny.object.Town;

import java.io.IOException;

public class PartialTownSerializer extends TownSerializer {
    public PartialTownSerializer() {
        super();
    }

    @Override
    public void serialize(Town town, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", town.getName());
        gen.writeStringField("UUID", town.getUUID().toString());
        gen.writeEndObject();
    }
}
