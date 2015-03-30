package aurilux.ardentcore.common.init;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import aurilux.ardentcore.common.core.ArdentCore;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

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
    /**
     * ArrayList of all the mods you've registered to get an update check.
     */
    private static final ArrayList<ModVersionInfo> registeredMods = new ArrayList<ModVersionInfo>();

    private final String updateThread = "http://goo.gl/J1USql";

    private String reset = EnumChatFormatting.RESET.toString();

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ArrayList<ModVersionInfo> updatedMods = new ArrayList<ModVersionInfo>();
        for (ModVersionInfo info : registeredMods) {
            ArdentCore.logger.info("Checking mod " + info.getModName() + " for updates");
            if (isOutdated(info)) {
                updatedMods.add(info);
                ArdentCore.logger.info("Mod " + info.getModName() + " is outdated");
            } else {
                ArdentCore.logger.info("Mod " + info.getModName() + " is up-to-date");
            }
        }

        //Only send the message if there are any mods that need updating
        if (!updatedMods.isEmpty()) {
            event.player.addChatMessage(generateMessage(updatedMods));
        }
    }

    public IChatComponent generateMessage(ArrayList<ModVersionInfo> updatedMods) {
        String message = "Mod(s) [";
        for (int i = 0; i < updatedMods.size(); i++) {
            ModVersionInfo info = updatedMods.get(i);
            message += info.getNameColor() + info.getModName() + reset + (i < updatedMods.size() - 1 ? ", " : "");
        }
        message += "] have an update: " + updateThread;

        IChatComponent chatMessage = new ChatComponentText(message);
        chatMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, updateThread));
        return chatMessage;
    }

    public boolean isOutdated(ModVersionInfo info) {
        Properties versionProperties = new Properties();
        try {
            //connects to the file we have specified and then reads it
            HttpsURLConnection conn = (HttpsURLConnection) new URL(info.getVersionUrl()).openConnection();
            InputStream versionFile = conn.getInputStream();
            versionProperties.loadFromXML(versionFile);
        } catch (Exception e) {
            return false;
        }

        //gets the mod version corresponding to the current Minecraft version along with the thread url
        String currentMCVersion = Loader.instance().getMCVersionString();
        String newVersion = versionProperties.getProperty(currentMCVersion);


        if (StringUtils.isNullOrEmpty(newVersion)) return false;

        ArtifactVersion current = new DefaultArtifactVersion(info.getModVersion());
        ArtifactVersion latest = new DefaultArtifactVersion(newVersion);

        return latest.compareTo(current) > 0;
    }

    /**
     * Adds the specified mod to the list of mods that will have their version checked.
     * Though it doesn't really matter in which stage you register a mod (preInit, init, postInit) as it only checks
     * whenever a player logs in, but for the sake of consistency it should be done in the preInit stage.
     *
     * @param modName    name of the mod
     * @param modVersion current version of the mod
     * @param nameColor  the color the mod name will be in chat
     * @param versionUrl the url of the version xml, typically on github
     */
    public static void registerModToUpdate(String modName, String modVersion, EnumChatFormatting nameColor, String versionUrl) {
        ArdentCore.logger.info("Registering " + modName + " for update check");
        registeredMods.add(new ModVersionInfo(modName, modVersion, nameColor, versionUrl));
    }

    public static class ModVersionInfo {
        private String modName;
        private String modVersion;
        private EnumChatFormatting nameColor;
        private String versionUrl;

        public ModVersionInfo(String modName, String modVersion, EnumChatFormatting nameColor, String versionUrl) {
            this.modName = modName;
            this.modVersion = modVersion;
            this.nameColor = nameColor;
            this.versionUrl = versionUrl;
        }

        public String getVersionUrl() {
            return versionUrl;
        }

        public String getModName() {
            return modName;
        }

        public String getModVersion() {
            return modVersion;
        }

        public String getNameColor() {
            return nameColor.toString();
        }
    }
}