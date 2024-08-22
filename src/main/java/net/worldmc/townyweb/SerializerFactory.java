package net.worldmc.townyweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.worldmc.townyweb.adapters.BannerSerializer;
import net.worldmc.townyweb.adapters.NationSerializer;
import net.worldmc.townyweb.adapters.ResidentSerializer;
import net.worldmc.townyweb.adapters.TownSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializerFactory {
    private static final SerializerFactory instance = new SerializerFactory();
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;

    private final NationSerializer nationSerializer;
    private final TownSerializer townSerializer;
    private final ResidentSerializer residentSerializer;

    public SerializerFactory() {
        this.nationSerializer = new NationSerializer(this);
        this.townSerializer = new TownSerializer(this);
        this.residentSerializer = new ResidentSerializer(this);
        BannerSerializer bannerSerializer = new BannerSerializer();

        this.fullObjectMapper = new ObjectMapper();
        this.partialObjectMapper = new ObjectMapper();

        SimpleModule fullModule = new SimpleModule();
        fullModule.addSerializer(Nation.class, nationSerializer);
        fullModule.addSerializer(Town.class, townSerializer);
        fullModule.addSerializer(Resident.class, residentSerializer);
        fullModule.addSerializer(ItemStack.class, bannerSerializer);
        fullObjectMapper.registerModule(fullModule);

        SimpleModule partialModule = new SimpleModule();
        partialModule.addSerializer(Nation.class, nationSerializer.getPartialSerializer());
        partialModule.addSerializer(Town.class, townSerializer.getPartialSerializer());
        partialModule.addSerializer(Resident.class, residentSerializer.getPartialSerializer());
        partialObjectMapper.registerModule(partialModule);
    }

    public <T> Map<String, Object> paginateList(List<T> items, int page, int pageSize) {
        int totalResults = items.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / pageSize));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalResults);

        List<T> paginatedItems = items.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", paginatedItems);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return response;
    }

    public <T> Map<String, Object> paginateList(List<T> items, int page) {
        return paginateList(items, page, DEFAULT_PAGE_SIZE);
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