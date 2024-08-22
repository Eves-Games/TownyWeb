package net.worldmc.townyweb.towny.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.townyweb.TownyWeb;
import net.worldmc.townyweb.SerializerFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

public class TownBannerCommand implements CommandExecutor {
    private final ObjectMapper fullObjectMapper;

    private static final String PERMISSION = "towny.command.town.set.banner";
    private static final String METADATA_KEY = "banner";

    private static final Component PERM_DENY_MSG = Component.text("You do not have permission to use this command!", NamedTextColor.RED);
    private static final Component PLAYER_ONLY_MSG = Component.text("This command can only be used by players.", NamedTextColor.RED);
    private static final Component NO_BANNER_MSG = Component.text("You must be holding a banner to use this command.", NamedTextColor.RED);

    public TownBannerCommand(TownyWeb townyWeb) {
        SerializerFactory serializerFactory = townyWeb.getSerializerFactory();
        this.fullObjectMapper = serializerFactory.getFullObjectMapper();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PLAYER_ONLY_MSG);
            return true;
        }

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(PERM_DENY_MSG);
            return true;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!isBanner(heldItem)) {
            player.sendMessage(NO_BANNER_MSG);
            return true;
        }
        BannerMeta bannerMeta = (BannerMeta) heldItem.getItemMeta();

        Resident resident = TownyAPI.getInstance().getResident(player);
        assert resident != null;
        Town town = resident.getTownOrNull();
        assert town != null;

        try {
            StringDataField bannerMetadata = new StringDataField(METADATA_KEY, getBannerNbtData(bannerMeta));
            town.addMetaData(bannerMetadata);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        TownyMessaging.sendPrefixedTownMessage(town, resident.getName() + " has changed the town banner.");

        return true;
    }

    private boolean isBanner(ItemStack item) {
        return item != null && item.getType().name().endsWith("_BANNER");
    }

    private String getBannerNbtData(BannerMeta bannerMeta) throws JsonProcessingException {
        return fullObjectMapper.writeValueAsString(bannerMeta);
    }
}