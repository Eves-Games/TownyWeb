package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.inviteobjects.NationAllyNationInvite;
import com.palmergames.bukkit.towny.object.inviteobjects.PlayerJoinTownInvite;
import com.palmergames.bukkit.towny.object.inviteobjects.TownJoinNationInvite;

import java.io.IOException;

public class InviteTypeAdapter extends StdSerializer<Invite> {
    private final TownyEntityAdapter partialEntityAdapter;

    public InviteTypeAdapter() {
        this(null);
    }

    public InviteTypeAdapter(Class<Invite> t) {
        super(t);
        this.partialEntityAdapter = new TownyEntityAdapter(null, TownyEntityAdapter.SerializationMode.PARTIAL);
    }

    @Override
    public void serialize(Invite invite, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("type", invite.getClass().getSimpleName());

        writeInvite(invite, gen, provider);

        gen.writeEndObject();
    }

    private void writeInvite(Invite invite, JsonGenerator gen, SerializerProvider provider) throws IOException {
        switch (invite) {
            case NationAllyNationInvite NANInvite ->
                    writeSenderReceiver(NANInvite.getSender(), NANInvite.getReceiver(), gen, provider);
            case PlayerJoinTownInvite PJIInvite ->
                    writeSenderReceiver(PJIInvite.getSender(), PJIInvite.getReceiver(), gen, provider);
            case TownJoinNationInvite TJNInvite ->
                    writeSenderReceiver(TJNInvite.getSender(), TJNInvite.getReceiver(), gen, provider);
            default -> throw new IOException("Unknown invite type: " + invite.getClass().getName());
        }
    }

    private void writeSenderReceiver(TownyObject sender, TownyObject receiver,
                                     JsonGenerator gen,
                                     SerializerProvider provider) throws IOException {
        gen.writeFieldName("sender");
        partialEntityAdapter.serialize(sender, gen, provider);

        gen.writeFieldName("receiver");
        partialEntityAdapter.serialize(receiver, gen, provider);
    }
}