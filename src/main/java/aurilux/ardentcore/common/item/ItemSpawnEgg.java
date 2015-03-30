package aurilux.ardentcore.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [04 Jan 2015]
 */

/**
 * My custom egg item used to spawn mobs, though most of it is identical to vanilla's ItemMonsterPlacer. This allows me
 * more control so I can add this item to my mod's creative tab and makes it much easier to add mob eggs.
 * <p/>
 * The strongest disadvantages to this approach is that it won't track how many times players have killed, and have been
 * killed by, your mob.
 */
public class ItemSpawnEgg extends Item {
    //Stores the entities that will have eggs made for them
    private ArrayList<EggInfo> entityList = new ArrayList<EggInfo>();

    @SideOnly(Side.CLIENT)
    private IIcon theIcon;

    public ItemSpawnEgg() {
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        super.registerIcons(reg);
        this.theIcon = reg.registerIcon(this.getIconString() + "_overlay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass > 0 ? this.theIcon : super.getIconFromDamageForRenderPass(damage, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < entityList.size(); i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getIconString() {
        return "spawn_egg";
    }

    public void addEntityEgg(String name, int color1, int color2) {
        entityList.add(new EggInfo(name, color1, color2));
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        String s = ("" + StatCollector.translateToLocal("item.monsterPlacer.name")).trim();
        String s1 = entityList.get(itemStack.getItemDamage()).name;
        if (s1 != null) {
            s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        }

        return s;
    }

    /**
     * @param itemStack the itemstack with this as an item
     * @param pass      the render pass
     * @return
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int pass) {
        EggInfo eggInfo = entityList.get(itemStack.getItemDamage());
        return eggInfo != null ? (pass == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor) : 16777215;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double halfBlockOffset = 0.0D;

            if (side == 1 && block.getRenderType() == 11) {
                halfBlockOffset = 0.5D;
            }

            Entity entity = spawnCreature(world, itemStack.getItemDamage(), (double) x + 0.5D, (double) y + halfBlockOffset, (double) z + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLivingBase && itemStack.hasDisplayName()) {
                    ((EntityLiving) entity).setCustomNameTag(itemStack.getDisplayName());
                }

                if (!player.capabilities.isCreativeMode) {
                    --itemStack.stackSize;
                }
            }
            return true;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return itemStack;
        } else {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
            if (movingobjectposition == null) {
                return itemStack;
            } else {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!world.canMineBlock(player, i, j, k)) {
                        return itemStack;
                    }

                    if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStack)) {
                        return itemStack;
                    }

                    if (world.getBlock(i, j, k) instanceof BlockLiquid) {
                        Entity entity = spawnCreature(world, itemStack.getItemDamage(), (double) i, (double) j, (double) k);

                        if (entity != null) {
                            if (entity instanceof EntityLivingBase && itemStack.hasDisplayName()) {
                                ((EntityLiving) entity).setCustomNameTag(itemStack.getDisplayName());
                            }

                            if (!player.capabilities.isCreativeMode) {
                                --itemStack.stackSize;
                            }
                        }
                    }
                }
                return itemStack;
            }
        }
    }

    public static Entity spawnCreature(World world, int entityId, double x, double y, double z) {
        if (!EntityList.entityEggs.containsKey(Integer.valueOf(entityId))) {
            return null;
        } else {
            Entity entity = null;
            for (int i = 0; i < 1; i++) {
                entity = EntityList.createEntityByID(entityId, world);
                if (entity != null && entity instanceof EntityLivingBase) {
                    EntityLiving entityliving = (EntityLiving) entity;
                    entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    entityliving.onSpawnWithEgg(null);
                    world.spawnEntityInWorld(entity);
                    entityliving.playLivingSound();
                }
            }
            return entity;
        }
    }

    private class EggInfo {
        public String name;
        public int primaryColor;
        public int secondaryColor;

        public EggInfo(String name, int color1, int color2) {
            this.name = name;
            this.primaryColor = color1;
            this.secondaryColor = color2;
        }
    }
}