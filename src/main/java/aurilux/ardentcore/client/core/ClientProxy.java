package aurilux.ardentcore.client.core;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import aurilux.ardentcore.common.core.CommonProxy;
import aurilux.ardentcore.common.init.ModVersionChecker;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * This class handles all of the client exclusive initialization.
 * This includes renderers, tile entities, client-side event handlers, and key bindings.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        FMLCommonHandler.instance().bus().register(new ModVersionChecker());
    }
}