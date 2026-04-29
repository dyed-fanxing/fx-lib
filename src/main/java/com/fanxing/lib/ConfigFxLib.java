package com.fanxing.lib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(modid = FxLib.MOD_ID)
public class ConfigFxLib {




    //   通用配置
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }
    public static class Common {
        public Common(ModConfigSpec.Builder builder) {
        }
    }




    // 客户端配置

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;
    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = specPair.getLeft();
        CLIENT_SPEC = specPair.getRight();
    }
    public static class Client {
        public static ModConfigSpec.IntValue SEGMENTS;
        public Client(ModConfigSpec.Builder builder) {
            SEGMENTS = builder
                    .translation("config.fx_lib.general.segments")
                    .defineInRange("segments", 32, 4, 128);
        }
    }


    // 服务端配置

    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class Server {
        public Server(ModConfigSpec.Builder builder) {
        }
    }



    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}