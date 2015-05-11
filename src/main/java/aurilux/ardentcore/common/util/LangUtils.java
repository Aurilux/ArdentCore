package aurilux.ardentcore.common.util;

import net.minecraft.util.StatCollector;

/**
 * This class was created by <Aurilux>. It's distributed as part of the Titles Mod.
 * <p/>
 * Titles is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [08 May 2015]
 */
public class LangUtils {
    /**
     * Helper method to cut down a bit on space and typing.
     * @param toTranslate the key to translate
     * @return the localized string
     */
    public static String translate(String toTranslate) {
        return StatCollector.translateToLocal(toTranslate);
    }

    /**
     * Helper method to cut down a bit on space and typing.
     * @param toTranslate the key to translate
     * @param args the String.format arguments
     * @return the localized and formatted string
     */
    public static String translateFormatted(String toTranslate, Object... args) {
        return StatCollector.translateToLocalFormatted(toTranslate, args);
    }
}