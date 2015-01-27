package com.outlook.siribby.bamsgrave;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GraveEventHandler {
    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if ((event.entityLiving instanceof EntityPlayerMP)) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
            boolean digGrave = false;
            boolean dontBreak = true;
            while ((!digGrave) && (dontBreak)) {
                if (!player.capabilities.isCreativeMode) {
                    int chestsNeeded = BaMsGRAVE.chestsNeededToMakeGrave;
                    if (chestsNeeded > 0) {
                        int numberOfChests = 0;
                        ItemStack[] inventory = player.inventory.mainInventory;
                        for (int i = 0; i < inventory.length; i++) {
                            ItemStack itemstack = inventory[i];
                            if ((itemstack != null) && (Block.getBlockFromItem(itemstack.getItem()) == Blocks.chest)) {
                                numberOfChests += itemstack.stackSize;
                            }
                        }
                        if (numberOfChests >= chestsNeeded) {
                            for (int i = 0; (chestsNeeded > 0) && (i < inventory.length); i++) {
                                ItemStack itemstack = inventory[i];
                                if ((itemstack != null) && (Block.getBlockFromItem(itemstack.getItem()) == Blocks.chest)) {
                                    while ((itemstack.stackSize > 0) && (chestsNeeded > 0)) {
                                        itemstack.stackSize -= 1;
                                        chestsNeeded--;
                                    }
                                    if (itemstack.stackSize <= 0) {
                                        player.inventory.setInventorySlotContents(i, null);
                                    }
                                }
                            }
                            digGrave = true;
                        }
                        dontBreak = false;
                    } else {
                        digGrave = true;
                    }
                } else if (BaMsGRAVE.gravesForCreative) {
                    digGrave = true;
                } else {
                    dontBreak = false;
                }
            }
            if (digGrave) {
                GraveDigger.dig((EntityPlayerMP) event.entityLiving);
            }
        }
    }
}
