package aurilux.ardentcore.common.mod;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

/**
 * This class was created by <Aurilux>. It's distributed as part of the Titles Mod.
 * <p/>
 * Titles is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [24 Mar 2015]
 */
public class AssetWrapper {
    private String domain;

    public AssetWrapper(String modId) {
        domain = modId;
    }

    public IIcon getIcon(IIconRegister reg, String textureName) {
        return reg.registerIcon(domain + ":" + textureName);
    }
    public ResourceLocation getEntityRes(String textureName) {
        return new ResourceLocation(domain, "textures/entity/" + textureName);
    }
    public ResourceLocation getEnviroRes(String textureName) {
        return new ResourceLocation(domain, "textures/environment/" + textureName);
    }
    public ResourceLocation getGuiRes(String textureName) {
        return new ResourceLocation(domain, "textures/gui/" + textureName);
    }
    public ResourceLocation getModelRes(String textureName) {
        return new ResourceLocation(domain, "models/" + textureName);
    }
    public ResourceLocation getArmorRes(String textureName) {
        return new ResourceLocation(domain, "models/armor/" + textureName);
    }
}
