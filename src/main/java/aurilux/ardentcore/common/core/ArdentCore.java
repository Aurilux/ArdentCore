package aurilux.ardentcore.common.core;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import aurilux.ardentcore.common.init.ModVersionChecker;
import aurilux.ardentcore.common.mod.AssetWrapper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ArdentCore.MOD_ID, name = "Ardent Core", version = ArdentCore.MOD_VERSION,
        dependencies = "required-after:Forge@[10.13.2.1230,)")
public class ArdentCore {
    public static final String MOD_ID = "ArdentCore";
    public static final String MOD_VERSION = "1.0.0";
    public static final String versionUrl = "https://raw.githubusercontent.com/Aurilux/ArdentCore/master/version.xml";

    /**
     * Your mod's asset directory should follow the convention of being in all lowercase letters
     */
    public static final AssetWrapper assets = new AssetWrapper(MOD_ID.toLowerCase(), null);
    /**
     * It is encouraged to use FMLPreInitializationEvent's getModLog to get your logger. However, when reading output it
     * is easier to find when the logger title is in all caps; getModLog uses your mod id which may not be in all caps.
     */
    public static final Logger logger = LogManager.getLogger(MOD_ID.toUpperCase());
    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MOD_ID);

    public static final boolean devEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Mod.Instance(MOD_ID)
    public static ArdentCore instance;

    @SidedProxy(
            clientSide = "aurilux.ardentcore.client.core.ClientProxy",
            serverSide = "aurilux.ardentcore.common.core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModVersionChecker.registerModToUpdate("ArdentCore", MOD_VERSION, EnumChatFormatting.GOLD, versionUrl);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}