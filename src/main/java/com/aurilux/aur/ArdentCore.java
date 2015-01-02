package com.aurilux.aur;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import com.aurilux.aur.network.proxy.ServerProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ArdentCore.MOD_ID, name = "Ardent Core", version = "1.0.0",
    //guiFactory = XARModInfo.GUI_FACTORY,
    dependencies = "required-after:Forge@[10.13.2.1230,)")
public class ArdentCore {
    public static final String MOD_ID = "ArdentCore";

    @Mod.Instance(MOD_ID)
    public static ArdentCore instance;

    @SidedProxy(clientSide = "com.aurilux.aur.network.proxy.ClientProxy", serverSide = "com.aurilux.aur.network.proxy.ServerProxy")
    public static ServerProxy proxy;

    public static final Logger log = LogManager.getLogger("ARDENT_CORE");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        //ModVersionChecker.registerModToUpdate("ArdentCore", "1.0.0", "gold", "");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
    }
}