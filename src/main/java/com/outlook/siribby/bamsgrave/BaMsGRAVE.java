package com.outlook.siribby.bamsgrave;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BaMsGRAVE.MOD_ID, name = "BaM's Grave", version = BaMsGRAVE.VERSION)
public class BaMsGRAVE {
    public static final String MOD_ID = "BaMsGRAVE";
    public static final String VERSION = "@VERSION@";
    public static boolean gravesForCreative;
    public static int chestsNeededToMakeGrave;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        gravesForCreative = config.get("general", "GravesForCreativeMode", false, "Do players in creative mode make graves?").getBoolean(false);
        chestsNeededToMakeGrave = config.get("general", "ChestsNeededToMakeGrave", 2, "How many chests does a player need to make a grave?").getInt(2);
        MinecraftForge.EVENT_BUS.register(new GraveEventHandler());
        config.save();
    }
}
