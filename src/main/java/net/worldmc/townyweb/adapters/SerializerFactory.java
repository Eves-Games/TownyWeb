package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.palmergames.bukkit.towny.invites.Invite;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class SerializerFactory {
    private static SerializerFactory instance;

    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;

    private NationSerializer nationSerializer;
    private TownSerializer townSerializer;
    private ResidentSerializer residentSerializer;
    private InviteSerializer inviteSerializer;

    private PartialNationSerializer partialNationSerializer;
    private PartialTownSerializer partialTownSerializer;
    private PartialResidentSerializer partialResidentSerializer;

    public SerializerFactory() {
        this.fullObjectMapper = new ObjectMapper();
        this.partialObjectMapper = new ObjectMapper();

        SimpleModule fullModule = new SimpleModule();
        fullModule.addSerializer(Nation.class, getNationSerializer());
        fullModule.addSerializer(Town.class, getTownSerializer());
        fullModule.addSerializer(Resident.class, getResidentSerializer());
        fullModule.addSerializer(Invite.class, getInviteSerializer());
        fullObjectMapper.registerModule(fullModule);

        SimpleModule partialModule = new SimpleModule();
        partialModule.addSerializer(Nation.class, getPartialNationSerializer());
        partialModule.addSerializer(Town.class, getPartialTownSerializer());
        partialModule.addSerializer(Resident.class, getPartialResidentSerializer());
        partialModule.addSerializer(Invite.class, getInviteSerializer());
        partialObjectMapper.registerModule(partialModule);
    }

    public static SerializerFactory getInstance() {
        if (instance == null) {
            instance = new SerializerFactory();
        }
        return instance;
    }

    public ObjectMapper getFullObjectMapper() {
        return fullObjectMapper;
    }

    public ObjectMapper getPartialObjectMapper() {
        return partialObjectMapper;
    }

    public NationSerializer getNationSerializer() {
        if (nationSerializer == null) {
            nationSerializer = new NationSerializer();
        }
        return nationSerializer;
    }

    public TownSerializer getTownSerializer() {
        if (townSerializer == null) {
            townSerializer = new TownSerializer();
        }
        return townSerializer;
    }

    public ResidentSerializer getResidentSerializer() {
        if (residentSerializer == null) {
            residentSerializer = new ResidentSerializer();
        }
        return residentSerializer;
    }

    public InviteSerializer getInviteSerializer() {
        if (inviteSerializer == null) {
            inviteSerializer = new InviteSerializer();
        }
        return inviteSerializer;
    }

    public PartialNationSerializer getPartialNationSerializer() {
        if (partialNationSerializer == null) {
            partialNationSerializer = new PartialNationSerializer();
        }
        return partialNationSerializer;
    }

    public PartialTownSerializer getPartialTownSerializer() {
        if (partialTownSerializer == null) {
            partialTownSerializer = new PartialTownSerializer();
        }
        return partialTownSerializer;
    }

    public PartialResidentSerializer getPartialResidentSerializer() {
        if (partialResidentSerializer == null) {
            partialResidentSerializer = new PartialResidentSerializer();
        }
        return partialResidentSerializer;
    }
}