package net.worldmc.townyweb.sets;

import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStacks {
    public static Map<String, Object> getItemStack(ItemStack item) {
        Map<String, Object> itemMap = new HashMap<>();

        if (item != null) {
            itemMap.put("type", item.getType().name());
            itemMap.put("amount", item.getAmount());

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof BannerMeta bannerMeta) {
                List<Map<String, String>> patterns = new ArrayList<>();
                for (Pattern pattern : bannerMeta.getPatterns()) {
                    Map<String, String> patternMap = new HashMap<>();
                    patternMap.put("color", pattern.getColor().name());
                    patternMap.put("pattern", pattern.getPattern().key().namespace());
                    patterns.add(patternMap);
                }
                itemMap.put("patterns", patterns);
            }

        }

        return itemMap;
    }
}