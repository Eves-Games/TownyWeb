package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.inviteobjects.NationAllyNationInvite;
import com.palmergames.bukkit.towny.object.inviteobjects.PlayerJoinTownInvite;
import com.palmergames.bukkit.towny.object.inviteobjects.TownJoinNationInvite;

import java.io.IOException;

public class InviteSerializer extends StdSerializer<Invite> {
    public InviteSerializer() {
        this(null);
    }

    public InviteSerializer(Class<Invite> t) {
        super(t);
    }

    @Override
    public void serialize(Invite invite, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("type", invite.getClass().getSimpleName());

        switch (invite) {
            case NationAllyNationInvite NANInvite ->
                    writeSenderReceiver(NANInvite.getSender(), NANInvite.getReceiver(), gen, provider);
            case PlayerJoinTownInvite PJTInvite ->
                    writeSenderReceiver(PJTInvite.getSender(), PJTInvite.getReceiver(), gen, provider);
            case TownJoinNationInvite TJNInvite ->
                    writeSenderReceiver(TJNInvite.getSender(), TJNInvite.getReceiver(), gen, provider);
            default -> throw new IOException("Unknown invite type: " + invite.getClass().getName());
        }

        gen.writeEndObject();
    }

    private void writeSenderReceiver(TownyObject sender, TownyObject receiver,
                                     JsonGenerator gen,
                                     SerializerProvider provider) throws IOException {
        gen.writeFieldName("sender");
        serializeTownyObject(sender, gen, provider);

        gen.writeFieldName("receiver");
        serializeTownyObject(receiver, gen, provider);
    }

    private void serializeTownyObject(TownyObject object, JsonGenerator gen, SerializerProvider provider) throws IOException {
        switch (object) {
            case Resident resident ->
                    SerializerFactory.getInstance().getPartialResidentSerializer().serialize(resident, gen, provider);
            case Town town -> SerializerFactory.getInstance().getPartialTownSerializer().serialize(town, gen, provider);
            case Nation nation ->
                    SerializerFactory.getInstance().getPartialNationSerializer().serialize(nation, gen, provider);
            default -> {}
        }
    }
}