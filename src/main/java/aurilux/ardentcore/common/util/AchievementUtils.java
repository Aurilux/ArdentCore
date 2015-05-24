package aurilux.ardentcore.common.util;

import aurilux.ardentcore.common.stats.ProgressAchievement;
import aurilux.ardentcore.common.stats.SequentialAchievement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;

/**
 * This class was created by <Aurilux>. It's distributed as part of the Xth'uoth Mod.
 * <p/>
 * Xth'uoth is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [13 May 2015]
 */
public class AchievementUtils {
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
        return player instanceof EntityPlayerMP
                && ((EntityPlayerMP) player).func_147099_x().hasAchievementUnlocked(achievement);
    }
}
