package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.palmergames.bukkit.towny.object.Nation;

import java.io.IOException;

public class PartialNationSerializer extends NationSerializer {
    public PartialNationSerializer() {
        super();
    }

    @Override
    public void serialize(Nation nation, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", nation.getName());
        gen.writeStringField("UUID", nation.getUUID().toString());
        gen.writeEndObject();
    }
}
