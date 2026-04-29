package com.fanxing.lib.util.collsion;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FanXing
 * @since 2025-09-27 13:29
 */
public class CollisionDetectionUtils {


    private static final Logger log = LoggerFactory.getLogger(CollisionDetectionUtils.class);





    // ==================== 椭球与线段碰撞检测（新增） ====================
    public static boolean isPointInsideEllipsoid(Vec3 point, Vec3 center, double a, double b, double c) {
        double dx = (point.x - center.x) / a;
        double dy = (point.y - center.y) / b;
        double dz = (point.z - center.z) / c;
        return dx * dx + dy * dy + dz * dz <= 1.0;
    }
    /**
     * 仅判断线段是否与椭球相交（不计算交点）
     * @param p1     线段起点
     * @param p2     线段终点
     * @param center 椭球中心
     * @param a      X半轴长
     * @param b      Y半轴长
     * @param c      Z半轴长
     * @return true 如果线段与椭球有交点
     */
    public static boolean doesSegmentIntersectEllipsoid(Vec3 p1, Vec3 p2, Vec3 center,
                                                        double a, double b, double c) {
        return getSegmentEllipsoidIntersection(p1, p2, center, a, b, c) != null;
    }

    /**
     * 计算线段与椭球的第一个交点（弹射物进入椭球的点）
     * @param p1     线段起点（通常为上一 tick 位置）
     * @param p2     线段终点（当前 tick 位置）
     * @param center 椭球中心
     * @param a      X半轴长
     * @param b      Y半轴长
     * @param c      Z半轴长
     * @return 交点世界坐标，如果没有交点则返回 null
     */
    @Nullable
    public static Vec3 getSegmentEllipsoidIntersection(Vec3 p1, Vec3 p2, Vec3 center,
                                                       double a, double b, double c) {
        // 将线段变换到椭球局部坐标系（以椭球中心为原点）
        Vec3 start = p1.subtract(center);
        Vec3 dir = p2.subtract(p1);
        double a2 = a * a;
        double b2 = b * b;
        double c2 = c * c;

        // 解二次方程 A*t^2 + B*t + C = 0
        double A = dir.x * dir.x / a2 + dir.y * dir.y / b2 + dir.z * dir.z / c2;
        double B = 2.0 * (start.x * dir.x / a2 + start.y * dir.y / b2 + start.z * dir.z / c2);
        double C = start.x * start.x / a2 + start.y * start.y / b2 + start.z * start.z / c2 - 1.0;

        // 处理退化情况（线段退化为点或方向导致 A == 0）
        if (Math.abs(A) < 1e-8) {
            // 此时方程为 B*t + C = 0
            if (Math.abs(B) < 1e-8) return null; // 无解或恒成立
            double t = -C / B;
            if (t >= 0 && t <= 1) {
                Vec3 hitLocal = start.add(dir.scale(t));
                return hitLocal.add(center);
            }
            return null;
        }

        double discriminant = B * B - 4.0 * A * C;
        if (discriminant < 0) return null;

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-B - sqrtDisc) / (2.0 * A);
        double t2 = (-B + sqrtDisc) / (2.0 * A);

        // 取最小的 t 在 [0,1] 范围内（即第一次进入椭球的点）
        double t = -1;
        if (t1 >= 0 && t1 <= 1) t = t1;
        else if (t2 >= 0 && t2 <= 1) t = t2;
        else return null;

        Vec3 hitLocal = start.add(dir.scale(t));
        return hitLocal.add(center);
    }


}
