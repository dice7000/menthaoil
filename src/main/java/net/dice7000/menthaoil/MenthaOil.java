package net.dice7000.menthaoil;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MenthaOil.MOD_ID)
public class MenthaOil {
    public static final String MOD_ID = "menthaoil";

    public MenthaOil(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MORegistry.confirm(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
