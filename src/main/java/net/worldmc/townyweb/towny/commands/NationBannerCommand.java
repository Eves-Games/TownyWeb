package net.worldmc.townyweb.towny.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.townyweb.SerializerFactory;
import net.worldmc.townyweb.TownyWeb;
import org.bukkit.Tag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NationBannerCommand implements CommandExecutor {
    private final ObjectMapper fullObjectMapper;

    private static final String PERMISSION = "towny.command.nation.set.banner";
    private static final String METADATA_KEY = "banner";

    private static final Component PERM_DENY_MSG = Component.text("You do not have permission to use this command!", NamedTextColor.RED);
    private static final Component PLAYER_ONLY_MSG = Component.text("This command can only be used by players.", NamedTextColor.RED);
    private static final Component NO_BANNER_MSG = Component.text("You must be holding a banner to use this command.", NamedTextColor.RED);

    public NationBannerCommand(TownyWeb townyWeb) {
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
        if (!Tag.BANNERS.isTagged(heldItem.getType())) {
            player.sendMessage(NO_BANNER_MSG);
            return true;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        assert resident != null;
        Nation nation = resident.getNationOrNull();
        assert nation != null;

        try {
            StringDataField bannerMetadata = new StringDataField(METADATA_KEY, fullObjectMapper.writeValueAsString(heldItem));
            nation.addMetaData(bannerMetadata);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        TownyMessaging.sendPrefixedNationMessage(nation, resident.getName() + " has changed the nation banner.");

        return true;
    }
}