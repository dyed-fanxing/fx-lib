package com.fanxing.lib.client.particle.editor.strategy;



import com.fanxing.lib.client.particle.editor.base.AbstractPropertyParticle;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.util.Mth;

/**
 * @author dyed_fanxing
 * @since 2026/5/24 19:52
 */
@FunctionalInterface
public interface PositionStrategy {
    default void tick(AbstractPropertyParticle particle) {}
    void applyLerpPosition(AbstractPropertyParticle particle, float partialTick);
    // 加速度模式
    class Acceleration implements PositionStrategy {
        public double ax, ay, az;

        public Acceleration(double ax, double ay, double az) {
            this.ax = ax;
            this.ay = ay;
            this.az = az;
        }

        @Override
        public void tick(AbstractPropertyParticle particle) {
            particle.setVel(particle.getXd()+ax,particle.getYd()+ ay,particle.getZd()+az);
            particle.move(particle.getXd(),particle.getYd(),particle.getZd());
        }

        @Override
        public void applyLerpPosition(AbstractPropertyParticle particle, float partialTick) {
            particle.lerpX = Mth.lerp(partialTick, particle.getXo(), particle.getX());
            particle.lerpY = Mth.lerp(partialTick, particle.getYo(), particle.getY());
            particle.lerpZ = Mth.lerp(partialTick, particle.getZo(), particle.getZ());
        }
    }
    // 缓动模式
    class Ease implements PositionStrategy {
        public double startX, startY, startZ;
        public double endX, endY, endZ;
        public FloatUnaryOperator easeX, easeY, easeZ;

        public Ease(double startX, double startY, double startZ,
                                    double endX, double endY, double endZ,
                                    FloatUnaryOperator easeX,
                                    FloatUnaryOperator easeY,
                                    FloatUnaryOperator easeZ) {
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            this.endX = endX;
            this.endY = endY;
            this.endZ = endZ;
            this.easeX = easeX;
            this.easeY = easeY;
            this.easeZ = easeZ;
        }

        @Override
        public void applyLerpPosition(AbstractPropertyParticle particle, float partialTick) {
            float t = particle.getProgress(partialTick);
            if (t >= 1.0f) {
                particle.lerpX = endX;
                particle.lerpY = endY;
                particle.lerpZ = endZ;
                return;
            }
            particle.lerpX = startX + (endX - startX) * easeX.apply(t);
            particle.lerpY = startY + (endY - startY) * easeY.apply(t);
            particle.lerpZ = startZ + (endZ - startZ) * easeZ.apply(t);
        }
    }
}



