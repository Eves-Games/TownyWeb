package net.worldmc.townyweb.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.IOException;

public class BannerSerializer extends StdSerializer<BannerMeta> {
    public BannerSerializer() {
        this(null);
    }

    public BannerSerializer(Class<BannerMeta> t) {
        super(t);
    }

    @Override
    public void serialize(BannerMeta bannerMeta, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (Pattern pattern : bannerMeta.getPatterns()) {
            gen.writeStartObject();
            gen.writeStringField("pattern", pattern.getPattern().toString());
            gen.writeStringField("color", pattern.getColor().toString());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}