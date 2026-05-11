package com.fanxing.lib.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public final class TrackEntityParticleOption implements ParticleOptions {
    private final ParticleType<TrackEntityParticleOption> type;
    private final int entityId;

    private TrackEntityParticleOption(ParticleType<TrackEntityParticleOption> type, int entityId) {
        this.type = type;
        this.entityId = entityId;
    }

    public static TrackEntityParticleOption create(ParticleType<TrackEntityParticleOption> type, int entityId) {
        return new TrackEntityParticleOption(type, entityId);
    }

    // 用于命令/NBT（如果不需要，可以返回 null）
    public static MapCodec<TrackEntityParticleOption> codec(ParticleType<TrackEntityParticleOption> type) {
        // 使用 Codec.INT，而不是 ExtraCodecs.INT
        return Codec.INT.xmap(
                id -> new TrackEntityParticleOption(type, id),
                opt -> opt.entityId
        ).fieldOf("entityId");
    }

    // 网络流编解码器
    public static StreamCodec<RegistryFriendlyByteBuf, TrackEntityParticleOption> streamCodec(ParticleType<TrackEntityParticleOption> type) {
        return StreamCodec.of(
                (buf, opt) -> buf.writeInt(opt.entityId),
                buf -> new TrackEntityParticleOption(type, buf.readInt())
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