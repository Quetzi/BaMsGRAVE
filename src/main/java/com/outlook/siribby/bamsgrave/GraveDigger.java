package com.outlook.siribby.bamsgrave;

import com.outlook.siribby.bamsgrave.repackage.baubles.api.BaublesApi;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GraveDigger {
    private static final ArrayList<Block> ALLOWED_BLOCKS = Lists.newArrayList(Blocks.air, Blocks.stone, Blocks.grass, Blocks.dirt, Blocks.sapling, Blocks.flowing_water, Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.sand, Blocks.gravel, Blocks.log, Blocks.log2, Blocks.leaves, Blocks.sandstone, Blocks.web, Blocks.tallgrass, Blocks.deadbush, Blocks.yellow_flower, Blocks.red_flower, Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.torch, Blocks.fire, Blocks.farmland, Blocks.standing_sign, Blocks.wall_sign, Blocks.snow_layer, Blocks.ice, Blocks.snow, Blocks.cactus, Blocks.clay, Blocks.reeds, Blocks.pumpkin, Blocks.netherrack, Blocks.soul_sand, Blocks.brown_mushroom_block, Blocks.red_mushroom_block, Blocks.melon_block, Blocks.pumpkin_stem, Blocks.melon_stem, Blocks.vine, Blocks.mycelium, Blocks.waterlily, Blocks.nether_wart, Blocks.end_stone, Blocks.cocoa, Blocks.flower_pot, Blocks.carrots, Blocks.potatoes);

    private static int checkGround(World world, Grave grave) {
        int blockingBlocks = 0;

        if (!ALLOWED_BLOCKS.contains(world.getBlockState(grave.sign).getBlock())) blockingBlocks++;
        if (!ALLOWED_BLOCKS.contains(world.getBlockState(grave.coffin1).getBlock())) blockingBlocks++;
        if (!ALLOWED_BLOCKS.contains(world.getBlockState(grave.coffin2).getBlock())) blockingBlocks++;

        return blockingBlocks;
    }

    private static void convertGraveGround(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        Block coverBlock = null;

        if (block == Blocks.stone) {
            coverBlock = Blocks.cobblestone;
        } else if (block == Blocks.grass) {
            coverBlock = Blocks.dirt;
        } else if (block == Blocks.sandstone) {
            coverBlock = Blocks.sand;
        } else if (block == Blocks.double_stone_slab) {
            coverBlock = Blocks.cobblestone;
        } else if (block == Blocks.stone_slab) {
            coverBlock = Blocks.cobblestone;
        }

        if (coverBlock != null) {
            world.setBlockState(pos, coverBlock.getDefaultState(), 2);
        }
    }

    public static void dig(EntityPlayerMP player) {
        if (player.worldObj == null) {
            return;
        }

        int playerView = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
        Grave grave = new Grave((int) player.posX, (int) player.posY, (int) player.posZ, playerView);

        int blockingBlocks0 = checkGround(player.worldObj, grave);

        if (blockingBlocks0 != 0) {
            int rotationYaw = MathHelper.wrapAngleTo180_float(player.rotationYaw) < 0.0F ? MathHelper.floor_double(MathHelper.wrapAngleTo180_float(player.rotationYaw) + 360.0D) : MathHelper.floor_double(MathHelper.wrapAngleTo180_float(player.rotationYaw));

            Grave tmpGrave90Left = new Grave((int) player.posX, (int) player.posY, (int) player.posZ, playerView - 1 & 0x3);
            Grave tmpGrave90Right = new Grave((int) player.posX, (int) player.posY, (int) player.posZ, playerView + 1 & 0x3);
            Grave tmpGrave180 = new Grave((int) player.posX, (int) player.posY, (int) player.posZ, playerView + 2 & 0x3);

            int blockingBlocks90Left = checkGround(player.worldObj, tmpGrave90Left);
            int blockingBlocks90Right = checkGround(player.worldObj, tmpGrave90Right);
            int blockingBlocks180 = checkGround(player.worldObj, tmpGrave180);

            if (rotationYaw % 90 < 45) {
                if (blockingBlocks90Right == 0) {
                    grave = tmpGrave90Right;
                } else if (blockingBlocks90Left == 0) {
                    grave = tmpGrave90Left;
                } else if (blockingBlocks180 == 0) {
                    grave = tmpGrave180;
                } else if ((blockingBlocks90Right < blockingBlocks0) && (blockingBlocks90Right <= blockingBlocks90Left) && (blockingBlocks90Right <= blockingBlocks180)) {
                    grave = tmpGrave90Right;
                } else if ((blockingBlocks90Left < blockingBlocks0) && (blockingBlocks90Left <= blockingBlocks180)) {
                    grave = tmpGrave90Left;
                } else if (blockingBlocks180 < blockingBlocks0) {
                    grave = tmpGrave180;
                }
            } else if (blockingBlocks90Left == 0) {
                grave = tmpGrave90Left;
            } else if (blockingBlocks90Right == 0) {
                grave = tmpGrave90Right;
            } else if (blockingBlocks180 == 0) {
                grave = tmpGrave180;
            } else if ((blockingBlocks90Left < blockingBlocks0) && (blockingBlocks90Left <= blockingBlocks90Right) && (blockingBlocks90Left <= blockingBlocks180)) {
                grave = tmpGrave90Left;
            } else if ((blockingBlocks90Right < blockingBlocks0) && (blockingBlocks90Right <= blockingBlocks180)) {
                grave = tmpGrave90Right;
            } else if (blockingBlocks180 < blockingBlocks0) {
                grave = tmpGrave180;
            }

        }

        IBlockState state = Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
        player.worldObj.setBlockState(grave.stone, state, 2);
        player.worldObj.setBlockState(grave.plant, Blocks.flower_pot.getDefaultState(), 2);
        player.worldObj.setTileEntity(grave.plant, new TileEntityFlowerPot(Item.getItemFromBlock(Blocks.yellow_flower), 0));
        state = Blocks.wall_sign.getStateFromMeta(grave.direction);
        player.worldObj.setBlockState(grave.sign, state, 2);
        player.worldObj.setBlockState(grave.coffin1, Blocks.chest.getDefaultState(), 2);
        player.worldObj.setBlockState(grave.coffin2, Blocks.chest.getDefaultState(), 2);

        TileEntitySign sign = (TileEntitySign) player.worldObj.getTileEntity(grave.sign);
        if (sign != null) {
            sign.signText[0].appendText(player.getDisplayName().getUnformattedText());
            sign.signText[2].appendText(new SimpleDateFormat("MMM d ''yy").format(Calendar.getInstance().getTime()));
            sign.signText[3].appendText(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
        }

        TileEntityChest coffinA = (TileEntityChest) player.worldObj.getTileEntity(grave.coffin1);
        TileEntityChest coffinB = (TileEntityChest) player.worldObj.getTileEntity(grave.coffin2);

        /* ---- fillCoffin START ---- */
        if (coffinA == null || coffinB == null) {
            return;
        }

        IInventory baublesInv = BaublesApi.getBaubles(player);
        if (baublesInv != null) {
            for (int i = 0; i < baublesInv.getSizeInventory(); i++) {
                coffinB.setInventorySlotContents(i + 13, baublesInv.getStackInSlot(i));

                baublesInv.setInventorySlotContents(i, null);
            }
        }

        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (i < 9) {
                coffinB.setInventorySlotContents(i, player.inventory.mainInventory[i]);
            } else {
                coffinA.setInventorySlotContents(i - 9, player.inventory.mainInventory[i]);
            }

            player.inventory.mainInventory[i] = null;
        }

        for (int i = 0; i < player.inventory.armorInventory.length; i++) {
            coffinB.setInventorySlotContents(i + 9, player.inventory.armorInventory[i]);

            player.inventory.armorInventory[i] = null;
        }

        if (BamGrave.storeXp) {
            // -- getXP START -- //
            int xp = 0;
            int level = player.experienceLevel - 1;

            if ((player.experience < 0.0F) || (player.experience >= 1.0F)) {
                FMLLog.warning("[%s] Incorrect value in %s's XP bar: %f", BamGrave.MOD_ID, player.getDisplayName(), player.experience);
            } else if (player.experienceLevel < 15) {
                xp += (int) (player.experience * 17.0F + 0.5F);
            } else if (player.experienceLevel < 30) {
                xp += (int) (player.experience * (17 + (player.experienceLevel - 15) * 3) + 0.5F);
            } else {
                xp += (int) (player.experience * (62 + (player.experienceLevel - 30) * 7) + 0.5F);
            }

            while (level >= 0) {
                if (level < 15) {
                    xp += 17;
                } else if (level < 30) {
                    xp += 17 + (level - 15) * 3;
                } else {
                    xp += 62 + (level - 30) * 7;
                }

                level--;
            }
            // -- getXP END -- //

            int flasks = xp / 11;
            int slot = 26;
            int slot1 = baublesInv != null ? 16 : 14;

            while (flasks > 0 && slot > slot1) {
                int stack_size = flasks > 64 ? 64 : flasks;

                coffinB.setInventorySlotContents(slot, new ItemStack(Items.experience_bottle, stack_size));

                flasks -= 64;
                slot--;
            }

            player.experienceLevel = 0;
            player.experienceTotal = 0;
            player.experience = 0.0F;
        }
        /* ---- fillCoffin END ---- */

        convertGraveGround(player.worldObj, grave.ground1);
        convertGraveGround(player.worldObj, grave.ground2);
    }

    private static class Grave {
        public final BlockPos stone;
        public final BlockPos plant;
        public final BlockPos sign;
        public final BlockPos coffin1;
        public final BlockPos coffin2;
        public final BlockPos ground1;
        public final BlockPos ground2;
        public final int direction;

        public Grave(int x, int y, int z, int player_view) {
            int myY = y < 3 ? 3 : y;

            switch (player_view) {
                case 1:
                    this.stone = new BlockPos(x, myY, z);
                    this.plant = new BlockPos(x, myY + 1, z);
                    this.sign = new BlockPos(x - 1, myY, z);
                    this.coffin1 = new BlockPos(x - 2, myY - 2, z);
                    this.coffin2 = new BlockPos(x - 1, myY - 2, z);
                    this.ground1 = new BlockPos(x - 1, myY - 1, z);
                    this.ground2 = new BlockPos(x - 2, myY - 1, z);
                    this.direction = 4;
                    break;
                case 2:
                    this.stone = new BlockPos(x, myY, z);
                    this.plant = new BlockPos(x, myY + 1, z);
                    this.sign = new BlockPos(x, myY, z - 1);
                    this.coffin1 = new BlockPos(x, myY - 2, z - 2);
                    this.coffin2 = new BlockPos(x, myY - 2, z - 1);
                    this.ground1 = new BlockPos(x, myY - 1, z - 1);
                    this.ground2 = new BlockPos(x, myY - 1, z - 2);
                    this.direction = 2;
                    break;
                case 3:
                    this.stone = new BlockPos(x, myY, z);
                    this.plant = new BlockPos(x, myY + 1, z);
                    this.sign = new BlockPos(x + 1, myY, z);
                    this.coffin1 = new BlockPos(x + 1, myY - 2, z);
                    this.coffin2 = new BlockPos(x + 2, myY - 2, z);
                    this.ground1 = new BlockPos(x + 1, myY - 1, z);
                    this.ground2 = new BlockPos(x + 2, myY - 1, z);
                    this.direction = 5;
                    break;
                default:
                    this.stone = new BlockPos(x, myY, z);
                    this.plant = new BlockPos(x, myY + 1, z);
                    this.sign = new BlockPos(x, myY, z + 1);
                    this.coffin1 = new BlockPos(x, myY - 2, z + 1);
                    this.coffin2 = new BlockPos(x, myY - 2, z + 2);
                    this.ground1 = new BlockPos(x, myY - 1, z + 1);
                    this.ground2 = new BlockPos(x, myY - 1, z + 2);
                    this.direction = 3;
            }
        }
    }
}
