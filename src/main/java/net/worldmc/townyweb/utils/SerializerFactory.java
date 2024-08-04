package net.worldmc.townyweb.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.worldmc.townyweb.adapters.InviteSerializer;
import net.worldmc.townyweb.adapters.NationSerializer;
import net.worldmc.townyweb.adapters.ResidentSerializer;
import net.worldmc.townyweb.adapters.TownSerializer;

public class SerializerFactory {
    private static final SerializerFactory instance = new SerializerFactory();

    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;

    private final NationSerializer nationSerializer;
    private final TownSerializer townSerializer;
    private final ResidentSerializer residentSerializer;
    private final InviteSerializer inviteSerializer;

    public SerializerFactory() {
        this.nationSerializer = new NationSerializer();
        this.townSerializer = new TownSerializer();
        this.residentSerializer = new ResidentSerializer();
        this.inviteSerializer = new InviteSerializer();

        this.fullObjectMapper = new ObjectMapper();
        this.partialObjectMapper = new ObjectMapper();

        SimpleModule fullModule = new SimpleModule();
        fullModule.addSerializer(Nation.class, nationSerializer);
        fullModule.addSerializer(Town.class, townSerializer);
        fullModule.addSerializer(Resident.class, residentSerializer);
        fullModule.addSerializer(Invite.class, inviteSerializer);
        fullObjectMapper.registerModule(fullModule);

        SimpleModule partialModule = new SimpleModule();
        partialModule.addSerializer(Nation.class, nationSerializer.getPartialSerializer());
        partialModule.addSerializer(Town.class, townSerializer.getPartialSerializer());
        partialModule.addSerializer(Resident.class, residentSerializer.getPartialSerializer());
        partialModule.addSerializer(Invite.class, inviteSerializer);
        partialObjectMapper.registerModule(partialModule);
    }

    public static SerializerFactory getInstance() {
        return instance;
    }

    public ObjectMapper getFullObjectMapper() {
        return fullObjectMapper;
    }

    public ObjectMapper getPartialObjectMapper() {
        return partialObjectMapper;
    }

    public NationSerializer getNationSerializer() {
        return nationSerializer;
    }

    public TownSerializer getTownSerializer() {
        return townSerializer;
    }

    public ResidentSerializer getResidentSerializer() {
        return residentSerializer;
    }

    public InviteSerializer getInviteSerializer() {
        return inviteSerializer;
    }

    public NationSerializer.Partial getPartialNationSerializer() {
        return nationSerializer.getPartialSerializer();
    }

    public TownSerializer.Partial getPartialTownSerializer() {
        return townSerializer.getPartialSerializer();
    }

    public ResidentSerializer.Partial getPartialResidentSerializer() {
        return residentSerializer.getPartialSerializer();
    }
}