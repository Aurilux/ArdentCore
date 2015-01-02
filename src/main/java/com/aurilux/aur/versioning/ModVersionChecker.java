package com.aurilux.aur.versioning;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import com.aurilux.aur.ArdentCore;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.util.ChatComponentText;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Checks all the mods you've registered to see if they have an update. A message will be sent to the player detailing
 * which mod has an update, which version you currently have vs. the latest released, and a link to the thread where
 * you can download the new file.
 */
public class ModVersionChecker {
    /** ArrayList of all the mods you've registered to get an update check. */
    private static final ArrayList<ModVersionInfo> mods = new ArrayList<ModVersionInfo>();

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        String updateMessage = "";
        for (ModVersionInfo info : mods) {
            ArdentCore.log.info("Checking mod " + info.getModName() + " for updates");
            if (isOutdated(info)) {
                if (!updateMessage.equals("")) {
                    updateMessage += "\n";
                }
                updateMessage += info.generateMessage();
                ArdentCore.log.info("Mod " + info.getModName() + " is outdated");
            }
            else {
                ArdentCore.log.info("Mod " + info.getModName() + " is up-to-date");
            }
        }

        //Only send the message if there are any mods that need updating
        if (!updateMessage.equals("")) {
            event.player.addChatMessage(new ChatComponentText(updateMessage));
        }
    }

    public boolean isOutdated(ModVersionInfo info) {
        Properties versionProperties = new Properties();
        try {
            //connects to the file we have specified and then reads it
            HttpsURLConnection conn = (HttpsURLConnection) new URL(info.getVersionUrl()).openConnection();
            InputStream versionFile = conn.getInputStream();
            versionProperties.loadFromXML(versionFile);
        }
        catch (Exception e) {
            return false;
        }

        //gets the mod version corresponding to the current Minecraft version along with the thread url
        String currentMCVersion = Loader.instance().getMCVersionString();
        info.setNewVersion(versionProperties.getProperty(currentMCVersion));
        info.setUpdateThread(versionProperties.getProperty("thread"));

        return info.hasNewVersion();
    }

    /**
     * Adds the specified mod to the list of mods that will have their version checked.
     * Though it doesn't really matter in which stage you register a mod (preInit, init, postInit) as it only checks
     * whenever a player logs in, but for the sake of consistency it should be done in the preInit stage.
     * @param modName name of the mod
     * @param modVersion current version of the mod
     * @param nameColor the color the mod name will be in chat
     * @param versionUrl the url of the version xml, typically on github
     */
    public static void registerModToUpdate(String modName, String modVersion, String nameColor, String versionUrl) {
        mods.add(new ModVersionInfo(modName, modVersion, nameColor, versionUrl));
    }
}