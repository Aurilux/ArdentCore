package aurilux.ardentcore.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [13 Jan 2015]
 */
public class RenderUtils {
    public static void bindTexture(ResourceLocation resource) {
        RenderUtils.bindTexture(Minecraft.getMinecraft(), resource);
    }

    public static void bindTexture(Minecraft mc, ResourceLocation resource) {
        mc.getTextureManager().bindTexture(resource);
    }
}