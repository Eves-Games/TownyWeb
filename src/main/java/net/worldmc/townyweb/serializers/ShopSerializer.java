package net.worldmc.townyweb.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ghostchu.quickshop.api.shop.Shop;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.worldmc.townyweb.SerializerFactory;

import java.io.IOException;

public class ShopSerializer extends StdSerializer<Shop> {
    private final SerializerFactory serializerFactory;

    public ShopSerializer(SerializerFactory serializerFactory) {
        this(null, serializerFactory);
    }

    public ShopSerializer(Class<Shop> t, SerializerFactory serializerFactory) {
        super(t);
        this.serializerFactory = serializerFactory;
    }

    @Override
    public void serialize(Shop shop, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("name", shop.getShopName());
        gen.writeNumberField("id", shop.getShopId());

        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(shop.getLocation());
        if (townBlock != null) {
            Town town = townBlock.getTownOrNull();
            if (town != null) {
                serializerFactory.getPartialTownSerializer().serialize(town, gen, provider);
            }
        }

        gen.writeObjectFieldStart("owner");
        gen.writeStringField("name", shop.getOwner().getUsername());
        gen.writeStringField("UUID", String.valueOf(shop.getOwner().getUniqueId()));
        gen.writeEndObject();

        gen.writeStringField("item", shop.getItem().getType().getKey().getNamespace());
        gen.writeNumberField("amount", shop.getShopStackingAmount());
        gen.writeNumberField("price", shop.getPrice());

        gen.writeEndObject();
    }
}