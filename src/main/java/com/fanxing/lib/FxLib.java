package com.fanxing.lib;

import com.fanxing.lib.client.render.instance.InstanceModelRegistry;
import com.fanxing.lib.integration.IntegrationFx;
import com.fanxing.lib.registry.*;
import com.fanxing.lib.util.phys.motion.PhysicsMotionModel;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// 此处值应与META-INF/neoforge.mods.toml文件中的条目匹配
@Mod(FxLib.MOD_ID)
public class FxLib {
    // 在公共位置定义mod id供所有内容引用
    public static final String MOD_ID = "fx_lib";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 模组类的构造函数是模组加载时运行的第一段代码
    public FxLib(IEventBus modEventBus, ModContainer modContainer) {
        // 注册commonSetup方法用于模组加载
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onClientSetup);


        AttributesFxLib.register(modEventBus);           // 属性注册
        modEventBus.addListener(AttributesFxLib::onEntityAttributeModification);

        MemoryModuleTypesFxLib.register(modEventBus);    // 记忆注册
        NumberProvidersFxLib.register(modEventBus);      // 数字提供者注册
        BlockTypesFxLib.register(modEventBus);           // 方块注册
        ItemTypesFxLib.register(modEventBus);            // 物品注册
        EntityTypesFxLib.register(modEventBus);          // 实体注册
        MobEffectTypesFxLib.registry(modEventBus);       // buff注册
        SoundEventsFxLib.register(modEventBus);          // 声音注册
        ParticleTypesFxLib.register(modEventBus);        // 粒子注册
        MenuTypesFxLib.register(modEventBus);            // 菜单注册
        AttachmentTypesFxLib.register(modEventBus);      // 附件注册
        DataComponentsFxLib.register(modEventBus);       // 数据组件注册
        StructureProcessorTypesFxLib.register(modEventBus); // 结构后处理器注册

        IntegrationFx.register(modEventBus);               // 其他注册

        // 注册当前类以响应游戏事件
        NeoForge.EVENT_BUS.register(this);

        // 注册将物品添加到创造标签页的方法
        modEventBus.addListener(this::addCreative);



        // 注册模组的配置规范，以便FML可以创建和加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigFxLib.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigFxLib.CLIENT_SPEC);
        // SERVER端配置，自动同步
        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigFxLib.SERVER_SPEC);
        // 客户端注册原生配置界面
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
        PhysicsMotionModel.registry();
    }

    // 客户端和服务端 所有静态注册表已经注册完毕，可以安全查询
    private void onCommonSetup(final FMLCommonSetupEvent event) {
    }

    // 将示例方块物品添加到建筑方块标签页
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        }
    }

    // 服务器启动时执行的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.debug("服务器正在启动");
    }


    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            InstanceModelRegistry.uploadAll();
        });
    }


}