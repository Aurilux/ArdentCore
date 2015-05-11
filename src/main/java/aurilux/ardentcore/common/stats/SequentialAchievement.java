package aurilux.ardentcore.common.stats;

import aurilux.ardentcore.common.util.AchievementUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [20 Mar 2015]
 */

/**
 * This achievement counts a common objective for sequential progress achievements. For example achievements that unlock
 * when the player has slain 10 Zombies, then 25, then 50, then 75, and so on. DO NOT add this to your list of
 * achievements for your achievement page as it will not work properly.
 *
 * The only reason this class extends Achievement (aside from the similarity) is so the completeAchievement function
 * in AchievementUtils can work with it.
 */
public class SequentialAchievement extends Achievement {
    private Achievement[] achievements;

    /**
     * @param id
     * @param achievements
     */
    public SequentialAchievement(String id, Achievement... achievements) {
        super(id, id, 0, 0, new ItemStack(Blocks.web), null);
        this.achievements = achievements;
    }

    /**
     * @param player player to test
     * @return the found ProgressAchievement, null if we've unlocked all ProgressAchievements in this sequence
     */
    public ProgressAchievement getNextInSequence(EntityPlayer player) {
        for (Achievement pa : achievements) {
            if (!AchievementUtils.isAchievementUnlocked(player, pa)) {
                return (ProgressAchievement) pa;
            }
        }
        return null;
    }

    /**
     * @return null because we don't want this processed like a normal achievement
     */
    @Override
    public Achievement registerStat() {
        return null;
    }
}