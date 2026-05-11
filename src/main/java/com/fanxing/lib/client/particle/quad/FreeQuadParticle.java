    package com.fanxing.lib.client.particle.quad;


    import com.fanxing.lib.client.particle.BaseParticle;
    import com.fanxing.lib.client.particle.mesh.QuadParticleRenderer;
    import com.mojang.blaze3d.vertex.VertexConsumer;
    import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
    import net.minecraft.client.multiplayer.ClientLevel;
    import net.minecraft.client.renderer.LightTexture;
    import net.minecraft.world.phys.AABB;
    import org.jetbrains.annotations.NotNull;
    import org.joml.Quaternionf;
    import org.joml.Vector3f;

    /**
     * @author dyed_fanxing
     * @date 2026/5/7 17:49
     * 自由平面纹理粒子（自由旋转、独立纹理，支持 UV 滚动）
     * 需要配合自定义 ParticleRenderType（纹理需支持 REPEAT 模式）。
     */
    public abstract class FreeQuadParticle extends BaseParticle {
        private FloatUnaryOperator width = t -> 1f;
        private FloatUnaryOperator length = t -> 1f;
        protected FloatUnaryOperator uMin = t -> 0f;
        protected FloatUnaryOperator vMin = t -> 0f;
        protected FloatUnaryOperator uMax = t -> 1f;
        protected FloatUnaryOperator vMax = t -> 1f;
        protected FloatUnaryOperator alphaFactory = t -> 1f;



        public FreeQuadParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            super(level, x, y, z, vx, vy, vz);
        }

        @Override
        public void render(@NotNull VertexConsumer consumer, Vector3f center, Quaternionf rotation, float partialTick) {
            float progress = getProgress(partialTick);
            float hw = width.apply(progress)*0.5f;
            float hh = length.apply(progress)*0.5f;
            // 局部坐标（未旋转）
            Vector3f br = new Vector3f(hw, -hh, 0);
            Vector3f tr = new Vector3f(hw, hh, 0);
            Vector3f tl = new Vector3f(-hw, hh, 0);
            Vector3f bl = new Vector3f(-hw, -hh, 0);
            // 应用旋转并平移到世界中心
            br.rotate(rotation).add(center);
            tr.rotate(rotation).add(center);
            tl.rotate(rotation).add(center);
            bl.rotate(rotation).add(center);

            setAlpha(alphaFactory.apply(progress));

            QuadParticleRenderer.render(consumer,
                    br.x(), br.y(), br.z(),
                    tr.x(), tr.y(), tr.z(),
                    tl.x(), tl.y(), tl.z(),
                    bl.x(), bl.y(), bl.z(),
                    (int) (rCol * 255), (int) (gCol * 255), (int) (bCol * 255), (int) (alpha*255), LightTexture.FULL_BRIGHT,
                    uMin.apply(progress),vMin.apply(progress), uMax.apply(progress),vMax.apply(progress));
        }

        @Override
        public @NotNull AABB getRenderBoundingBox(float partialTick) {
            Vector3f pos = getInterpolatedPos(partialTick);
            float progress = getProgress(partialTick);
            double cx = pos.x;
            double cy = pos.y;
            double cz = pos.z;
            float hw = width.apply(progress) * 0.5f;
            float hh = length.apply(progress) * 0.5f;
            return new AABB(cx - hw, cy - hh, cz - hw, cx + hw, cy + hh, cz + hw);
        }



        public void setSize(FloatUnaryOperator size) {
            this.width = size;this.length = size;
        }
        public void setSize(FloatUnaryOperator width, FloatUnaryOperator length) {
            this.width = width;this.length = length;
        }

        public void setUV(FloatUnaryOperator uMin, FloatUnaryOperator uMax, FloatUnaryOperator vMin, FloatUnaryOperator vMax) {
            this.uMin = uMin;this.uMax = uMax;this.vMin = vMin;this.vMax = vMax;
        }
        public void setUV(FloatUnaryOperator ...uv) {
            uMin = uv[0];vMin = uv[1];uMax = uv[2];vMax = uv[3];
        }

        public void setAlphaFactory(FloatUnaryOperator alphaFactory) {
            this.alphaFactory = alphaFactory;
        }
    }