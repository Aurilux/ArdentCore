package aurilux.ardentcore.common.stats;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [20 Mar 2015]
 */

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

/**
 * This achievement unlocks when a player has done a certain objective a certain number of times. This can be anything
 * from collecting a certain item, to slaying a certain mob, crafting a certain item, mining certain blocks, etc.
 */
public class ProgressAchievement extends Achievement {
    /**
     * The maximum amount of progress this achievement needs before it is unlocked.
     */
    private int progressMax = 0;

    public ProgressAchievement(String id, int column, int row, ItemStack itemStack, Achievement parent, int max) {
        super(id, id, column, row, itemStack, parent);
        progressMax = max;
    }

    public int getProgressMax() {
        return progressMax;
    }
}
