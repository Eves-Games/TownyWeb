package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palmergames.bukkit.towny.object.*;

import java.io.IOException;

public class ResidentSerializer extends StdSerializer<Resident> {
    public ResidentSerializer() {
        this(null);
    }

    public ResidentSerializer(Class<Resident> t) {
        super(t);
    }

    @Override
    public void serialize(Resident resident, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", resident.getName());
        gen.writeStringField("UUID", resident.getUUID().toString());
        gen.writeStringField("title", resident.getTitle());

        gen.writeStringField("surname", resident.getSurname());
        gen.writeStringField("formattedName", resident.getFormattedName());
        gen.writeStringField("formattedTitleName", resident.getFormattedTitleName());
        gen.writeNumberField("plotsCount", resident.getTownBlocks().size());
        gen.writeNumberField("bankAccount", resident.getAccount().getCachedBalance());
        gen.writeStringField("about", resident.getAbout());
        gen.writeNumberField("registered", resident.getRegistered());
        gen.writeNumberField("lastOnline", resident.getLastOnline());
        gen.writeBooleanField("isNPC", resident.isNPC());
        gen.writeBooleanField("isOnline", resident.isOnline());
        gen.writeBooleanField("isMayor", resident.isMayor());
        gen.writeBooleanField("isKing", resident.isKing());
        gen.writeBooleanField("isAdmin", resident.isAdmin());
        gen.writeNumberField("joinedTownAt", resident.getJoinedTownAt());

        TownyPermission permissions = resident.getPermissions();
        gen.writeObjectFieldStart("permissions");
        gen.writeBooleanField("pvp", permissions.pvp);
        gen.writeBooleanField("fire", permissions.fire);
        gen.writeBooleanField("explosion", permissions.explosion);
        gen.writeBooleanField("mobs", permissions.mobs);
        gen.writeEndObject();

        gen.writeArrayFieldStart("modes");
        for (String resMode : resident.getModes()) {
            gen.writeString(resMode);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("townRanks");
        for (String rank : resident.getTownRanks()) {
            gen.writeString(rank);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("nationRanks");
        for (String rank : resident.getNationRanks()) {
            gen.writeString(rank);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("friends");
        for (Resident friend : resident.getFriends()) {
            new PartialResidentSerializer().serialize(friend, gen, provider);
        }
        gen.writeEndArray();

        Town town = resident.getTownOrNull();
        if (town != null) {
            gen.writeFieldName("town");
            SerializerFactory.getInstance().getPartialTownSerializer().serialize(town, gen, provider);
        }

        Nation nation = resident.getNationOrNull();
        if (nation != null) {
            gen.writeFieldName("nation");
            SerializerFactory.getInstance().getPartialNationSerializer().serialize(nation, gen, provider);
        }

        gen.writeObjectFieldStart("jailStatus");
        gen.writeBooleanField("isJailed", resident.isJailed());
        gen.writeNumberField("jailHours", resident.getJailHours());
        gen.writeNumberField("jailBailCost", resident.getJailBailCost());
        gen.writeEndObject();
    }
}