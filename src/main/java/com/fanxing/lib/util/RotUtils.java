package com.fanxing.lib.util;


import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 向量旋转工具
 */
public class RotUtils {

    // 航偏角度，同MC默认lookAt方法计算方式，对齐MC世界坐标Z轴
    public static float yRotD(Vec3 vec) {
        return Mth.wrapDegrees((float)(Mth.atan2(vec.z,vec.x) * Mth.RAD_TO_DEG - 90.0F));
    }
    // 仰俯角度，同MC默认lookAt方法计算方式，对齐MC世界坐标Y轴
    public static float xRotD(Vec3 vec) {
        return Mth.wrapDegrees((float)(-(Mth.atan2(vec.y, vec.horizontalDistance()) * Mth.RAD_TO_DEG)));
    }
    public static float[] yxRotD(Vec3 vec){
        return new float[]{Mth.wrapDegrees((float)(Mth.atan2(vec.z,vec.x) * Mth.RAD_TO_DEG - 90.0F)),Mth.wrapDegrees((float)(-(Mth.atan2(vec.y, vec.horizontalDistance()) * Mth.RAD_TO_DEG)))};
    }

    // 航偏角度，同上
    public static float yRotD(double x,double z) {
        return Mth.wrapDegrees((float)(Mth.atan2(x, z) * Mth.RAD_TO_DEG - 90.0F));
    }
    // 仰俯角度，同上
    public static float xRotD(double y,double hd) {
        return Mth.wrapDegrees((float)(-(Mth.atan2(y, hd) * Mth.RAD_TO_DEG)));
    }


    // 航偏弧度，同MC默认lookAt方法计算方式，对齐MC世界坐标Z轴
    public static float yRotR(Vec3 vec) {
        return (float) (Mth.atan2(vec.x,vec.z) - 0.5);
    }
    // 仰俯弧度，同MC默认lookAt方法计算方式，对齐MC世界坐标Y轴
    public static float xRotR(Vec3 vec) {
        return (float)(-Mth.atan2(vec.y, vec.horizontalDistance()));
    }
    // 航偏弧度，同上
    public static float yRotR(double x,double z) {
        return (float) (Mth.atan2(x,z) - 0.5);
    }
    // 仰俯弧度，同上
    public static float xRotR(double y,double hd) {
        return (float)(-Mth.atan2(y, hd));
    }


    /**
     * 实体看向向量方向，不适用于弹射物，弹射物请使用下方的专用方法
     * @param entity 实体，非弹射物
     * @param vec 向量
     */
    public static void lookVec(Entity entity,Vec3 vec) {
        entity.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(vec.y, vec.horizontalDistance()) * Mth.RAD_TO_DEG))));
        entity.setYRot(Mth.wrapDegrees((float)(Mth.atan2(vec.z,vec.x) * Mth.RAD_TO_DEG - 90.0F)));
    }


    /*
        !!!由于原版的弹射物shoot方法所调用的设置旋转的逻辑与Entity实体的逻辑不同
        导致弹射物的视线方向和运动方向不一样，下方的方法是使用shoot里的设置旋转的逻辑
        以同步Entity的lookAt方法名，方便设置弹射物的方向
    */
    public static float shootYRot(Vec3 vec){
        return (float)(Mth.atan2(vec.x, vec.z) * Mth.RAD_TO_DEG);
    }
    public static float shootXRot(Vec3 vec){
        return  (float)(Mth.atan2(vec.y, vec.horizontalDistance())  * Mth.RAD_TO_DEG);
    }
    // 航偏角度
    public static float shootYRot(double x,double z) {
        return (float)(Mth.atan2(x, z) * Mth.RAD_TO_DEG);
    }
    // 物理仰俯角度（未对齐mc y轴）
    public static float shootXRot(double y,double d) {
        return (float)(Mth.atan2(y, d)  * Mth.RAD_TO_DEG);
    }

    public static void absRotateByShoot(Entity entity, Entity target){
        Vec3 vec = new Vec3(target.getX() - entity.getX(),target.getEyeY() - entity.getY(),target.getZ() - entity.getZ());
        entity.absRotateTo(shootXRot(vec.y,vec.horizontalDistance()),shootYRot(vec.x,vec.z));
    }

    public static void lookAtShoot(Entity entity, Entity target){
        lookVecShoot(entity, new Vec3(target.getX() - entity.getX(),target.getY() - entity.getY(),target.getZ() - entity.getZ()));
    }
    public static void lookAtShoot(Entity entity, Vec3 targetPos){
        lookVecShoot(entity,new Vec3(targetPos.x - entity.getX(),targetPos.y - entity.getY(),targetPos.z - entity.getZ()));
    }
    public static void lookAtShoot(Entity entity, double x,double y,double z){
        lookVecShoot(entity,new Vec3(x - entity.getX(),y - entity.getY(),z - entity.getZ()));
    }
    public static void lookAtBodyShoot(Entity entity, Entity target){
        lookVecShoot(entity, new Vec3(target.getX() - entity.getX(),target.getY(0.5f) - entity.getY(0.5f),target.getZ() - entity.getZ()));
    }
    public static void lookAtBodyShoot(Entity entity, Vec3 targetPos){
        lookVecShoot(entity,new Vec3(targetPos.x - entity.getX(),targetPos.y - entity.getY(0.5f),targetPos.z - entity.getZ()));
    }
    public static void lookAtBodyShoot(Entity entity, double x,double y,double z){
        lookVecShoot(entity,new Vec3(x - entity.getX(),y - entity.getY(0.5f),z - entity.getZ()));
    }
    public static void lookAtEyeShoot(Entity entity, Entity target){
        lookVecShoot(entity,new Vec3(target.getX() - entity.getX(),target.getEyeY() - entity.getEyeY(),target.getZ() - entity.getZ()));
    }
    public static void lookAtEyeShoot(Entity entity, Vec3 targetPos){
        lookVecShoot(entity,new Vec3(targetPos.x - entity.getX(),targetPos.y - entity.getEyeY(),targetPos.z - entity.getZ()));
    }
    public static void lookAtEyeShoot(Entity entity, double x,double y,double z){
        lookVecShoot(entity,new Vec3(x - entity.getX(),y - entity.getEyeY(),z - entity.getZ()));
    }
    /**
     * 弹射物看向矢量方向
     * @param entity 弹射物，不可用于实体
     * @param vec 矢量，常用于射击方向，即将要运动的方向
     */
    public static void lookVecShoot(Entity entity,Vec3 vec){
        entity.setXRot((float)(Mth.atan2(vec.y, vec.horizontalDistance())  * Mth.RAD_TO_DEG));
        entity.setYRot((float)(Mth.atan2(vec.x, vec.z) * Mth.RAD_TO_DEG));
    }



    /**
     * 将向量（坐标）vec对齐MC世界Roll翻滚方向
     */
    public static Vec3 zRot(Vec3 vec,float roll) {
        return vec.zRot(roll * Mth.DEG_TO_RAD);
    }
    /**
     * 将向量（坐标）vec对齐MC世界Pitch仰俯方向
     */
    public static Vec3 xRot(Vec3 vec,float pitch) {
        return vec.xRot(-pitch * Mth.DEG_TO_RAD);
    }
    /**
     * 将向量（坐标）vec对齐MC世界Yaw航偏方向
     */
    public static Vec3 rotateY(Vec3 vec,float yaw) {
        return vec.yRot(-yaw * Mth.DEG_TO_RAD);
    }
    public static Vec3 rotateY(double dx,double dy,double dz,float yaw) {
        return new Vec3(dx,dy,dz).yRot(-yaw * Mth.DEG_TO_RAD);
    }
    public static Vec3 rotateYX(Vec3 pos,float yaw,float pitch){
        return rotateYX((float) pos.x, (float) pos.y, (float) pos.z,yaw,pitch);
    }
    public static Vec3 rotateYX(double x, double y, double z, float yaw, float pitch) {
        return rotateYX((float) x, (float) y, (float) z,yaw,pitch);
    }
    /**
     * 局部旋转顺序：先绕世界 Y 轴转 yaw，再绕局部 X 轴转 pitch。
     * KEY 等价于固定轴复合矩阵 R = Ry(yaw) * Rx(pitch)。 即先绕世界X旋转，再绕世界Y旋转，该旋转始终是针对初始世界旋转轴的
     * @param x,y,z 相对坐标
     * @param yaw,pitch 航偏,仰俯
     * @return 世界向量
     */
    public static Vec3 rotateYX(float x, float y, float z, float yaw, float pitch) {
        float pitchRad = pitch * Mth.DEG_TO_RAD;      // 原版未取反
        float yawRad   = -yaw * Mth.DEG_TO_RAD;       // 原版对 yaw 取反
        float cosPitch = Mth.cos(pitchRad);
        float sinPitch = Mth.sin(pitchRad);
        float cosYaw   = Mth.cos(yawRad);
        float sinYaw   = Mth.sin(yawRad);

        // 先绕 X 轴旋转 pitch
        float y1 = y * cosPitch - z * sinPitch;
        float z1 = y * sinPitch + z * cosPitch;
        // x 不变

        // 再绕 Y 轴旋转 yawRad
        float xOut = x * cosYaw + z1 * sinYaw;
        float zOut = z1 * cosYaw - x * sinYaw;

        return new Vec3(xOut, y1, zOut);
    }

    public static Vec3 rotateYXZ(Vec3 pos,float yaw,float pitch,float roll){
        return rotateYXZ((float) pos.x, (float) pos.y, (float) pos.z,yaw,pitch,roll);
    }

    /**
     * 局部旋转顺序：先绕世界 Y 轴转 yaw，再绕局部 X 轴转 pitch，最后绕局部 Z 轴转 roll。
     * 等价于固定轴复合矩阵 R = Ry(yaw) * Rx(pitch) * Rz(roll)。
     * KEY 等价于固定轴复合矩阵 R = Ry(yaw) * Rx(pitch) * Rz(roll)。 即先绕世界Z旋转，再绕世界X旋转，再绕世界Y旋转，该旋转始终是针对初始世界旋转轴的
     * @param x,y,z 相对坐标
     * @param yaw 航偏角（度）
     * @param pitch 仰俯角（度）
     * @param roll 翻滚角（度）
     * @return 世界坐标向量
     */
    public static Vec3 rotateYXZ(float x, float y, float z, float yaw, float pitch, float roll) {
        float yawRad   = -yaw * Mth.DEG_TO_RAD;      // 取反适配 MC
        float pitchRad =  pitch * Mth.DEG_TO_RAD;
        float rollRad  =  roll * Mth.DEG_TO_RAD;

        double cy = Math.cos(yawRad);
        double sy = Math.sin(yawRad);
        double cp = Math.cos(pitchRad);
        double sp = Math.sin(pitchRad);
        double cr = Math.cos(rollRad);
        double sr = Math.sin(rollRad);

        // 矩阵 R = Ry * Rx * Rz
        double m00 =  cy * cr + sy * sp * sr;
        double m01 = -cy * sr + sy * sp * cr;
        double m02 =  sy * cp;
        double m10 =  cp * sr;
        double m11 =  cp * cr;
        double m12 = -sp;
        double m20 = -sy * cr + cy * sp * sr;
        double m21 =  sy * sr + cy * sp * cr;
        double m22 =  cy * cp;

        double xOut = m00 * x + m01 * y + m02 * z;
        double yOut = m10 * x + m11 * y + m12 * z;
        double zOut = m20 * x + m21 * y + m22 * z;
        return new Vec3(xOut, yOut, zOut);
    }



    public static void buildMatrix3f(float yaw, float pitch,float[] out) {
        float yawRad = -yaw * Mth.DEG_TO_RAD;
        float pitchRad = pitch * Mth.DEG_TO_RAD;
        float cosY = Mth.cos(yawRad), sinY = Mth.sin(yawRad);
        float cosP = Mth.cos(pitchRad), sinP = Mth.sin(pitchRad);
        out[0] = cosP * sinY; out[1] = -sinP; out[2] = cosP * cosY;   // dir
        out[3] = cosY;        out[4] = 0;     out[5] = -sinY;          // right
        out[6] = sinP * sinY; out[7] = cosP;  out[8] = sinP * cosY;    // up
    }
    /**
     * 根据欧拉角 (yaw, pitch, roll) 计算世界空间的三个轴向量。
     * 旋转顺序：先绕世界 Y 轴转 yaw，再绕局部 X 轴转 pitch，最后绕局部 Z 轴转 roll。
     * 结果写入 out 数组，长度为 9：依次为 (dx,dy,dz, rx,ry,rz, ux,uy,uz)
     */
    public static void buildMatrix3f(float yaw, float pitch, float roll, float[] out) {
        float yawRad   = -yaw * Mth.DEG_TO_RAD;   // MC 的 yaw 取反
        float pitchRad =  pitch * Mth.DEG_TO_RAD;
        float rollRad  =  roll * Mth.DEG_TO_RAD;

        float cy = Mth.cos(yawRad), sy = Mth.sin(yawRad);
        float cp = Mth.cos(pitchRad), sp = Mth.sin(pitchRad);
        float cr = Mth.cos(rollRad), sr = Mth.sin(rollRad);

        // 旋转矩阵 R = Ry(yaw) * Rx(pitch) * Rz(roll)
        // 矩阵的列向量分别为：Z 轴方向（dir）、X 轴方向（right）、Y 轴方向（up）
        // 第 0 列（局部 X → 世界 right）
        out[3] =  cy * cr + sy * sp * sr;   // rx
        out[4] =  cp * sr;                  // ry
        out[5] = -sy * cr + cy * sp * sr;   // rz

        // 第 1 列（局部 Y → 世界 up）
        out[6] = -cy * sr + sy * sp * cr;   // ux
        out[7] =  cp * cr;                  // uy
        out[8] =  sy * sr + cy * sp * cr;   // uz

        // 第 2 列（局部 Z → 世界 dir）
        out[0] =  sy * cp;                  // dx
        out[1] = -sp;                       // dy
        out[2] =  cy * cp;                  // dz
    }


    public static Vec3 rotate(float x, float y, float z, float[] matrix3f) {
        // matrix3f 长度至少为 9，布局：[dirX, dirY, dirZ, rightX, rightY, rightZ, upX, upY, upZ]
        double wx = x * matrix3f[3] + y * matrix3f[6] + z * matrix3f[0];
        double wy = x * matrix3f[4] + y * matrix3f[7] + z * matrix3f[1];
        double wz = x * matrix3f[5] + y * matrix3f[8] + z * matrix3f[2];
        return new Vec3(wx, wy, wz);
    }
    public static Vec3 rotate(Vec3 pos, float[] matrix3f) {
        // matrix3f 长度至少为 9，布局：[dirX, dirY, dirZ, rightX, rightY, rightZ, upX, upY, upZ]
        double wx = pos.x * matrix3f[3] + pos.y * matrix3f[6] + pos.z * matrix3f[0];
        double wy = pos.x * matrix3f[4] + pos.y * matrix3f[7] + pos.z * matrix3f[1];
        double wz = pos.x * matrix3f[5] + pos.y * matrix3f[8] + pos.z * matrix3f[2];
        return new Vec3(wx, wy, wz);
    }

    /**
     * 使用指定的四元数进行旋转
     */
    public static Vec3 rotate(float x,float y, float z, Quaternionf quaternionf) {
        // 使用四元数计算眼睛位置
        Vector3f upLocal = new Vector3f(x, y, z).rotate(quaternionf);
        return new Vec3(upLocal.x, upLocal.y, upLocal.z);
    }

    /**
     * 返回从from向量旋转到to向量的四元数，常用于渲染矩阵旋转
     */
    public static Quaternionf rotation(Vec3 from, Vec3 to) {
        return new Quaternionf().fromAxisAngleRad(from.cross(to).toVector3f(), (float) Math.acos(from.dot(to)));
    }

    /**
     * 将一个向量绕指定轴，旋转一定角度
     * @param vec 向量
     * @param axis 轴
     * @param angleRad 角度
     */
    public static Vec3 rotate(Vec3 vec, Vec3 axis, double angleRad) {
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double dot = vec.dot(axis);
        // 平行分量
        Vec3 parallel = axis.scale(dot);
        // 垂直分量
        Vec3 perpendicular = vec.subtract(parallel);
        // 叉积分量（用于旋转）
        Vec3 cross = axis.cross(perpendicular);
        // 旋转后的垂直分量
        Vec3 rotatedPerp = perpendicular.scale(cos).add(cross.scale(sin));
        return parallel.add(rotatedPerp);
    }

}
