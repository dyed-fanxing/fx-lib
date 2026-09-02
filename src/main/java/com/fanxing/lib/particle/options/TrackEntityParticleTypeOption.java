package com.fanxing.lib.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class TrackEntityParticleTypeOption implements ParticleOptions {
    private final ParticleType<TrackEntityParticleTypeOption> type;
    private final int entityId;

    public TrackEntityParticleTypeOption(ParticleType<TrackEntityParticleTypeOption> type, int entityId) {
        this.type = type;
        this.entityId = entityId;
    }

    public static TrackEntityParticleTypeOption create(ParticleType<TrackEntityParticleTypeOption> type, int entityId) {
        return new TrackEntityParticleTypeOption(type, entityId);
    }



    // 用于命令/NBT（如果不需要，可以返回 null）
    public static MapCodec<TrackEntityParticleTypeOption> codec(ParticleType<TrackEntityParticleTypeOption> type) {
        // 使用 Codec.INT，而不是 ExtraCodecs.INT
        return Codec.INT.xmap(
                id -> new TrackEntityParticleTypeOption(type, id),
                opt -> opt.entityId
        ).fieldOf("entityId");
    }

    // 网络流编解码器
    public static StreamCodec<RegistryFriendlyByteBuf, TrackEntityParticleTypeOption> streamCodec(ParticleType<TrackEntityParticleTypeOption> type) {
        return StreamCodec.of(
                (buf, opt) -> buf.writeInt(opt.entityId),
                buf -> new TrackEntityParticleTypeOption(type, buf.readInt())
        );
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type;
    }

    public int getEntityId() {
        return entityId;
    }
}