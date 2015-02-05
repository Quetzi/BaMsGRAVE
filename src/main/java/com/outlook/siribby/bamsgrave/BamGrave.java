package com.outlook.siribby.bamsgrave;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BamGrave.MOD_ID, name = "BaM's Grave", version = BamGrave.VERSION)
public class BamGrave {
    public static final String MOD_ID = "BaMsGRAVE";
    public static final String VERSION = "@VERSION@";
    public static boolean gravesForCreative, storeXp;
    public static int chestsNeededToMakeGrave;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        gravesForCreative = config.get("general", "GravesForCreativeMode", false, "Do players in creative mode make graves?").getBoolean(false);
        chestsNeededToMakeGrave = config.get("general", "ChestsNeededToMakeGrave", 2, "How many chests does a player need to make a grave?").getInt(2);
        storeXp = config.get("general", "StoreXP", true, "Is XP stored in the player's grave?").getBoolean(false);
        config.save();

        MinecraftForge.EVENT_BUS.register(new GraveEventHandler());
    }
}
