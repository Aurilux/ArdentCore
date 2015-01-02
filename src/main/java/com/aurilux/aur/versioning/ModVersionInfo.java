package com.aurilux.aur.versioning;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

public class ModVersionInfo {
    private String modName;
    private String modVersion;
    private String nameColor;
    private String versionUrl;

    private String newVersion;
    private String updateThread;
    private String formattingReset = EnumChatFormatting.RESET.toString();

    public ModVersionInfo(String modName, String modVersion, String nameColor, String versionUrl) {
        this.modName = modName;
        this.modVersion = modVersion;
        this.nameColor = EnumChatFormatting.getValueByName(nameColor).toString();
        this.versionUrl = versionUrl;
    }

    public String generateMessage() {
        String message = "";
        message += "[" + nameColor + modName + formattingReset + "] has an new version : " + newVersion +
                " (You have " + modVersion + ")! " + (!StringUtils.isNullOrEmpty(updateThread) ? updateThread : "");
        return message;
    }

    public boolean hasNewVersion() {
        if (StringUtils.isNullOrEmpty(newVersion)) return false;

        ArtifactVersion current = new DefaultArtifactVersion(modVersion);
        ArtifactVersion latest = new DefaultArtifactVersion(newVersion);

        return latest.compareTo(current) > 0;
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

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public void setUpdateThread(String updateThread) {
        this.updateThread = updateThread;
    }
}