package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palmergames.bukkit.towny.object.*;

import java.io.IOException;

public class TownyEntityAdapter extends StdSerializer<TownyObject> {

    public enum SerializationMode {
        FULL, PARTIAL
    }

    private final SerializationMode mode;

    public TownyEntityAdapter() {
        this(null, SerializationMode.FULL);
    }

    public TownyEntityAdapter(Class<TownyObject> t, SerializationMode mode) {
        super(t);
        this.mode = mode;
    }

    @Override
    public void serialize(TownyObject obj, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (obj instanceof Resident) {
            serializeResident((Resident) obj, gen, provider);
        } else if (obj instanceof Town) {
            serializeTown((Town) obj, gen, provider);
        } else if (obj instanceof Nation) {
            serializeNation((Nation) obj, gen, provider);
        } else {
            throw new IOException("Unsupported object type: " + obj.getClass().getName());
        }
    }

    private void serializeResident(Resident resident, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", resident.getName());
        gen.writeStringField("UUID", resident.getUUID().toString());
        gen.writeStringField("title", resident.getTitle());

        if (mode == SerializationMode.FULL) {
            gen.writeStringField("surname", resident.getSurname());
            gen.writeStringField("formattedName", resident.getFormattedName());
            gen.writeStringField("formattedTitleName", resident.getFormattedTitleName());
            gen.writeNumberField("plotsCount", resident.getTownBlocks().size());
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
            for (String mode : resident.getModes()) {
                gen.writeString(mode);
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
                gen.writeStartObject();
                gen.writeStringField("name", friend.getName());
                gen.writeStringField("UUID", friend.getUUID().toString());
                gen.writeEndObject();
            }
            gen.writeEndArray();

            Town town = resident.getTownOrNull();
            if (town != null) {
                gen.writeFieldName("town");
                gen.writeStartObject();
                gen.writeStringField("name", town.getName());
                gen.writeStringField("UUID", town.getUUID().toString());
                gen.writeEndObject();
            }

            Nation nation = resident.getNationOrNull();
            if (nation != null) {
                gen.writeFieldName("nation");
                gen.writeStartObject();
                gen.writeStringField("name", nation.getName());
                gen.writeStringField("UUID", nation.getUUID().toString());
                gen.writeEndObject();
            }

            gen.writeObjectFieldStart("jailStatus");
            gen.writeBooleanField("isJailed", resident.isJailed());
            gen.writeNumberField("jailHours", resident.getJailHours());
            gen.writeNumberField("jailBailCost", resident.getJailBailCost());
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }

    private void serializeTown(Town town, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", town.getName());
        gen.writeStringField("UUID", town.getUUID().toString());

        if (mode == SerializationMode.FULL) {
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
            serializeResident(town.getMayor(), gen, provider);

            Nation nation = town.getNationOrNull();
            if (nation != null) {
                gen.writeFieldName("nation");
                gen.writeStartObject();
                gen.writeStringField("name", nation.getName());
                gen.writeStringField("UUID", nation.getUUID().toString());
                gen.writeEndObject();
            }

            gen.writeNumberField("residents", town.getNumResidents());
            gen.writeNumberField("trustedResidents", town.getTrustedResidents().size());

            gen.writeObjectFieldStart("spawn");
            gen.writeNumberField("x", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getX() : 0);
            gen.writeNumberField("y", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getY() : 0);
            gen.writeNumberField("z", town.getSpawnOrNull() != null ? town.getSpawnOrNull().getZ() : 0);
            gen.writeEndObject();

            gen.writeArrayFieldStart("plotGroups");
            town.getPlotGroups().forEach(group -> {
                try {
                    gen.writeString(group.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            gen.writeEndArray();
        }

        gen.writeEndObject();
    }

    private void serializeNation(Nation nation, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", nation.getName());
        gen.writeStringField("UUID", nation.getUUID().toString());

        if (mode == SerializationMode.FULL) {
            gen.writeNumberField("level", nation.getLevelNumber());
            gen.writeNumberField("bankAccount", nation.getAccount().getCachedBalance());
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
            serializeResident(nation.getKing(), gen, provider);

            gen.writeFieldName("capital");
            serializeTown(nation.getCapital(), gen, provider);

            gen.writeObjectFieldStart("spawn");
            gen.writeNumberField("x", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getX() : 0);
            gen.writeNumberField("y", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getY() : 0);
            gen.writeNumberField("z", nation.getSpawnOrNull() != null ? nation.getSpawnOrNull().getZ() : 0);
            gen.writeEndObject();

            gen.writeArrayFieldStart("allies");
            for (Nation ally : nation.getAllies()) {
                gen.writeStartObject();
                gen.writeStringField("name", ally.getName());
                gen.writeStringField("UUID", ally.getUUID().toString());
                gen.writeEndObject();
            }
            gen.writeEndArray();

            gen.writeArrayFieldStart("enemies");
            for (Nation enemy : nation.getEnemies()) {
                gen.writeStartObject();
                gen.writeStringField("name", enemy.getName());
                gen.writeStringField("UUID", enemy.getUUID().toString());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }

        gen.writeEndObject();
    }

    public static class Partial extends TownyEntityAdapter {
        public Partial() {
            super(null, SerializationMode.PARTIAL);
        }
    }
}