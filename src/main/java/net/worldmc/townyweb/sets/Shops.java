package net.worldmc.townyweb.sets;

import com.ghostchu.quickshop.api.shop.Shop;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

import java.util.HashMap;
import java.util.Map;

public class Shops {
    public static Map<String, Object> getShop(Shop shop) {
        Map<String, Object> shopMap = new HashMap<>();

        if (shop != null) {
            shopMap.put("name", shop.getShopName());
            shopMap.put("id", shop.getShopId());

            TownBlock townBlock = TownyAPI.getInstance().getTownBlock(shop.getLocation());
            if (townBlock != null) {
                Town town = townBlock.getTownOrNull();
                if (town != null) {
                    shopMap.put("town", Towns.getPartialTown(town));
                }
            }

            Resident resident = TownyAPI.getInstance().getResident(shop.getOwner().getUniqueId());
            shopMap.put("owner", Residents.getPartialResident(resident));

            shopMap.put("item", shop.getItem().getType().name());
            shopMap.put("amount", shop.getShopStackingAmount());
            shopMap.put("price", shop.getPrice());
            shopMap.put("stock", shop.getRemainingStock());
        }

        return shopMap;
    }
}