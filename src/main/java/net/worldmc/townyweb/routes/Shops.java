package net.worldmc.townyweb.routes;

import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import io.javalin.http.Context;
import net.worldmc.townyweb.utils.PaginationUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.worldmc.townyweb.WebServer.MAX_PAGE_SIZE;

public class Shops {
    public static void getShops(Context ctx) {
        String searchQuery = ctx.queryParam("query");
        String sortOrder = ctx.queryParam("sort");
        Integer minPrice = ctx.queryParamAsClass("minPrice", Integer.class).getOrDefault(null);
        Integer maxPrice = ctx.queryParamAsClass("maxPrice", Integer.class).getOrDefault(null);
        Integer minStock = ctx.queryParamAsClass("minStock", Integer.class).getOrDefault(null);
        Integer maxStock = ctx.queryParamAsClass("maxStock", Integer.class).getOrDefault(null);
        String shopType = ctx.queryParamAsClass("shopType", String.class).getOrDefault("SELLING");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int pageSize = Math.min(ctx.queryParamAsClass("pageSize", Integer.class).getOrDefault(10), MAX_PAGE_SIZE);

        QuickShopAPI quickShopAPI = QuickShopAPI.getInstance();
        List<Shop> allShops = new ArrayList<>(quickShopAPI.getShopManager().getAllShops());

        List<Shop> filteredShops = allShops.stream()
                .filter(shop -> {
                    if (searchQuery != null && !searchQuery.isEmpty()) {
                        Material material = shop.getItem().getType();
                        String itemName = material.name().toLowerCase().replace("_", " ");
                        return itemName.contains(searchQuery.toLowerCase());
                    }
                    return true;
                })
                .filter(shop -> {
                    if (!shop.isLoaded()) return false;
                    if (minPrice != null && shop.getPrice() < minPrice) return false;
                    if (maxPrice != null && shop.getPrice() > maxPrice) return false;
                    if (minStock != null && shop.getRemainingStock() < minStock) return false;
                    if (maxStock != null && shop.getRemainingStock() > maxStock) return false;
                    return shop.getShopType().name().equalsIgnoreCase(shopType);
                })
                .collect(Collectors.toList());

        Comparator<Shop> stockComparator = Comparator.comparingInt((Shop shop) ->
                shop.getRemainingStock() >= shop.getShopStackingAmount() ? 0 : 1);

        Comparator<Shop> priceComparator = Comparator.comparingDouble(shop ->
                shop.getPrice() / shop.getItem().getAmount());

        Comparator<Shop> valueComparator = stockComparator.thenComparing(priceComparator);

        if ("desc".equalsIgnoreCase(sortOrder)) {
            valueComparator = stockComparator.thenComparing(priceComparator.reversed());
        }

        filteredShops.sort(valueComparator);

        Map<String, Object> paginatedResult = PaginationUtil.paginateList(filteredShops, page, pageSize);

        List<?> dataList = (List<?>) paginatedResult.get("data");
        List<Map<String, Object>> shopMaps = dataList.stream()
                .filter(item -> item instanceof Shop)
                .map(item -> net.worldmc.townyweb.sets.Shops.getShop((Shop) item))
                .collect(Collectors.toList());

        paginatedResult.put("data", shopMaps);
        ctx.json(paginatedResult);
    }
}