package com.fanxing.lib.integration.curio.register;

import com.fanxing.lib.FxLib;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author dyed_fanxing
 * @date 2026/4/28 18:27
 */
public class AttachmentTypesFxLibItg {
    public static DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FxLib.MOD_ID);

    public static final Supplier<AttachmentType<Map<String, Map<Integer, Integer>>>> USING_CURIOS =
            ATTACHMENT_TYPES.register("using_curios", () -> AttachmentType.<Map<String, Map<Integer, Integer>>>builder(() -> new HashMap<>())
                    .sync(
                            // 同步发送条件：总是发送到所有跟踪该玩家的客户端
                            (holder, serverPlayer) -> true,
                            // 序列化/反序列化 codec
                            StreamCodec.of(
                                    (buf, map) -> {
                                        // 写入外层 Map size
                                        buf.writeVarInt(map.size());
                                        for (Map.Entry<String, Map<Integer, Integer>> outer : map.entrySet()) {
                                            buf.writeUtf(outer.getKey());
                                            Map<Integer, Integer> inner = outer.getValue();
                                            buf.writeVarInt(inner.size());
                                            for (Map.Entry<Integer, Integer> entry : inner.entrySet()) {
                                                buf.writeVarInt(entry.getKey());
                                                buf.writeVarInt(entry.getValue());
                                            }
                                        }
                                    },
                                    buf -> {
                                        int outerSize = buf.readVarInt();
                                        Map<String, Map<Integer, Integer>> result = new HashMap<>();
                                        for (int i = 0; i < outerSize; i++) {
                                            String key = buf.readUtf();
                                            int innerSize = buf.readVarInt();
                                            Map<Integer, Integer> inner = new HashMap<>();
                                            for (int j = 0; j < innerSize; j++) {
                                                int idx = buf.readVarInt();
                                                int ticks = buf.readVarInt();
                                                inner.put(idx, ticks);
                                            }
                                            result.put(key, inner);
                                        }
                                        return result;
                                    }
                            )
                    )

                    .build()
            );


    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }


}
