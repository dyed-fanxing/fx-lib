package com.fanxing.lib.client.render.shape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class SphereRenderer {

    /**
     * 鐞冧綋锛氬甫UV缂╂斁锛堜娇鐢?QUADS 妯″紡锛屾渶浼樺疄鐜帮級锛屾寚瀹氱悆蹇?*********************************************************************************************
     *
     * @param pose     濮挎€?
     * @param consumer 娓叉煋鍣紙QUADS 鎴?TRIANGLES锛?
     * @param center   鐞冨績鍧愭爣
     * @param radius   鍗婂緞
     * @param segments 鍒嗘鏁?
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        int latSegments = segments / 2;
        float deltaTheta = Mth.TWO_PI / segments;
        float deltaPhi = Mth.PI / latSegments;

        for (int i = 0; i < latSegments; i++) {
            float phi1 = i * deltaPhi - Mth.HALF_PI;
            float phi2 = (i + 1) * deltaPhi - Mth.HALF_PI;
            float sinPhi1 = Mth.sin(phi1), cosPhi1 = Mth.cos(phi1);
            float sinPhi2 = Mth.sin(phi2), cosPhi2 = Mth.cos(phi2);
            float r1 = radius * cosPhi1;
            float r2 = radius * cosPhi2;
            float v1 = (float) i / latSegments * vScale;
            float v2 = (float) (i + 1) / latSegments * vScale;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float sinTheta1 = Mth.sin(theta1), cosTheta1 = Mth.cos(theta1);
                float sinTheta2 = Mth.sin(theta2), cosTheta2 = Mth.cos(theta2);
                float u1 = (float) j / segments * uScale;
                float u2 = (float) (j + 1) / segments * uScale;

                float x1 = r1 * cosTheta1, y1 = radius * sinPhi1, z1 = r1 * sinTheta1;
                float x2 = r2 * cosTheta1, y2 = radius * sinPhi2, z2 = r2 * sinTheta1;
                float x3 = r2 * cosTheta2, y3 = radius * sinPhi2, z3 = r2 * sinTheta2;
                float x4 = r1 * cosTheta2, y4 = radius * sinPhi1, z4 = r1 * sinTheta2;

                float nx1 = cosPhi1 * cosTheta1, nz1 = cosPhi1 * sinTheta1;
                float nx2 = cosPhi2 * cosTheta1, nz2 = cosPhi2 * sinTheta1;
                float nx3 = cosPhi2 * cosTheta2, nz3 = cosPhi2 * sinTheta2;
                float nx4 = cosPhi1 * cosTheta2, nz4 = cosPhi1 * sinTheta2;

                QuadRenderer.render(pose, consumer,
                        center.x() + x1, center.y() + y1, center.z() + z1,
                        center.x() + x2, center.y() + y2, center.z() + z2,
                        center.x() + x3, center.y() + y3, center.z() + z3,
                        center.x() + x4, center.y() + y4, center.z() + z4,
                        nx1, sinPhi1, nz1,
                        nx2, sinPhi2, nz2,
                        nx3, sinPhi2, nz3,
                        nx4, sinPhi1, nz4,
                        r, g, b, a, overlay, light,
                        u1, v1, u1, v2, u2, v2, u2, v1);
            }
        }
    }

    /**
     * 鐞冧綋锛氬甫UV缂╂斁锛堜娇鐢ㄩ粯璁?UV 缂╂斁 1.0锛夛紝鐞冨績鍦ㄥ師鐐?
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int r, int g, int b, int a, int overlay, int light) {
        render(pose, consumer, center, radius, segments, r, g, b, a, overlay, light, 1f, 1f);
    }

    /**
     * 娓叉煋閮ㄥ垎鐞冧綋锛堟敮鎸佷粠搴曢儴鍚戜笂鎴栭《閮ㄥ悜涓嬫覆鏌撴寚瀹氶珮搴︽瘮渚嬶級
     *
     * @param pose        濮挎€佺煩闃?
     * @param consumer    椤剁偣娑堣垂鑰咃紙QUADS 妯″紡锛?
     * @param start       鍩哄噯鐐癸紙鍗婄悆鏈€浣庣偣鎴栨渶楂樼偣浣嶇疆锛屽彇鍐充簬 yRatio 绗﹀彿锛?
     * @param radius      鐞冧綋鍗婂緞
     * @param yRatio      娓叉煋楂樺害姣斾緥锛岃寖鍥?[-1f, 1f]銆傜粷瀵瑰€艰〃绀烘覆鏌撻珮搴﹀崰鏁翠釜鐞冧綋楂樺害鐨勬瘮渚嬶紝
     *                    姝ｅ€间粠搴曢儴鍚戜笂娓叉煋锛岃礋鍊间粠椤堕儴鍚戜笅娓叉煋銆?
     * @param yOffset     鍨傜洿鍋忕Щ锛堢浉瀵逛簬鍩哄噯鐐癸級
     * @param segments    姘村钩鍒嗘鏁帮紙缁忓害鍒嗘锛?
     * @param latSegments 绾害鍒嗘鏁帮紙鏁翠釜鐞冧綋鐨勭含搴︽鏁帮紝搴旂瓑浜?segments/2锛?
     * @param ringRadius  棰勮绠楃殑鍚勭含搴﹀湀鍗婂緞鏁扮粍锛堥暱搴?latSegments+1锛?
     * @param ringY       棰勮绠楃殑鍚勭含搴﹀湀 Y 鍧愭爣鏁扮粍锛堢浉瀵逛簬鐞冨績锛岄暱搴?latSegments+1锛?
     * @param deltaTheta  姘村钩瑙掑害姝ラ暱锛堝姬搴︼紝閫氬父涓?2蟺/segments锛?
     * @param r,g,b,a     棰滆壊
     * @param overlay     瑕嗙洊灞?
     * @param light       鍏夌収
     * @param uScale      UV 妯悜缂╂斁
     * @param vScale      UV 绾靛悜缂╂斁
     * @param uOffset     U 鏂瑰悜婊氬姩鍋忕Щ
     * @param vOffset     V 鏂瑰悜婊氬姩鍋忕Щ
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int r, int g, int b, int a, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        // 闄愬埗 yRatio 鍦?[-1, 1] 鑼冨洿
        yRatio = Mth.clamp(yRatio, -1f, 1f);
        float absRatio = Math.abs(yRatio);
        if (absRatio < 1e-6f) return; // 鏃犳覆鏌撳尯鍩?

        int startIndex, endIndex;

        if (yRatio >= 0) {
            // 浠庡簳閮ㄥ悜涓婃覆鏌?
            startIndex = 0;
            endIndex = (int) (absRatio * latSegments);
        } else {
            // 浠庨《閮ㄥ悜涓嬫覆鏌?
            startIndex = latSegments - (int) (absRatio * latSegments);
            endIndex = latSegments;
        }

        // 杈圭晫淇濇姢
        startIndex = Mth.clamp(startIndex, 0, latSegments);
        endIndex = Mth.clamp(endIndex, 0, latSegments);
        if (startIndex >= endIndex) return;

        float invSteps = 1f / (endIndex - startIndex);

        for (int i = startIndex; i < endIndex; i++) {
            float r1 = ringRadius[i];
            float r2 = ringRadius[i + 1];
            float y1 = ringY[i] + yOffset;
            float y2 = ringY[i + 1] + yOffset;
            float y3 = y2;
            float y4 = y1;

            // 璁＄畻褰撳墠甯︾殑 V 鍧愭爣锛堢嚎鎬ф彃鍊硷紝鍙犲姞 vOffset锛?
            float t1 = (i - startIndex) * invSteps;
            float t2 = (i + 1 - startIndex) * invSteps;
            float v1 = t1 * vScale + vOffset;
            float v2 = t2 * vScale + vOffset;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);
                float u1 = ((float) j / segments) * uScale + uOffset;
                float u2 = ((float) (j + 1) / segments) * uScale + uOffset;

                // 鍥涗釜椤剁偣鍧愭爣锛堢浉瀵逛簬鐞冨績锛?
                float x1 = r1 * cos1, z1 = r1 * sin1;
                float x2 = r2 * cos1, z2 = r2 * sin1;
                float x3 = r2 * cos2, z3 = r2 * sin2;
                float x4 = r1 * cos2, z4 = r1 * sin2;

                // 娉曠嚎锛堝熀浜庡師濮嬬悆闈紝鍑忓幓 yOffset 杩樺師锛?
                float nx1 = cos1 * (r1 / radius);
                float ny1 = (y1 - yOffset) / radius;
                float nz1 = sin1 * (r1 / radius);
                float nx2 = cos1 * (r2 / radius);
                float ny2 = (y2 - yOffset) / radius;
                float nz2 = sin1 * (r2 / radius);
                float nx3 = cos2 * (r2 / radius);
                float ny3 = (y2 - yOffset) / radius;
                float nz3 = sin2 * (r2 / radius);
                float nx4 = cos2 * (r1 / radius);
                float ny4 = (y1 - yOffset) / radius;
                float nz4 = sin2 * (r1 / radius);

                QuadRenderer.render(pose, consumer,
                        start.x() + x1, start.y() + y1, start.z() + z1,
                        start.x() + x2, start.y() + y2, start.z() + z2,
                        start.x() + x3, start.y() + y3, start.z() + z3,
                        start.x() + x4, start.y() + y4, start.z() + z4,
                        nx1, ny1, nz1,
                        nx2, ny2, nz2,
                        nx3, ny3, nz3,
                        nx4, ny4, nz4,
                        r, g, b, a, overlay, light,
                        u1, v1, u1, v2, u2, v2, u2, v1);
            }
        }
    }

    /**
     * 娓叉煋閮ㄥ垎鐞冧綋锛堟棤 uOffset 鍜?vOffset锛屼袱鑰呴粯璁や负 0锛?
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int r, int g, int b, int a, int overlay, int light, float uScale, float vScale) {
        renderHemisphere(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                r, g, b, a, overlay, light, uScale, vScale, 0f, 0f);
    }



    /**
     * 鍗婄悆娓叉煋锛堝緞鍚戞笎鍙樿壊鐗堟湰锛夆€斺€?浠庝腑蹇冭壊鍒拌竟缂樿壊閫愰《鐐规彃鍊?
     * 閫傜敤浜庨渶瑕佸湪鍗婄悆闈笂鍛堢幇寰勫悜娓愬彉鏁堟灉鐨勫満鏅?
     */
    public static void renderHemisphereRadial(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                               int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                               int edgeR, int edgeG, int edgeB, int edgeA,
                                               int centerR, int centerG, int centerB, int centerA,
                                               int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        yRatio = Mth.clamp(yRatio, -1f, 1f);
        float absRatio = Math.abs(yRatio);
        if (absRatio < 1e-6f) return;

        int startIndex, endIndex;
        float vStart, vEnd;

        if (yRatio >= 0) {
            startIndex = 0;
            endIndex = (int) (absRatio * latSegments);
            vStart = 0f;
            vEnd = absRatio;
        } else {
            startIndex = latSegments - (int) (absRatio * latSegments);
            endIndex = latSegments;
            vStart = 1f - absRatio;
            vEnd = 1f;
        }

        startIndex = Mth.clamp(startIndex, 0, latSegments);
        endIndex = Mth.clamp(endIndex, 0, latSegments);
        if (startIndex >= endIndex) return;

        float invSteps = 1f / (endIndex - startIndex);

        for (int i = startIndex; i < endIndex; i++) {
            float r1 = ringRadius[i];
            float r2 = ringRadius[i + 1];
            float y1 = ringY[i] + yOffset;
            float y2 = ringY[i + 1] + yOffset;
            float y3 = y2;
            float y4 = y1;

            // 寰勫悜鍥犲瓙锛?=涓績杞达紝1=琛ㄩ潰杈圭紭
            float radial1 = r1 / radius;
            float radial2 = r2 / radius;

            // 姣忎釜椤剁偣鎻掑€奸鑹?
            int vr1 = (int) (centerR + (edgeR - centerR) * radial1);
            int vg1 = (int) (centerG + (edgeG - centerG) * radial1);
            int vb1 = (int) (centerB + (edgeB - centerB) * radial1);
            int va1 = (int) (centerA + (edgeA - centerA) * radial1);
            int vr2 = (int) (centerR + (edgeR - centerR) * radial2);
            int vg2 = (int) (centerG + (edgeG - centerG) * radial2);
            int vb2 = (int) (centerB + (edgeB - centerB) * radial2);
            int va2 = (int) (centerA + (edgeA - centerA) * radial2);

            float t1 = (i - startIndex) * invSteps;
            float t2 = (i + 1 - startIndex) * invSteps;
            float v1 = (vStart + (vEnd - vStart) * t1) * vScale + vOffset;
            float v2 = (vStart + (vEnd - vStart) * t2) * vScale + vOffset;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);
                float u1 = ((float) j / segments) * uScale + uOffset;
                float u2 = ((float) (j + 1) / segments) * uScale + uOffset;

                float x1 = r1 * cos1, z1 = r1 * sin1;
                float x2 = r2 * cos1, z2 = r2 * sin1;
                float x3 = r2 * cos2, z3 = r2 * sin2;
                float x4 = r1 * cos2, z4 = r1 * sin2;

                float nx1 = cos1 * (r1 / radius);
                float ny1 = (y1 - yOffset) / radius;
                float nz1 = sin1 * (r1 / radius);
                float nx2 = cos1 * (r2 / radius);
                float ny2 = (y2 - yOffset) / radius;
                float nz2 = sin1 * (r2 / radius);
                float nx3 = cos2 * (r2 / radius);
                float ny3 = (y2 - yOffset) / radius;
                float nz3 = sin2 * (r2 / radius);
                float nx4 = cos2 * (r1 / radius);
                float ny4 = (y1 - yOffset) / radius;
                float nz4 = sin2 * (r1 / radius);

                // 姣忎釜椤剁偣鐙珛棰滆壊
                consumer.addVertex(pose.pose(), start.x() + x1, start.y() + y1, start.z() + z1)
                        .setNormal(pose, nx1, ny1, nz1).setUv(u1, v1).setColor(vr1, vg1, vb1, va1).setOverlay(overlay).setLight(light);
                consumer.addVertex(pose.pose(), start.x() + x2, start.y() + y2, start.z() + z2)
                        .setNormal(pose, nx2, ny2, nz2).setUv(u1, v2).setColor(vr2, vg2, vb2, va2).setOverlay(overlay).setLight(light);
                consumer.addVertex(pose.pose(), start.x() + x3, start.y() + y3, start.z() + z3)
                        .setNormal(pose, nx3, ny3, nz3).setUv(u2, v2).setColor(vr2, vg2, vb2, va2).setOverlay(overlay).setLight(light);
                consumer.addVertex(pose.pose(), start.x() + x4, start.y() + y4, start.z() + z4)
                        .setNormal(pose, nx4, ny4, nz4).setUv(u2, v1).setColor(vr1, vg1, vb1, va1).setOverlay(overlay).setLight(light);
            }
        }
    }

    /**
     * 鍗婄悆娓叉煋锛堝緞鍚戞笎鍙樿壊鐗堟湰锛屾棤 UV 鍋忕Щ锛?
     */
    public static void renderHemisphereRadial(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                               int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                               int edgeR, int edgeG, int edgeB, int edgeA,
                                               int centerR, int centerG, int centerB, int centerA,
                                               int overlay, int light, float uScale, float vScale) {
        renderHemisphereRadial(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                edgeR, edgeG, edgeB, edgeA, centerR, centerG, centerB, centerA, overlay, light, uScale, vScale, 0f, 0f);
    }

    /**
     * 鍗婄悆娓叉煋锛堥€愰《鐐瑰僵铏?寰勫悜娓愬彉锛夆€斺€?鑹茬浉鐢?Y 鍧愭爣褰掍竴鍖栧€煎喅瀹氾紝涓績娣″嚭鐧借壊
     * @param hueOffset 鑹茬浉鍋忕Щ锛堟椂闂达級锛屼娇褰╄櫣娌?Y 鏂瑰悜娴佸姩
     */
    public static void renderHemisphereRainbowRadial(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                                      int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                                      float hueOffset, float capsuleBottomY, float capsuleTotalHeight,
                                                      int alpha, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        yRatio = Mth.clamp(yRatio, -1f, 1f);
        float absRatio = Math.abs(yRatio);
        if (absRatio < 1e-6f) return;

        int startIndex, endIndex;

        if (yRatio >= 0) {
            startIndex = 0;
            endIndex = (int) (absRatio * latSegments);
        } else {
            startIndex = latSegments - (int) (absRatio * latSegments);
            endIndex = latSegments;
        }

        startIndex = Mth.clamp(startIndex, 0, latSegments);
        endIndex = Mth.clamp(endIndex, 0, latSegments);
        if (startIndex >= endIndex) return;

        float invTotal = 1f / capsuleTotalHeight;

        for (int i = startIndex; i < endIndex; i++) {
            float r1 = ringRadius[i];
            float r2 = ringRadius[i + 1];
            float y1 = ringY[i] + yOffset;
            float y2 = ringY[i + 1] + yOffset;
            float y3 = y2;
            float y4 = y1;

            // 寰勫悜鍥犲瓙锛?=涓績杞达紝1=琛ㄩ潰杈圭紭
            float radial1 = r1 / radius;
            float radial2 = r2 / radius;

            // 鑹茬浉鐢?Y 鍧愭爣褰掍竴鍖栧€煎喅瀹?
            float t1 = (start.y() + y1 - capsuleBottomY) * invTotal;
            float t2 = (start.y() + y2 - capsuleBottomY) * invTotal;
            float hue1 = ((t1 + hueOffset) * 360F * vScale) % 360F;
            float hue2 = ((t2 + hueOffset) * 360F * vScale) % 360F;
            int c1 = CylinderRenderer.hueToArgb(hue1, alpha);
            int c2 = CylinderRenderer.hueToArgb(hue2, alpha);
            // 寰勫悜娣峰悎鐧借壊
            int c1r1 = CylinderRenderer.blendWhite(c1, 1F - radial1);
            int c2r2 = CylinderRenderer.blendWhite(c2, 1F - radial2);

            // UV 涓庡渾鏌辩粺涓€浣跨敤 Y 鍧愭爣褰掍竴鍖栧€硷紝纭繚鎺ュ彛杩炵画
            float v1 = t1 * vScale + vOffset;
            float v2 = t2 * vScale + vOffset;

            for (int j = 0; j < segments; j++) {
                float theta1 = j * deltaTheta;
                float theta2 = (j + 1) * deltaTheta;
                float cos1 = Mth.cos(theta1), sin1 = Mth.sin(theta1);
                float cos2 = Mth.cos(theta2), sin2 = Mth.sin(theta2);
                float u1 = ((float) j / segments) * uScale + uOffset;
                float u2 = ((float) (j + 1) / segments) * uScale + uOffset;

                float x1 = r1 * cos1, z1 = r1 * sin1;
                float x2 = r2 * cos1, z2 = r2 * sin1;
                float x3 = r2 * cos2, z3 = r2 * sin2;
                float x4 = r1 * cos2, z4 = r1 * sin2;

                float nx1 = cos1 * (r1 / radius);
                float ny1 = (y1 - yOffset) / radius;
                float nz1 = sin1 * (r1 / radius);
                float nx2 = cos1 * (r2 / radius);
                float ny2 = (y2 - yOffset) / radius;
                float nz2 = sin1 * (r2 / radius);
                float nx3 = cos2 * (r2 / radius);
                float ny3 = (y2 - yOffset) / radius;
                float nz3 = sin2 * (r2 / radius);
                float nx4 = cos2 * (r1 / radius);
                float ny4 = (y1 - yOffset) / radius;
                float nz4 = sin2 * (r1 / radius);

                consumer.addVertex(pose.pose(), start.x() + x1, start.y() + y1, start.z() + z1)
                        .setNormal(pose, nx1, ny1, nz1).setUv(u1, v1).setColor(FastColor.ARGB32.red(c1r1), FastColor.ARGB32.green(c1r1), FastColor.ARGB32.blue(c1r1), FastColor.ARGB32.alpha(c1r1)).setOverlay(overlay).setLight(light);
                consumer.addVertex(pose.pose(), start.x() + x2, start.y() + y2, start.z() + z2)
                        .setNormal(pose, nx2, ny2, nz2).setUv(u1, v2).setColor(FastColor.ARGB32.red(c2r2), FastColor.ARGB32.green(c2r2), FastColor.ARGB32.blue(c2r2), FastColor.ARGB32.alpha(c2r2)).setOverlay(overlay).setLight(light);
                consumer.addVertex(pose.pose(), start.x() + x3, start.y() + y3, start.z() + z3)
                        .setNormal(pose, nx3, ny3, nz3).setUv(u2, v2).setColor(FastColor.ARGB32.red(c2r2), FastColor.ARGB32.green(c2r2), FastColor.ARGB32.blue(c2r2), FastColor.ARGB32.alpha(c2r2)).setOverlay(overlay).setLight(light);
                consumer.addVertex(pose.pose(), start.x() + x4, start.y() + y4, start.z() + z4)
                        .setNormal(pose, nx4, ny4, nz4).setUv(u2, v1).setColor(FastColor.ARGB32.red(c1r1), FastColor.ARGB32.green(c1r1), FastColor.ARGB32.blue(c1r1), FastColor.ARGB32.alpha(c1r1)).setOverlay(overlay).setLight(light);
            }
        }
    }

    /**
     * 鍗婄悆娓叉煋锛堥€愰《鐐瑰僵铏?寰勫悜娓愬彉锛屾棤 UV 鍋忕Щ锛?
     */
    public static void renderHemisphereRainbowRadial(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                                      int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                                      float hueOffset, float capsuleBottomY, float capsuleTotalHeight,
                                                      int alpha, int overlay, int light, float uScale, float vScale) {
        renderHemisphereRainbowRadial(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                hueOffset, capsuleBottomY, capsuleTotalHeight, alpha, overlay, light, uScale, vScale, 0f, 0f);
    }

    // ==================== ARGB 閲嶈浇 ====================

    /**
     * 鐞冧綋锛氬甫UV缂╂斁锛屾帴鍙?ARGB 棰滆壊锛堜娇鐢?QUADS 妯″紡锛?
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int argb, int overlay, int light, float uScale, float vScale) {
        render(pose, consumer, center, radius, segments,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale);
    }

    /**
     * 鐞冧綋锛氫娇鐢ㄩ粯璁?UV 缂╂斁锛屾帴鍙?ARGB 棰滆壊
     */
    public static void render(PoseStack.Pose pose, VertexConsumer consumer, Vector3f center, float radius, int segments,
                              int argb, int overlay, int light) {
        render(pose, consumer, center, radius, segments, argb, overlay, light, 1f, 1f);
    }

    /**
     * 娓叉煋閮ㄥ垎鐞冧綋锛堟敮鎸侀珮搴︽瘮渚嬨€乁V婊氬姩锛夛紝鎺ュ彈 ARGB 棰滆壊
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int argb, int overlay, int light, float uScale, float vScale, float uOffset, float vOffset) {
        renderHemisphere(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                FastColor.ARGB32.red(argb), FastColor.ARGB32.green(argb), FastColor.ARGB32.blue(argb), FastColor.ARGB32.alpha(argb), overlay, light, uScale, vScale, uOffset, vOffset);
    }

    /**
     * 娓叉煋閮ㄥ垎鐞冧綋锛堟棤 uOffset/vOffset锛夛紝鎺ュ彈 ARGB 棰滆壊
     */
    public static void renderHemisphere(PoseStack.Pose pose, VertexConsumer consumer, Vector3f start, float radius, float yRatio, float yOffset,
                                        int segments, int latSegments, float[] ringRadius, float[] ringY, float deltaTheta,
                                        int argb, int overlay, int light, float uScale, float vScale) {
        renderHemisphere(pose, consumer, start, radius, yRatio, yOffset, segments, latSegments, ringRadius, ringY, deltaTheta,
                argb, overlay, light, uScale, vScale, 0f, 0f);
    }
}
