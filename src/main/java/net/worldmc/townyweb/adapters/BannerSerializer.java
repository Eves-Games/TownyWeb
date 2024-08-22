package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.IOException;

public class BannerSerializer extends StdSerializer<ItemStack> {
    public BannerSerializer() {
        this(null);
    }

    public BannerSerializer(Class<ItemStack> t) {
        super(t);
    }

    @Override
    public void serialize(ItemStack itemStack, JsonGenerator gen, SerializerProvider provider) throws IOException {
        BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();

        gen.writeStartObject();

        gen.writeStringField("type", itemStack.getType().name());

        gen.writeArrayFieldStart("patterns");
        for (Pattern pattern : bannerMeta.getPatterns()) {
            gen.writeStartObject();
            gen.writeStringField("pattern", pattern.getPattern().name());
            gen.writeStringField("color", pattern.getColor().name());
            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}