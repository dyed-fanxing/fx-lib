package com.fanxing.lib.client.render.particle;

import com.fanxing.lib.client.particle.rendertypes.ParticleRenderTypesFxLib;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * 手动渲染粒子的全局缓冲、排序与提交管理器（单一渲染类型）。
 * <p>
 * 排序严格遵循两阶段规则，提交采用独立 Tesselator。
 * 粒子注册时直接加入主列表，无延迟批处理。
 * </p>
 *
 * @author dyed_fanxing
 * @since 2026/6/23 15:30
 */
public class RenderParticleRenderer {

    private static final List<RenderParticle> PARTICLES = new ArrayList<>(16384);
    private static final Minecraft MC = Minecraft.getInstance();

    public static void register(RenderParticle particle) {
        PARTICLES.add(particle);
    }

    public static void registerAll(Collection<RenderParticle> particles) {
        PARTICLES.addAll(particles);
    }

    public static void unregister(RenderParticle particle) {
        PARTICLES.remove(particle);
    }

    public static void unregisterAll(Collection<RenderParticle> particles) {
        PARTICLES.removeAll(particles);
    }

    public static void render() {
        if (PARTICLES.isEmpty()) return;

        LightTexture lightTexture = MC.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = ParticleRenderTypesFxLib.PARTICLE_TRANSLUCENT.begin(tesselator, MC.getTextureManager());

        if (builder != null) {
            Camera camera = MC.gameRenderer.getMainCamera();
            Vec3 camPos = camera.getPosition();
            float camX = (float) camPos.x, camY = (float) camPos.y, camZ = (float) camPos.z;

            // 第一阶段：稳定距离排序（远的先画）
            PARTICLES.sort((a, b) -> {
                double da = camPos.distanceToSqr(a.x, a.y, a.z);
                double db = camPos.distanceToSqr(b.x, b.y, b.z);
                return Double.compare(db, da);
            });

            // 第二阶段：同 layerID 内按 orderInLayer 升序重排
            Map<Integer, List<RenderParticle>> layerMap = new HashMap<>();
            for (RenderParticle p : PARTICLES) {
                layerMap.computeIfAbsent(p.layerID, k -> new ArrayList<>()).add(p);
            }
            for (List<RenderParticle> group : layerMap.values()) {
                if (group.size() > 1) {
                    group.sort(Comparator.comparingInt(p -> p.orderInLayer));
                }
            }
            Map<Integer, Iterator<RenderParticle>> iterMap = new HashMap<>();
            for (Map.Entry<Integer, List<RenderParticle>> e : layerMap.entrySet()) {
                iterMap.put(e.getKey(), e.getValue().iterator());
            }
            PARTICLES.replaceAll(rp -> iterMap.get(rp.layerID).next());

            // 提交顶点
            float upRefX = 0, upRefY = 1, upRefZ = 0;
            for (RenderParticle p : PARTICLES) {
                float wx = p.x, wy = p.y, wz = p.z;
                float dx = camX - wx, dy = camY - wy, dz = camZ - wz;
                float len = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
                dx /= len; dy /= len; dz /= len;

                float dot = dx * upRefX + dy * upRefY + dz * upRefZ;
                float ux, uy, uz;
                if (Math.abs(dot) > 0.9999f) {
                    ux = 0; uy = 0; uz = 1;
                } else {
                    ux = upRefX; uy = upRefY; uz = upRefZ;
                }
                float rx = uy * dz - uz * dy;
                float ry = uz * dx - ux * dz;
                float rz = ux * dy - uy * dx;
                float rlen = (float) Math.sqrt(rx*rx + ry*ry + rz*rz);
                rx /= rlen; ry /= rlen; rz /= rlen;

                float ux2 = dy * rz - dz * ry;
                float uy2 = dz * rx - dx * rz;
                float uz2 = dx * ry - dy * rx;

                float s = p.size;
                float v0x = wx + rx*s - ux2*s;
                float v0y = wy + ry*s - uy2*s;
                float v0z = wz + rz*s - uz2*s;
                float v1x = wx + rx*s + ux2*s;
                float v1y = wy + ry*s + uy2*s;
                float v1z = wz + rz*s + uz2*s;
                float v2x = wx - rx*s + ux2*s;
                float v2y = wy - ry*s + uy2*s;
                float v2z = wz - rz*s + uz2*s;
                float v3x = wx - rx*s - ux2*s;
                float v3y = wy - ry*s - uy2*s;
                float v3z = wz - rz*s - uz2*s;

                float c0x = v0x - camX, c0y = v0y - camY, c0z = v0z - camZ;
                float c1x = v1x - camX, c1y = v1y - camY, c1z = v1z - camZ;
                float c2x = v2x - camX, c2y = v2y - camY, c2z = v2z - camZ;
                float c3x = v3x - camX, c3y = v3y - camY, c3z = v3z - camZ;

                builder.addVertex(c0x, c0y, c0z).setColor(p.color).setUv(p.sprite.getU1(), p.sprite.getV1()).setLight(LightTexture.FULL_BRIGHT);
                builder.addVertex(c1x, c1y, c1z).setColor(p.color).setUv(p.sprite.getU1(), p.sprite.getV0()).setLight(LightTexture.FULL_BRIGHT);
                builder.addVertex(c2x, c2y, c2z).setColor(p.color).setUv(p.sprite.getU0(), p.sprite.getV0()).setLight(LightTexture.FULL_BRIGHT);
                builder.addVertex(c3x, c3y, c3z).setColor(p.color).setUv(p.sprite.getU0(), p.sprite.getV1()).setLight(LightTexture.FULL_BRIGHT);
            }

            MeshData mesh = builder.build();
            if (mesh != null) BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();
    }
}