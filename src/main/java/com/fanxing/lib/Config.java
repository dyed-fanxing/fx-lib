package com.fanxing.lib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(modid = FxLib.MOD_ID)
public class Config {
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {
        // ----- 通用设置 -----
        public final ModConfigSpec.IntValue segments;
        public Common(ModConfigSpec.Builder builder) {
            // ----- 通用设置 -----
            builder.push("general");
            builder.translation(String.format("config.%s.general", FxLib.MOD_ID));
            this.segments = builder
                    .translation("config.fx_corelib.general.segments")
                    .defineInRange("segments", 32, 4, 128);
            builder.pop();
        }

        public ModConfigSpec.IntValue getSegments() {
            return segments;
        }
    }
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // 可在此添加配置加载后的处理（目前留空）
    }

    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public Server(ModConfigSpec.Builder builder) {
            // ----- 通用设置 -----
            builder.push("general");
            builder.translation(String.format("config.%s.general", FxLib.MOD_ID));
            builder.pop();
        }
    }
    public record EntityExpEntry(int exp, int limit) {}
}