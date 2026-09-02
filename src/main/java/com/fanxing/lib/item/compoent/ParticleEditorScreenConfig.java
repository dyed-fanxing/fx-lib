package com.fanxing.lib.item.compoent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

/**
 * 粒子编辑器屏幕配置
 * @param previewWidthR 左侧面板粒子预览宽度比例
 * @param previewHeightR 左侧面板粒子预览高度比例
 * @param propertyHeightR 右侧面板属性高度比例
 */
public record ParticleEditorScreenConfig(float previewWidthR,float previewHeightR,float propertyHeightR) {
    public static final Codec<ParticleEditorScreenConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("previewWidthR").forGetter(ParticleEditorScreenConfig::previewWidthR),
            Codec.FLOAT.fieldOf("previewHeightR").forGetter(ParticleEditorScreenConfig::previewHeightR),
            Codec.FLOAT.fieldOf("propertyHeightR").forGetter(ParticleEditorScreenConfig::propertyHeightR)
    ).apply(instance, ParticleEditorScreenConfig::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleEditorScreenConfig> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ParticleEditorScreenConfig decode(@NotNull RegistryFriendlyByteBuf buf) {
            return new ParticleEditorScreenConfig(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, ParticleEditorScreenConfig palette) {
            buf.writeFloat(palette.previewWidthR);
            buf.writeFloat(palette.previewHeightR);
            buf.writeFloat(palette.propertyHeightR);
        }
    };
}
