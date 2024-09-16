package net.worldmc.townyweb.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townyweb.SerializerFactory;
import net.worldmc.townyweb.TownyWeb;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Shops {
    private final ObjectMapper fullObjectMapper;
    private final ObjectMapper partialObjectMapper;
    private final SerializerFactory serializerFactory;

    public Shops(TownyWeb townyWeb) {
        this.serializerFactory = SerializerFactory.getInstance();
        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
        this.partialObjectMapper = serializerFactory.getPartialObjectMapper();
    }

    public void getShops(Context ctx) {
        String itemQuery = ctx.queryParam("item");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);

        if (itemQuery == null) {
            throw new HttpResponseException(400, "Required query parameter 'item' is missing.");
        }

        itemQuery = itemQuery.toUpperCase().trim();

        Material material = Material.getMaterial(itemQuery);
        if (material == null) {
            throw new HttpResponseException(404, "Query parameter 'item' is invalid. Material not found: " + itemQuery);
        }

        List<Shop> shops = QuickShopAPI.getInstance().getShopManager().getAllShops().stream()
                .filter(shop -> shop.getItem().getType() == material)
                .sorted(Comparator.comparingDouble(Shop::getPrice))
                .toList();

        Map<String, Object> paginatedResult = serializerFactory.paginateList(shops, page);
        paginatedResult.put("data", fullObjectMapper.valueToTree(paginatedResult.get("data")));

        ctx.json(paginatedResult);
    }
}
