package com.fanxing.lib.entity.summon;

import com.fanxing.lib.entity.EffectEntity;
import com.fanxing.lib.util.collsion.CapsuleCCDUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * @author dyed_fanxing
 * @since 2026/6/15 17:55
 */
public abstract class LaserBeam extends Summons implements IEntityWithComplexSpawn, EffectEntity {
    protected float radius = 1.0f;            // 半径大小

    private static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(LaserBeam.class, EntityDataSerializers.FLOAT);

    public LaserBeam(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public LaserBeam(EntityType<?> entityType, Level level, Entity owner) {
        super(entityType, level, owner);
    }


    public void applyHitEntities(Vec3 start,Vec3 end){
        List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(radius), this::canHitEntity)
                .stream().filter(target -> CapsuleCCDUtils.capsuleIntersectsAABB(start, end, radius * 0.5f, target.getBoundingBox()))
                .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(start))).toList();
        for (LivingEntity target : livingEntities) {
            target.hurt(damageSources().source(damageSource(), this, getOwner() == null ? this : owner), attackDamage());
        }
    }

    protected abstract ResourceKey<DamageType> damageSource();
    protected abstract float attackDamage();


    public float getLength() {
        return this.entityData.get(LENGTH);
    }
    public void setLength(float length){
        this.entityData.set(LENGTH, length);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(LENGTH, 16f);
    }


    @Override
    public void writeSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(getLength());
    }

    @Override
    public void readSpawnData(@NotNull RegistryFriendlyByteBuf buffer) {
        setLength(buffer.readFloat());
    }
}
