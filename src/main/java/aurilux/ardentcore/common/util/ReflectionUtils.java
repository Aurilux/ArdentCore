package aurilux.ardentcore.common.util;

/**
 * This class was created by <Aurilux>. It's distributed as part of the ArdentCore Mod.
 * <p/>
 * ArdentCore is Open Source and distributed under the GNU Lesser General Public License v3.0
 * (https://www.gnu.org/licenses/lgpl.html)
 * <p/>
 * File Created @ [01 Jan 2015]
 */

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A helper class for interacting with otherwise inaccessible parts of the code
 */
public class ReflectionUtils {
    /**
     * Returns the first field found with the specified field names.
     * <p/>
     * This is notably different from cpw's ReflectionHelper in that it allows you to change final values.
     *
     * @param clazz      the class to find the field (variable) in
     * @param fieldNames the names this field would have, either obfuscated or deobfuscated
     * @return the field if one matching the names were found, null otherwise
     */
    public static Field getField(Class clazz, String... fieldNames) {
        try {
            Field field;
            if (fieldNames.length > 1) {
                field = ReflectionHelper.findField(clazz,
                        ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), fieldNames));
            } else {
                field = clazz.getField(fieldNames[0]);
            }

            field.setAccessible(true);
            Field modfield = Field.class.getDeclaredField("modifiers");
            modfield.setAccessible(true);
            modfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            return field;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets the value of a specific variable
     *
     * @param clazz      the class to find the field (variable) in
     * @param fieldNames the names this field would have, either obfuscated or deobfuscated
     * @param <T>        the desired return type for the variable
     * @return the value, null otherwise
     */
    public static <T> T getProtectedValue(Class clazz, String... fieldNames) {
        try {
            Field field = getField(clazz, fieldNames);
            return (T) field.get(null);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets the value of a specific variable
     *
     * @param clazz       the class to find the field (variable) in
     * @param classObject the object instance to change the value in
     * @param newValue    the new value to assign to the variable
     * @param fieldNames  the names this field would have, either obfuscated or deobfuscated
     */
    public static void setProtectedValue(Class clazz, Object classObject, Object newValue, String... fieldNames) {
        try {
            Field field = getField(clazz, fieldNames);
            field.set(classObject, newValue);
        } catch (Exception ex) {
        }
    }

    /**
     * @param clazz       the class to find the field (variable) in
     * @param classObject the object instance the underlying method is invoked from
     * @param methodName  the name of the method to find
     * @param paramTypes  the object types of the values in {@code args}
     * @param args        the values needed to be sent to the method as parameters
     * @return the return value of the method, null if the method wasn't found
     */
    public static Object invokeMethod(Class clazz, Object classObject, String methodName, Class[] paramTypes, Object... args) {
        try {
            //if no paramTypes are provided, try to generate some of our own
            if (paramTypes == null) {
                paramTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Integer) {
                        paramTypes[i] = int.class;
                    } else {
                        paramTypes[i] = args[i].getClass();
                    }
                }
            }
            Method m = clazz.getDeclaredMethod(methodName, paramTypes);
            m.setAccessible(true);
            return m.invoke(classObject, args);
        } catch (Exception ex) {
            return null;
        }
    }
}
