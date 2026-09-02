package com.fanxing.lib.util.collsion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CapsuleCCDUtils {
    /**
     * 获取实体视线上的碰撞检测实体列表（带方块阻挡）
     */
    public static List<Entity> getEntityHitResultsOnViewVector(Entity entity, float radius, double length, Predicate<Entity> filter, ClipContext.Block block, ClipContext.Fluid fluid) {
        Level level = entity.level();
        Vec3 start = entity.getEyePosition();
        // 使用胶囊体方块检测获取实际终点
        Vec3 end = getBlockHitResult(level, start, entity.getViewVector(1.0f), (float) length, radius, block, fluid).getLocation();
        return level.getEntities(entity, new AABB(start, end).inflate(radius), filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, end, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }


    /**
     * 获取碰撞检测实体列表（带方块阻挡）
     */
    public static List<Entity> getEntityHitResults(Entity entity, Vec3 start, float radius, double length, Predicate<Entity> filter, ClipContext.Block block, ClipContext.Fluid fluid) {
        Level level = entity.level();
        // 使用胶囊体方块检测获取实际终点
        Vec3 end = getBlockHitResult(level, start, entity.getViewVector(1.0f), (float) length, radius, block, fluid).getLocation();
        return level.getEntities(entity, new AABB(start, end).inflate(radius), filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, end, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }

    /**
     * 获取实体视线上的碰撞检测实体列表
     */
    public static List<Entity> getEntityHitResultsOnViewVector(Entity entity, float radius, double length, Predicate<Entity> filter) {
        Level level = entity.level();
        Vec3 viewVector = entity.getViewVector(1.0f);
        Vec3 start = entity.getEyePosition();
        Vec3 end = start.add(viewVector.scale(length));
        return level.getEntities(entity, new AABB(start, end).inflate(radius), filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, end, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }


    /**
     * 获取碰撞检测实体列表
     */
    public static List<Entity> getEntityHitResults(Entity entity, Vec3 start, Vec3 end, float radius, Predicate<Entity> filter) {
        Level level = entity.level();
        return level.getEntities(entity, new AABB(start, end).inflate(radius), filter)
                .stream().filter(target -> capsuleIntersectsAABB(start, end, radius, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
    }

    /**
     * 获取光束路径上最近的碰撞实体（带胶囊体方块阻挡检测）
     */
    @Nullable
    public static Entity getEntityHitResult(Entity entity, float radius, double length, Predicate<Entity> filter, ClipContext.Block block, ClipContext.Fluid fluid) {
        Level level = entity.level();
        Vec3 start = entity.getEyePosition();
        // 使用胶囊体方块检测获取实际终点
        return getEntityHitResult(entity, start, getBlockHitResult(level, start, entity.getViewVector(1.0f), (float) length, radius, block, fluid).getLocation(), radius, filter);
    }
    /**
     * 获取光束路径上最近的碰撞实体（按距离排序取第一个）
     * 比 getHitEntities 更高效，不创建列表，找到即返回
     *
     * @param entity 检测者（用于排除自身）
     * @param start  起点
     * @param end    终点
     * @param radius 胶囊体半径
     * @param filter 额外过滤条件（可为 null）
     * @return 最近的实体，若无则返回 null
     */
    @Nullable
    public static Entity getEntityHitResult(Entity entity, Vec3 start, Vec3 end, float radius, Predicate<Entity> filter) {
        Level level = entity.level();
        AABB searchBox = new AABB(start, end).inflate(radius);
        double closestDistSq = Double.MAX_VALUE;
        Entity closest = null;
        for (Entity target : level.getEntities(entity, searchBox, filter)) {
            if (!capsuleIntersectsAABB(start, end, radius, target.getBoundingBox())) continue;
            double distSq = target.distanceToSqr(start);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = target;
            }
        }
        return closest;
    }







    /**
     * 获取光束路径上第一个与胶囊体碰撞的方块，返回精确的 BlockHitResult
     * 使用 AABB 包围盒遍历路径上的方块，并用胶囊体与方块的 VoxelShape 进行精确相交检测。
     * 完全依赖 ClipContext.Block 和 ClipContext.Fluid 的判断逻辑。
     *
     * @param level     世界
     * @param start     起点
     * @param direction 方向（单位向量）
     * @param length    最大扫描距离
     * @param radius    胶囊体半径
     * @param block     方块检测类型（决定使用什么形状：碰撞箱/轮廓/视觉形状）
     * @param fluid     流体检测类型（决定是否检测流体）
     * @return BlockHitResult，命中则 type = BLOCK，否则 type = MISS
     */
    public static BlockHitResult getBlockHitResult(Level level, Vec3 start, Vec3 direction, float length, float radius,
                                                   ClipContext.Block block, ClipContext.Fluid fluid) {
        float step = 0.5f;
        float traveled = 0;
        Set<BlockPos> visited = new HashSet<>();
        CollisionContext collisionContext = CollisionContext.empty();

        while (traveled < length) {
            float segEnd = Math.min(traveled + step, length);
            Vec3 p1 = start.add(direction.scale(traveled));
            Vec3 p2 = start.add(direction.scale(segEnd));

            AABB segBox = new AABB(
                    Math.min(p1.x, p2.x) - radius,
                    Math.min(p1.y, p2.y) - radius,
                    Math.min(p1.z, p2.z) - radius,
                    Math.max(p1.x, p2.x) + radius,
                    Math.max(p1.y, p2.y) + radius,
                    Math.max(p1.z, p2.z) + radius
            );

            for (BlockPos pos : BlockPos.betweenClosed(
                    BlockPos.containing(segBox.minX, segBox.minY, segBox.minZ),
                    BlockPos.containing(segBox.maxX, segBox.maxY, segBox.maxZ)
            )) {
                if (visited.contains(pos)) continue;
                visited.add(pos);
                BlockState state = level.getBlockState(pos);
                FluidState fluidState = state.getFluidState();

                // 使用 ClipContext.Block 获取方块形状
                VoxelShape blockShape = block.get(state, level, pos, collisionContext);

                // 使用 ClipContext.Fluid 判断是否检测流体
                VoxelShape fluidShape = fluid.canPick(fluidState) ? fluidState.getShape(level, pos) : Shapes.empty();

                // 检测胶囊体是否与当前段的形状相交
                if (capsuleIntersectsVoxelShape(p1, p2, radius, blockShape, pos) ||
                        capsuleIntersectsVoxelShape(p1, p2, radius, fluidShape, pos)) {
                    // 找到碰撞，使用 AABB.clip 获取精确碰撞点
                    AABB blockBox = new AABB(pos);
                    Optional<Vec3> hitOpt = blockBox.clip(start, p2);
                    if (hitOpt.isPresent()) {
                        Vec3 hitPos = hitOpt.get();
                        Direction dir = Direction.getNearest(
                                hitPos.x - start.x,
                                hitPos.y - start.y,
                                hitPos.z - start.z
                        );
                        return new BlockHitResult(hitPos, dir, pos, false);
                    } else {
                        return new BlockHitResult(p2, Direction.getNearest(direction.x, direction.y, direction.z), pos, false);
                    }
                }
            }
            traveled = segEnd;
        }

        Vec3 endPos = start.add(direction.scale(length));
        Direction dir = Direction.getNearest(direction.x, direction.y, direction.z);
        return BlockHitResult.miss(endPos, dir, BlockPos.containing(endPos));
    }




    /**
     * 扫描光束路径上的方块，直到回调返回 false 或达到指定长度。
     *
     * @param level         世界
     * @param start         光束起点
     * @param direction     光束方向（单位向量）
     * @param length        最大扫描长度
     * @param radius        光束半径
     * @param step          分段步长
     * @param includeFluids 是否处理流体
     * @param blockHandler  处理每个符合条件的方块，返回 true 继续扫描，false 立即停止
     * @return 实际扫描到达的长度
     */
    public static float scanBlocks(Level level, Vec3 start, Vec3 direction, float length, float radius, float step,
                                   boolean includeFluids, BiPredicate<BlockPos, BlockState> blockHandler) {
        Set<BlockPos> visited = new HashSet<>();
        float traveled = 0f; // 从起点开始
        while (traveled < length) {
            float segEnd = Math.min(traveled + step, length);
            Vec3 p1 = start.add(direction.scale(traveled));
            Vec3 p2 = start.add(direction.scale(segEnd));

            AABB segBox = new AABB(
                    Math.min(p1.x, p2.x) - radius, Math.min(p1.y, p2.y) - radius, Math.min(p1.z, p2.z) - radius,
                    Math.max(p1.x, p2.x) + radius, Math.max(p1.y, p2.y) + radius, Math.max(p1.z, p2.z) + radius
            );

            for (BlockPos pos : BlockPos.betweenClosed(
                    BlockPos.containing(segBox.minX, segBox.minY, segBox.minZ),
                    BlockPos.containing(segBox.maxX, segBox.maxY, segBox.maxZ))) {
                if (visited.contains(pos)) continue;
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || (!includeFluids && !state.getFluidState().isEmpty())) continue;

                AABB blockBox = new AABB(pos);
                if (capsuleIntersectsAABB(p1, p2, radius, blockBox)) {
                    visited.add(pos);
                    if (!blockHandler.test(pos, state)) {
                        return (float) start.distanceTo(Vec3.atBottomCenterOf(pos));
                    }
                }
            }
            traveled = segEnd;
        }
        return length;
    }


    /**
     * 检测胶囊体与AABB是否碰撞（胶囊体的定义是圆柱的轴线和两个半球R）
     * 检测的范围是start到end的圆柱范围，以及两端两个半球的范围
     *
     * @param start 胶囊体起点
     * @param end   胶囊体终点
     * @param radius     胶囊体半径
     * @param aabb  AABB盒子
     * @return 是否碰撞
     */
    public static boolean capsuleIntersectsAABB(Vec3 start, Vec3 end, float radius, AABB aabb) {
        // 1. 找到胶囊体线段上离AABB最近的点
        Vec3 closestOnSegment = getClosestPointOnLineSegment(aabb.getCenter(), start, end);
        // 2. 找到AABB上离这个点最近的点
        // 3. 计算两点之间的距离平方 <= 距离平方小于等于半径平方则碰撞为ture，否则 false
        return aabb.distanceToSqr(closestOnSegment) <= radius * radius;
    }

    /**
     * 获取线段上离给定点最近的点
     *
     * @param point 目标点
     * @param start 线段起点
     * @param end   线段终点
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


    /**
     * 检测胶囊体是否与 VoxelShape 相交
     */
    private static boolean capsuleIntersectsVoxelShape(Vec3 start, Vec3 end, float radius, VoxelShape shape, BlockPos pos) {
        if (shape.isEmpty()) return false;
        for (AABB box : shape.toAabbs()) {
            AABB worldBox = box.move(pos.getX(), pos.getY(), pos.getZ());
            if (capsuleIntersectsAABB(start, end, radius, worldBox)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算胶囊体与 AABB 的碰撞区域加权中心点（用于爆炸定位）。
     *
     * 原理：在胶囊体端面圆盘上采样多个点，沿轴向发射射线与 AABB 求交，
     * 将所有交点的平均位置作为碰撞区域的几何中心。
     *
     * 适用于光束、激光等有体积的攻击判定，即使光束只擦过 AABB 边缘，
     * 也能正确计算出中心的爆炸位置。
     *
     * 降级策略：若采样未命中，则返回中轴线上与 AABB 轴向投影重叠区间的中心点。
     *
     * @param start            胶囊体起点
     * @param end              胶囊体终点
     * @param radius           胶囊体半径
     * @param aabb             目标 AABB
     * @param samplesPerRing   每层采样点数（建议 8）
     * @param rings            半径层数（建议 2~3，在半径 0~1 之间等分）
     * @return 碰撞区域中心点，若无碰撞则返回 null
     */
    @NotNull
    public static Vec3 getCapsuleAABBContactPoint(Vec3 start, Vec3 end, float radius, AABB aabb,
                                                  int samplesPerRing, int rings) {
        Vec3 dir = end.subtract(start).normalize();
        double length = start.distanceTo(end);
        // 2. 构造垂直于方向的局部基向量
        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(dir.dot(up)) > 0.99) {
            up = new Vec3(1, 0, 0);
        }
        Vec3 right = dir.cross(up).normalize();
        up = right.cross(dir).normalize();

        // 3. 采样端面圆盘上的点，发射射线与 AABB 求交
        List<Vec3> hits = new ArrayList<>();
        for (int r = 0; r < rings; r++) {
            float rFactor = (float) (r + 1) / rings;
            float rRad = radius * rFactor;
            for (int i = 0; i < samplesPerRing; i++) {
                double angle = i * 2 * Math.PI / samplesPerRing;
                double offX = rRad * Math.cos(angle);
                double offY = rRad * Math.sin(angle);
                Vec3 rayStart = start.add(right.scale(offX)).add(up.scale(offY));
                Vec3 rayEnd = rayStart.add(dir.scale(length));
                aabb.clip(rayStart, rayEnd).ifPresent(hits::add);
            }
        }

        // 4. 如果采样有命中，取平均点作为碰撞中心
        if (!hits.isEmpty()) {
            Vec3 avg = Vec3.ZERO;
            for (Vec3 p : hits) {
                avg = avg.add(p);
            }
            avg = avg.scale(1.0 / hits.size());
            return avg;
        }

        // 5. 降级方案：中轴线上与 AABB 轴向投影重叠区间的中心点
        double axialMin = Double.POSITIVE_INFINITY;
        double axialMax = Double.NEGATIVE_INFINITY;

        // 遍历 AABB 的 8 个顶点，计算它们在光束方向上的投影
        Vec3[] corners = {
                new Vec3(aabb.minX, aabb.minY, aabb.minZ),
                new Vec3(aabb.maxX, aabb.minY, aabb.minZ),
                new Vec3(aabb.minX, aabb.maxY, aabb.minZ),
                new Vec3(aabb.maxX, aabb.maxY, aabb.minZ),
                new Vec3(aabb.minX, aabb.minY, aabb.maxZ),
                new Vec3(aabb.maxX, aabb.minY, aabb.maxZ),
                new Vec3(aabb.minX, aabb.maxY, aabb.maxZ),
                new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ)
        };

        for (Vec3 corner : corners) {
            double t = corner.subtract(start).dot(dir);
            if (t < axialMin) axialMin = t;
            if (t > axialMax) axialMax = t;
        }

        // 夹紧到光束长度范围内
        axialMin = Math.max(0, axialMin);
        axialMax = Math.min(length, axialMax);

        // 取重叠区间的中点
        double midT = (axialMin + axialMax) / 2;
        return start.add(dir.scale(midT));
    }



}
