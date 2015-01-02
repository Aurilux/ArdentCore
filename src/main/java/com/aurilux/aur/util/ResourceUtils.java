package com.aurilux.aur.util;

public class ResourceUtils {
    public static final String BLOCKS = "textures/blocks/";
    public static final String ENTITY = "textures/entity/";
    public static final String ENVIRON = "textures/environment/";
    public static final String GUI = "textures/gui/";
    public static final String ITEMS = "textures/items/";
    public static final String MODEL = "models/";
    public static final String ARMOR = MODEL + "armor/";

    /**
     * ResourceLocation does not search the mod's resource directory for resources like the IIconRegister does.
     * So you must ALWAYS provide a near-absolute path (starting with 'textures' is best) for Minecraft to find the
     * resource correctly.
     * @param path the resource's path
     * @return the ResourceLocation
     *
    public static ResourceLocation getResource(String path) {
        return new ResourceLocation(XARModInfo.MOD_ID + ":" + path);
    }

    /**
     * Unlike 'getResource', this method just returns a string with the mod id and resource name. This should only be
     * used with IIconRegister
     * @param name the name of resource file
     * @return the mod resource string in the format MOD_ID:FILE_NAME
     *
    public static String getTexturePath(String name) {
        return XARModInfo.MOD_ID + ":" + name;
    }

    /**
     * @param reg the icon register
     * @param name the texture name
     * @return the icon
     *
    public static IIcon getIcon(IIconRegister reg, String name) { return reg.registerIcon(getTexturePath(name)); }*/
}