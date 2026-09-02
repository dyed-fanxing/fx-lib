package com.fanxing.lib.client;

import com.fanxing.lib.net.packet.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 本地客户端注册绑定服务端相关的注册表标识
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class Setup {
    /**
     * 监听客户端实体渲染事件
     * 添加 将服务端注册的标识与客户端具体渲染实现绑定一起
     */
    @SubscribeEvent
    public static void registerRendererHandler(final EntityRenderersEvent.RegisterRenderers event) {
    }

    /**
     * 注册需要属性的实体，即继承自LivingEntity
     */
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
    }


    /**
     * 监听客户端服务端注册网络发包事件
     * @param event 三种注册方式
     */
    @SubscribeEvent
    public static void registerPayloadHandler(final RegisterPayloadHandlersEvent event) {
        // 初始化注册器，设置网络版本为"1"
        final PayloadRegistrar registrar = event.registrar("1");
        // 仅客户端接收的Payload

        registrar.playToClient(AnimPacket.TYPE, AnimPacket.STREAM_CODEC, AnimPacket::handle);


        registrar.playToClient(WarningTipAABBPacket.TYPE, WarningTipAABBPacket.STREAM_CODEC, WarningTipAABBPacket::handle);
        registrar.playToClient(WarningTipPacket.Cylinder.TYPE, WarningTipPacket.Cylinder.STREAM_CODEC, WarningTipPacket.Cylinder::handle);
        registrar.playToClient(WarningTipPacket.Cube.TYPE, WarningTipPacket.Cube.STREAM_CODEC, WarningTipPacket.Cube::handle);
        registrar.playToClient(WarningTipPacket.Quad.TYPE, WarningTipPacket.Quad.STREAM_CODEC, WarningTipPacket.Quad::handle);
        registrar.playToClient(WarningTipPacket.QuadPrecession.TYPE, WarningTipPacket.QuadPrecession.STREAM_CODEC, WarningTipPacket.QuadPrecession::handle);
        registrar.playToClient(WarningTipPacket.QuadCirclePrecession.TYPE, WarningTipPacket.QuadCirclePrecession.STREAM_CODEC, WarningTipPacket.QuadCirclePrecession::handle);
        registrar.playToClient(WarningTipPacket.Circle.TYPE, WarningTipPacket.Circle.STREAM_CODEC, WarningTipPacket.Circle::handle);
        registrar.playToClient(WarningTipPacket.CurveStrip.TYPE, WarningTipPacket.CurveStrip.STREAM_CODEC, WarningTipPacket.CurveStrip::handle);
        registrar.playToClient(WarningTipPacket.RadialPrecessionCurveStripsPacket.TYPE, WarningTipPacket.RadialPrecessionCurveStripsPacket.STREAM_CODEC, WarningTipPacket.RadialPrecessionCurveStripsPacket::handle);
        registrar.playToClient(SyncMotionPayload.TYPE,SyncMotionPayload.STREAM_CODEC, SyncMotionPayload::handle);

        registrar.playToServer(ColorSchemePacket.TYPE,ColorSchemePacket.STREAM_CODEC, ColorSchemePacket::handle);
        registrar.playToServer(ColorPalettesPacket.TYPE,ColorPalettesPacket.STREAM_CODEC, ColorPalettesPacket::handle);
        registrar.playToServer(StopUsingPacket.TYPE, StopUsingPacket.STREAM_CODEC, StopUsingPacket::handle);
    }

    /**
     * 监听客户端菜单屏幕事件
     */
    @SubscribeEvent
    public static void onRegisterMenuScreens(final RegisterMenuScreensEvent event) {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {});
    }
}

