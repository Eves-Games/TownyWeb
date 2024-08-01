package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.palmergames.bukkit.towny.object.Resident;

import java.io.IOException;

public class PartialResidentSerializer extends ResidentSerializer {
    public PartialResidentSerializer() {
        super();
    }

    @Override
    public void serialize(Resident resident, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", resident.getName());
        gen.writeStringField("UUID", resident.getUUID().toString());
        gen.writeStringField("title", resident.getTitle());
        gen.writeEndObject();
    }
}
