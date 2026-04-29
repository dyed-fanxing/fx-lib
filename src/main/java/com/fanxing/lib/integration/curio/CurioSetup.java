package com.fanxing.lib.integration.curio;

import com.fanxing.lib.integration.curio.network.packet.StopUseCurioPacket;
import com.fanxing.lib.integration.curio.network.packet.UseCurioPacket;
import com.fanxing.lib.integration.curio.register.AttachmentTypesFxLibItg;
import com.fanxing.lib.integration.curio.util.CurioUseHelper;
import com.fanxing.lib.integration.curio.util.ICurioUseHelper;
import com.fanxing.lib.integration.curio.util.NoOpCurioUseHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * @author dyed_fanxing
 * @date 2026/4/28 12:15
 */
public class CurioSetup {
    public static ICurioUseHelper HELPER = new NoOpCurioUseHelper();

    /**
     * 监听客户端服务端注册网络发包事件
     *
     * @param event 三种注册方式
     */
    public static void registerPayloadHandler(final RegisterPayloadHandlersEvent event) {
        // 初始化注册器，设置网络版本为"1"
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(UseCurioPacket.TYPE, UseCurioPacket.STREAM_CODEC, UseCurioPacket::handle);
        registrar.playToServer(StopUseCurioPacket.TYPE, StopUseCurioPacket.STREAM_CODEC, StopUseCurioPacket::handle);
    }

    public static void register(IEventBus bus) {
        AttachmentTypesFxLibItg.register(bus);
        bus.addListener(CurioSetup::registerPayloadHandler);
        HELPER = new CurioUseHelper();
    }
}
