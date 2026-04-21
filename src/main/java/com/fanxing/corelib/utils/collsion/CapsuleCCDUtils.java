package com.fanxing.corelib.utils.collsion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CapsuleCCDUtils {
    /**
     * 获取实体视线上的碰撞检测实体列表
     */
    public static List<Entity> getHitEntitiesOnViewVector(Entity entity,float radius, double length, Predicate<Entity> filter,  ClipContext.Block block, ClipContext.Fluid fluid) {
        Level level = entity.level();
        Vec3 start = entity.getEyePosition();
        Vec3 end = start.add(entity.getViewVector(1.0f).scale(length));
        // 光束的射线检测，如果路径上被方块阻挡，则最终位置替换成该方块位置
        BlockHitResult clip = level.clip(new ClipContext(start, end, block, fluid, entity));
        // 攻击终点若为null 或 碰撞的攻击终点位置和上一次的攻击终点位置发生变化，则进行更新
        double disSqr = start.distanceToSqr(clip.getLocation());
        if (length*length - disSqr >= Mth.EPSILON) {
            end = clip.getLocation();
        }
        Vec3 finalEnd = end;
        return level.getEntities(entity, new AABB(start, end).inflate(radius),filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, finalEnd, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }

    /**
     * 获取实体视线上的碰撞检测实体列表
     */
    public static List<Entity> getHitEntitiesOnViewVector(Entity entity,float radius, double length, Predicate<Entity> filter) {
        Level level = entity.level();
        Vec3 viewVector = entity.getViewVector(1.0f);
        Vec3 start = entity.getEyePosition();
        Vec3 end = start.add(viewVector.scale(length));
        return level.getEntities(entity, new AABB(start, end).inflate(radius),filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, end, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }
    /**
     * 获取碰撞检测实体列表
     */
    public static List<Entity> getHitEntities(Entity entity,Vec3 start,Vec3 end,float radius, Predicate<Entity> filter) {
        Level level = entity.level();
        return level.getEntities(entity, new AABB(start, end).inflate(radius),filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, end, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }

    /**
     * 检测胶囊体与AABB是否碰撞（胶囊体的定义是圆柱的轴线和两个半球R）
     * 检测的范围是start到end的圆柱范围，以及两端两个半球的范围
     * @param start 胶囊体起点
     * @param end 胶囊体终点
     * @param r 胶囊体半径
     * @param aabb AABB盒子
     * @return 是否碰撞
     */
    public static boolean capsuleIntersectsAABB(Vec3 start, Vec3 end, float r, AABB aabb) {
        // 1. 找到胶囊体线段上离AABB最近的点
        Vec3 closestOnSegment = getClosestPointOnLineSegment(aabb.getCenter(), start, end);
        // 2. 找到AABB上离这个点最近的点
        // 3. 计算两点之间的距离平方 <= 距离平方小于等于半径平方则碰撞为ture，否则 false
        return aabb.distanceToSqr(closestOnSegment) <= r * r;
    }

    /**
     * 获取线段上离给定点最近的点
     * @param point 目标点
     * @param start 线段起点
     * @param end 线段终点
     * @return 线段上最近的点
     */
    public static Vec3 getClosestPointOnLineSegment(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 line = end.subtract(start);
        double lineSqr = line.lengthSqr();
        // 线段退化为点
        if (lineSqr < 1e-6) {
            return start;
        }

        // 计算投影比例
        double ratio = point.subtract(start).dot(line) / lineSqr;
        // 夹紧到[0,1]范围内
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        return start.add(line.scale(ratio));
    }


}
