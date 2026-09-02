package com.fanxing.lib.client.particle.emit;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * 球体发射器 —— 纯局部坐标生成（原点周围，支持偏移）。
 * <p>
 * 所有球面方法均使用「面积自适应经纬网格」：
 * - 赤道附近粒子多，两极附近粒子少，保证球面面积密度一致。
 * - 粒子数 = nLon × nLat，由经纬网格控制。
 * - 便捷重载支持 segments 参数（自动映射 nLon=segments, nLat=segments）。
 * </p>
 *
 * @author dyed_fanxing
 * @since 2026/6/23 16:00
 */
public class OrbEmitter {

    // ==================== 方向发射 ====================

    /**
     * 球面发射（向外），粒子位于面积自适应经纬网格面心，方向径向向外。
     * 抖动只影响经纬度位置（球面上的散布），不影响半径。
     */
    public static void emitSurfaceOutward(float cx, float cy, float cz, float outerR,
                                          int nLon, int nLat, float jitter,
                                          RandomSource random, PositionEmitter emitter) {
        if (nLon < 3 || nLat < 2) return;

        float latStep = (float) (Math.PI / nLat);
        int totalParticles = nLon * nLat;

        double[] weights = new double[nLat];
        double totalWeight = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            weights[latIdx] = Math.max(0.01, Math.cos(lat));
            totalWeight += weights[latIdx];
        }

        int[] ringCounts = new int[nLat];
        int assigned = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            ringCounts[latIdx] = (int) Math.round(totalParticles * weights[latIdx] / totalWeight);
            assigned += ringCounts[latIdx];
        }
        int diff = totalParticles - assigned;
        for (int i = 0; i < Math.abs(diff); i++) {
            int idx = (diff > 0) ? i % nLat : (nLat - 1 - (i % nLat));
            ringCounts[idx] += (diff > 0) ? 1 : -1;
        }

        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            int ringCount = ringCounts[latIdx];
            if (ringCount <= 0) continue;

            float lonStep = Mth.TWO_PI / ringCount;
            for (int lonIdx = 0; lonIdx < ringCount; lonIdx++) {
                float finalLat = lat;
                float finalLon = lonIdx * Mth.TWO_PI / ringCount;

                if (jitter > 0 && random != null) {
                    float latJitter = (random.nextFloat() * 2f - 1f) * latStep * 0.5f * jitter;
                    finalLat += latJitter;
                    finalLat = Mth.clamp(finalLat, (float) -Math.PI/2, (float) Math.PI/2);
                    float lonJitter = (random.nextFloat() * 2f - 1f) * lonStep * 0.5f * jitter;
                    finalLon += lonJitter;
                    finalLon = finalLon % Mth.TWO_PI;
                    if (finalLon < 0) finalLon += Mth.TWO_PI;
                }

                float cosLatFinal = (float) Math.cos(finalLat);
                float x = outerR * cosLatFinal * (float) Math.cos(finalLon);
                float y = outerR * (float) Math.sin(finalLat);
                float z = outerR * cosLatFinal * (float) Math.sin(finalLon);

                float dx = cosLatFinal * (float) Math.cos(finalLon);
                float dy = (float) Math.sin(finalLat);
                float dz = cosLatFinal * (float) Math.sin(finalLon);

                emitter.accept(cx + x, cy + y, cz + z, dx, dy, dz);
            }
        }
    }

    /**
     * 球面发射（向内），粒子位于面积自适应经纬网格面心，方向径向向内。
     * 抖动只影响经纬度位置，不影响半径。
     */
    public static void emitSurfaceInward(float cx, float cy, float cz, float outerR,
                                         int nLon, int nLat, float jitter,
                                         RandomSource random, PositionEmitter emitter) {
        if (nLon < 3 || nLat < 2) return;

        float latStep = (float) (Math.PI / nLat);
        int totalParticles = nLon * nLat;

        double[] weights = new double[nLat];
        double totalWeight = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            weights[latIdx] = Math.max(0.01, Math.cos(lat));
            totalWeight += weights[latIdx];
        }

        int[] ringCounts = new int[nLat];
        int assigned = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            ringCounts[latIdx] = (int) Math.round(totalParticles * weights[latIdx] / totalWeight);
            assigned += ringCounts[latIdx];
        }
        int diff = totalParticles - assigned;
        for (int i = 0; i < Math.abs(diff); i++) {
            int idx = (diff > 0) ? i % nLat : (nLat - 1 - (i % nLat));
            ringCounts[idx] += (diff > 0) ? 1 : -1;
        }

        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            int ringCount = ringCounts[latIdx];
            if (ringCount <= 0) continue;

            float lonStep = Mth.TWO_PI / ringCount;
            for (int lonIdx = 0; lonIdx < ringCount; lonIdx++) {
                float finalLat = lat;
                float finalLon = lonIdx * Mth.TWO_PI / ringCount;

                if (jitter > 0 && random != null) {
                    float latJitter = (random.nextFloat() * 2f - 1f) * latStep * 0.5f * jitter;
                    finalLat += latJitter;
                    finalLat = Mth.clamp(finalLat, (float) -Math.PI/2, (float) Math.PI/2);
                    float lonJitter = (random.nextFloat() * 2f - 1f) * lonStep * 0.5f * jitter;
                    finalLon += lonJitter;
                    finalLon = finalLon % Mth.TWO_PI;
                    if (finalLon < 0) finalLon += Mth.TWO_PI;
                }

                float cosLatFinal = (float) Math.cos(finalLat);
                float x = outerR * cosLatFinal * (float) Math.cos(finalLon);
                float y = outerR * (float) Math.sin(finalLat);
                float z = outerR * cosLatFinal * (float) Math.sin(finalLon);

                float dx = cosLatFinal * (float) Math.cos(finalLon);
                float dy = (float) Math.sin(finalLat);
                float dz = cosLatFinal * (float) Math.sin(finalLon);

                emitter.accept(cx + x, cy + y, cz + z, -dx, -dy, -dz);
            }
        }
    }

    /**
     * 中心向外发射（从球心出发），方向均匀覆盖球面，数量由面积自适应经纬网格控制。
     * 抖动只影响经纬度位置（方向散布），位置始终在球心。
     */
    public static void emitCenterOutward(float cx, float cy, float cz,
                                         int nLon, int nLat, float jitter,
                                         RandomSource random, PositionEmitter emitter) {
        if (nLon < 3 || nLat < 2) return;

        float latStep = (float) (Math.PI / nLat);
        int totalParticles = nLon * nLat;

        double[] weights = new double[nLat];
        double totalWeight = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            weights[latIdx] = Math.max(0.01, Math.cos(lat));
            totalWeight += weights[latIdx];
        }

        int[] ringCounts = new int[nLat];
        int assigned = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            ringCounts[latIdx] = (int) Math.round(totalParticles * weights[latIdx] / totalWeight);
            assigned += ringCounts[latIdx];
        }
        int diff = totalParticles - assigned;
        for (int i = 0; i < Math.abs(diff); i++) {
            int idx = (diff > 0) ? i % nLat : (nLat - 1 - (i % nLat));
            ringCounts[idx] += (diff > 0) ? 1 : -1;
        }

        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            int ringCount = ringCounts[latIdx];
            if (ringCount <= 0) continue;

            float lonStep = Mth.TWO_PI / ringCount;
            for (int lonIdx = 0; lonIdx < ringCount; lonIdx++) {
                float finalLat = lat;
                float finalLon = lonIdx * Mth.TWO_PI / ringCount;

                if (jitter > 0 && random != null) {
                    float latJitter = (random.nextFloat() * 2f - 1f) * latStep * 0.5f * jitter;
                    finalLat += latJitter;
                    finalLat = Mth.clamp(finalLat, (float) -Math.PI/2, (float) Math.PI/2);
                    float lonJitter = (random.nextFloat() * 2f - 1f) * lonStep * 0.5f * jitter;
                    finalLon += lonJitter;
                    finalLon = finalLon % Mth.TWO_PI;
                    if (finalLon < 0) finalLon += Mth.TWO_PI;
                }

                float cosLatFinal = (float) Math.cos(finalLat);
                float dx = cosLatFinal * (float) Math.cos(finalLon);
                float dy = (float) Math.sin(finalLat);
                float dz = cosLatFinal * (float) Math.sin(finalLon);

                emitter.accept(cx, cy, cz, dx, dy, dz);
            }
        }
    }

    // ==================== 便捷重载：segments 自动映射 nLon=segments, nLat=segments ====================

    public static void emitSurfaceOutward(float cx, float cy, float cz, float outerR,
                                          int segments, float jitter,
                                          RandomSource random, PositionEmitter emitter) {
        int nLon = Math.max(3, segments);
        int nLat = Math.max(2, segments);
        emitSurfaceOutward(cx, cy, cz, outerR, nLon, nLat, jitter, random, emitter);
    }

    public static void emitSurfaceInward(float cx, float cy, float cz, float outerR,
                                         int segments, float jitter,
                                         RandomSource random, PositionEmitter emitter) {
        int nLon = Math.max(3, segments);
        int nLat = Math.max(2, segments);
        emitSurfaceInward(cx, cy, cz, outerR, nLon, nLat, jitter, random, emitter);
    }

    public static void emitCenterOutward(float cx, float cy, float cz,
                                         int segments, float jitter,
                                         RandomSource random, PositionEmitter emitter) {
        int nLon = Math.max(3, segments);
        int nLat = Math.max(2, segments);
        emitCenterOutward(cx, cy, cz, nLon, nLat, jitter, random, emitter);
    }

    // 无抖动便捷重载
    public static void emitSurfaceOutward(float cx, float cy, float cz, float outerR,
                                          int segments, RandomSource random, PositionEmitter emitter) {
        emitSurfaceOutward(cx, cy, cz, outerR, segments, 0f, random, emitter);
    }

    public static void emitSurfaceInward(float cx, float cy, float cz, float outerR,
                                         int segments, RandomSource random, PositionEmitter emitter) {
        emitSurfaceInward(cx, cy, cz, outerR, segments, 0f, random, emitter);
    }

    public static void emitCenterOutward(float cx, float cy, float cz,
                                         int segments, RandomSource random, PositionEmitter emitter) {
        emitCenterOutward(cx, cy, cz, segments, 0f, random, emitter);
    }

    // 无抖动全参重载
    public static void emitSurfaceOutward(float cx, float cy, float cz, float outerR,
                                          int nLon, int nLat,
                                          RandomSource random, PositionEmitter emitter) {
        emitSurfaceOutward(cx, cy, cz, outerR, nLon, nLat, 0f, random, emitter);
    }

    public static void emitSurfaceInward(float cx, float cy, float cz, float outerR,
                                         int nLon, int nLat,
                                         RandomSource random, PositionEmitter emitter) {
        emitSurfaceInward(cx, cy, cz, outerR, nLon, nLat, 0f, random, emitter);
    }

    public static void emitCenterOutward(float cx, float cy, float cz,
                                         int nLon, int nLat,
                                         RandomSource random, PositionEmitter emitter) {
        emitCenterOutward(cx, cy, cz, nLon, nLat, 0f, random, emitter);
    }

    // ==================== 随机体积填充（爆炸、烟雾等，用 count） ====================

    public static void fillVolume(float cx, float cy, float cz, float innerR, float outerR, int count,
                                  RandomSource random, PositionPlacer placer) {
        for (int i = 0; i < count; i++) {
            float r;
            if (innerR <= 0) {
                r = outerR * (float) Math.cbrt(random.nextFloat());
            } else {
                float innerCube = innerR * innerR * innerR;
                float outerCube = outerR * outerR * outerR;
                r = (float) Math.cbrt(innerCube + random.nextFloat() * (outerCube - innerCube));
            }
            float u = random.nextFloat(), v = random.nextFloat();
            float phi = (float) Math.acos(2 * v - 1);
            float theta = Mth.TWO_PI * u;
            placer.accept(
                    cx + r * (float) (Math.sin(phi) * Math.cos(theta)),
                    cy + r * (float) (Math.sin(phi) * Math.sin(theta)),
                    cz + r * (float) Math.cos(phi)
            );
        }
    }

    public static void fillVolumeGaussian(float cx, float cy, float cz, float innerR, float outerR, int count,
                                          float sigmaFactor, RandomSource random, PositionPlacer placer) {
        float sigma = outerR * sigmaFactor;
        for (int i = 0; i < count; i++) {
            float r = Math.abs((float) random.nextGaussian() * sigma);
            r = Mth.clamp(r, innerR, outerR);
            float u = random.nextFloat(), v = random.nextFloat();
            float phi = (float) Math.acos(2 * v - 1);
            float theta = Mth.TWO_PI * u;
            placer.accept(
                    cx + r * (float) (Math.sin(phi) * Math.cos(theta)),
                    cy + r * (float) (Math.sin(phi) * Math.sin(theta)),
                    cz + r * (float) Math.cos(phi)
            );
        }
    }

    // ==================== 球面填充（面积自适应经纬网格，无方向） ====================

    /**
     * 面积自适应经纬网格球面填充 —— 赤道多、极地少，面积密度一致。
     * 总粒子数 = nLon × nLat。
     */
    public static void fillSurface(float cx, float cy, float cz, float outerR,
                                   int nLon, int nLat, float jitter,
                                   RandomSource random, PositionPlacer placer) {
        if (nLon < 3 || nLat < 2) return;

        float latStep = (float) (Math.PI / nLat);
        int totalParticles = nLon * nLat;

        double[] weights = new double[nLat];
        double totalWeight = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            weights[latIdx] = Math.max(0.01, Math.cos(lat));
            totalWeight += weights[latIdx];
        }

        int[] ringCounts = new int[nLat];
        int assigned = 0;
        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            ringCounts[latIdx] = (int) Math.round(totalParticles * weights[latIdx] / totalWeight);
            assigned += ringCounts[latIdx];
        }
        int diff = totalParticles - assigned;
        for (int i = 0; i < Math.abs(diff); i++) {
            int idx = (diff > 0) ? i % nLat : (nLat - 1 - (i % nLat));
            ringCounts[idx] += (diff > 0) ? 1 : -1;
        }

        for (int latIdx = 0; latIdx < nLat; latIdx++) {
            float lat = (float) (-Math.PI / 2 + (latIdx + 0.5f) * latStep);
            int ringCount = ringCounts[latIdx];
            if (ringCount <= 0) continue;

            float lonStep = Mth.TWO_PI / ringCount;
            for (int lonIdx = 0; lonIdx < ringCount; lonIdx++) {
                float finalLat = lat;
                float finalLon = lonIdx * Mth.TWO_PI / ringCount;

                if (jitter > 0 && random != null) {
                    float latJitter = (random.nextFloat() * 2f - 1f) * latStep * 0.5f * jitter;
                    finalLat += latJitter;
                    finalLat = Mth.clamp(finalLat, (float) -Math.PI/2, (float) Math.PI/2);
                    float lonJitter = (random.nextFloat() * 2f - 1f) * lonStep * 0.5f * jitter;
                    finalLon += lonJitter;
                    finalLon = finalLon % Mth.TWO_PI;
                    if (finalLon < 0) finalLon += Mth.TWO_PI;
                }

                float cosLatFinal = (float) Math.cos(finalLat);
                float x = outerR * cosLatFinal * (float) Math.cos(finalLon);
                float y = outerR * (float) Math.sin(finalLat);
                float z = outerR * cosLatFinal * (float) Math.sin(finalLon);
                placer.accept(cx + x, cy + y, cz + z);
            }
        }
    }

    /**
     * 便捷：使用 segments 自动映射（nLon=segments, nLat=segments）。
     */
    public static void fillSurface(float cx, float cy, float cz, float outerR,
                                   int segments, float jitter,
                                   RandomSource random, PositionPlacer placer) {
        int nLon = Math.max(3, segments);
        int nLat = Math.max(2, segments);
        fillSurface(cx, cy, cz, outerR, nLon, nLat, jitter, random, placer);
    }

    // 无抖动重载
    public static void fillSurface(float cx, float cy, float cz, float outerR,
                                   int nLon, int nLat,
                                   RandomSource random, PositionPlacer placer) {
        fillSurface(cx, cy, cz, outerR, nLon, nLat, 0f, random, placer);
    }

    public static void fillSurface(float cx, float cy, float cz, float outerR,
                                   int segments,
                                   RandomSource random, PositionPlacer placer) {
        fillSurface(cx, cy, cz, outerR, segments, 0f, random, placer);
    }
}