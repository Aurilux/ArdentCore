package aurilux.ardentcore.common.util;

import aurilux.ardentcore.common.stats.ProgressAchievement;
import aurilux.ardentcore.common.stats.SequentialAchievement;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [13 Jan 2015]
 */

/**
 * A helper class to assist with achievements.
 */
public class AchievementUtils {
    /**
     * A helper to create an achievement object. Will automatically return a ProgressAchievement if the correct
     * parameters are sent.
     * @param id      id of the achievment
     * @param col     the display column
     * @param row     the display row
     * @param iconObj the object from which the achievement will get its icon
     * @param parent  the parent achievement, if any
     * @param max     the maximum progress, if any (set to 0 if not a progress achievement)
     * @return the created AchievementMod object
     */
    public static Achievement createAchievement(String id, int col, int row, Object iconObj, Achievement parent, int max) {
        ItemStack itemStack;
        if (iconObj instanceof Item) {
            itemStack = new ItemStack((Item) iconObj);
        } else if (iconObj instanceof Block) {
            itemStack = new ItemStack((Block) iconObj);
        } else if (iconObj instanceof ItemStack) {
            itemStack = (ItemStack) iconObj;
        } else { //if the iconObj is anything else but an Item, Block, or ItemStack something has gone wrong
            return null;
        }

        if (max == 0) {
            return new Achievement(id, id, col, row, itemStack, parent).registerStat();
        }
        else {
            return new ProgressAchievement(id, col, row, itemStack, parent, max).registerStat();
        }
    }

    public static Achievement createAchievement(String id, int col, int row, Object iconObj, Achievement parent) {
        return createAchievement(id, col, row, iconObj, parent, 0);
    }

    public static Achievement createAchievement(String id, int col, int row, Object iconObj, int max) {
        return createAchievement(id, col, row, iconObj, null, max);
    }

    public static Achievement createAchievement(String id, int col, int row, Object iconObj) {
        return createAchievement(id, col, row, iconObj, null, 0);
    }

    /**
     * A helper to create a mod's achievement page.
     * @param modId        ID of the mod
     * @param achievements list of all the mod's achievements
     */
    public static void createAchievementPage(String modId, Achievement... achievements) {
        AchievementPage page = new AchievementPage(modId, achievements);
        AchievementPage.registerAchievementPage(page);
    }

    /**
     * Completes the achievement or increments it if it is a progress achievement.
     * @param player      the player that just completed the achievement
     * @param achievement the achievement to complete
     */
    public static void completeAchievement(EntityPlayer player, Achievement achievement) {
        if (achievement instanceof ProgressAchievement) {
            NBTTagCompound persistentTag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            ProgressAchievement a = (ProgressAchievement) achievement;

            //increment the progress count then test to see if we've met the objective
            int achievementProgress = persistentTag.getInteger(a.statId) + 1;
            if (achievementProgress == a.getProgressMax()) {
                player.addStat(achievement, 1);
            }
            else {
                //set the new value of the progress
                persistentTag.setInteger(a.statId, achievementProgress);
            }
            //save the changes to the persistent entity tag
            player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
        }
        else if (achievement instanceof SequentialAchievement) {
            NBTTagCompound persistentTag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            SequentialAchievement a = (SequentialAchievement) achievement;

            //increment the progress count then test to see if we've met the objective
            int achievementProgress = persistentTag.getInteger(a.statId) + 1;
            ProgressAchievement nextInSequence = a.getNextInSequence(player);
            if (nextInSequence != null && achievementProgress == nextInSequence.getProgressMax()) {
                player.addStat(nextInSequence, 1);
            }
            else {
                //set the new value of the progress
                persistentTag.setInteger(a.statId, achievementProgress);
            }
            //save the changes to the persistent entity tag
            player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
        }
        else {
            player.addStat(achievement, 1);
        }
    }

    public static boolean isAchievementUnlocked(EntityPlayer player, Achievement achievement) {
        return player instanceof EntityPlayerMP && ((EntityPlayerMP) player).func_147099_x().hasAchievementUnlocked(achievement);
    }
}