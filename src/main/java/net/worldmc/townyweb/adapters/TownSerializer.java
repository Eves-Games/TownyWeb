package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palmergames.bukkit.towny.object.*;
import net.worldmc.townyweb.utils.SerializerFactory;

import java.io.IOException;

public class TownSerializer extends StdSerializer<Town> {
    private static final TownSerializer.Partial partial = new TownSerializer.Partial();

    public TownSerializer() {
        this(null);
    }

    public TownSerializer(Class<Town> t) {
        super(t);
    }

    public static class Partial extends StdSerializer<Town> {
        public Partial() {
            this(null);
        }

        protected Partial(Class<Town> t) {
            super(t);
        }

        @Override
        public void serialize(Town town, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", town.getName());
            gen.writeStringField("UUID", town.getUUID().toString());
            gen.writeEndObject();
        }
    }

    @Override
    public void serialize(Town town, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", town.getName());
        gen.writeStringField("UUID", town.getUUID().toString());

        gen.writeNumberField("level", town.getLevelNumber());
        gen.writeNumberField("bankAccount", town.getAccount().getCachedBalance());
        gen.writeStringField("board", town.getBoard());
        gen.writeNumberField("registered", town.getRegistered());
        gen.writeStringField("founder", town.getFounder());
        gen.writeNumberField("townBlocks", town.getNumTownBlocks());
        gen.writeBooleanField("isPublic", town.isPublic());
        gen.writeBooleanField("isNeutral", town.isNeutral());
        gen.writeBooleanField("isOpen", town.isOpen());

        gen.writeObjectFieldStart("settings");
        gen.writeBooleanField("pvp", town.isPVP());
        gen.writeBooleanField("fire", town.isFire());
        gen.writeBooleanField("mobs", town.hasMobs());
        gen.writeBooleanField("explosions", town.isExplosion());
        gen.writeBooleanField("taxpercent", town.isTaxPercentage());
        gen.writeEndObject();

        gen.writeFieldName("mayor");
        SerializerFactory.getInstance().getPartialResidentSerializer().serialize(town.getMayor(), gen, provider);

        Nation nation = town.getNationOrNull();
        if (nation != null) {
            gen.writeFieldName("nation");
            SerializerFactory.getInstance().getPartialNationSerializer().serialize(nation, gen, provider);
        }

        gen.writeNumberField("residents", town.getNumResidents());
        gen.writeNumberField("trustedResidents", town.getTrustedResidents().size());

        gen.writeObjectFieldStart("spawn");
        gen.writeNumberField("x", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getX() : 0);
        gen.writeNumberField("y", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getY() : 0);
        gen.writeNumberField("z", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getZ() : 0);
        gen.writeEndObject();

        gen.writeArrayFieldStart("plotGroups");
        for (PlotGroup group : town.getPlotGroups()) {
            gen.writeString(group.getName());
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }

    public TownSerializer.Partial getPartialSerializer() {
        return partial;
    }
}