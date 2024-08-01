package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palmergames.bukkit.towny.object.*;

import java.io.IOException;

public class NationSerializer extends StdSerializer<Nation> {
    public NationSerializer() {
        this(null);
    }

    public NationSerializer(Class<Nation> t) {
        super(t);
    }

    @Override
    public void serialize(Nation nation, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", nation.getName());
        gen.writeStringField("UUID", nation.getUUID().toString());

        gen.writeNumberField("level", nation.getLevelNumber());
        gen.writeNumberField("bankAccount", nation.getAccount().getCachedBalance());
        gen.writeStringField("board", nation.getBoard());
        gen.writeNumberField("registered", nation.getRegistered());
        gen.writeNumberField("numResidents", nation.getNumResidents());
        gen.writeNumberField("numTowns", nation.getNumTowns());
        gen.writeNumberField("numTownblocks", nation.getNumTownblocks());
        gen.writeNumberField("nationZoneSize", nation.getNationZoneSize());
        gen.writeBooleanField("isPublic", nation.isPublic());
        gen.writeBooleanField("isOpen", nation.isOpen());

        gen.writeObjectFieldStart("settings");
        gen.writeBooleanField("isTaxPercentage", nation.isTaxPercentage());
        gen.writeNumberField("taxes", nation.getTaxes());
        gen.writeNumberField("maxPercentTaxAmount", nation.getMaxPercentTaxAmount());
        gen.writeNumberField("conqueredTax", nation.getConqueredTax());
        gen.writeEndObject();

        gen.writeFieldName("king");
        SerializerFactory.getInstance().getPartialResidentSerializer().serialize(nation.getKing(), gen, provider);

        gen.writeFieldName("capital");
        SerializerFactory.getInstance().getPartialTownSerializer().serialize(nation.getCapital(), gen, provider);

        gen.writeObjectFieldStart("spawn");
        gen.writeNumberField("x", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getX() : 0);
        gen.writeNumberField("y", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getY() : 0);
        gen.writeNumberField("z", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getZ() : 0);
        gen.writeEndObject();

        gen.writeArrayFieldStart("allies");
        for (Nation ally : nation.getAllies()) {
            SerializerFactory.getInstance().getPartialNationSerializer().serialize(ally, gen, provider);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("enemies");
        for (Nation enemy : nation.getEnemies()) {
            SerializerFactory.getInstance().getPartialNationSerializer().serialize(enemy, gen, provider);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}

