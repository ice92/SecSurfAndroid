package com.ftunram.secsurf.toolkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class WinRegistry {
    public static final int HKEY_CURRENT_USER = -2147483647;
    public static final int HKEY_LOCAL_MACHINE = -2147483646;
    private static final int KEY_ALL_ACCESS = 983103;
    private static final int KEY_READ = 131097;
    public static final int REG_ACCESSDENIED = 5;
    public static final int REG_NOTFOUND = 2;
    public static final int REG_SUCCESS = 0;
    private static Method regCloseKey;
    private static Method regCreateKeyEx;
    private static Method regDeleteKey;
    private static Method regDeleteValue;
    private static Method regEnumKeyEx;
    private static Method regEnumValue;
    private static Method regOpenKey;
    private static Method regQueryInfoKey;
    private static Method regQueryValueEx;
    private static Method regSetValueEx;
    private static Preferences systemRoot;
    private static Class<? extends Preferences> userClass;
    private static Preferences userRoot;

    static {
        userRoot = Preferences.userRoot();
        systemRoot = Preferences.systemRoot();
        userClass = userRoot.getClass();
        regOpenKey = null;
        regCloseKey = null;
        regQueryValueEx = null;
        regEnumValue = null;
        regQueryInfoKey = null;
        regEnumKeyEx = null;
        regCreateKeyEx = null;
        regSetValueEx = null;
        regDeleteKey = null;
        regDeleteValue = null;
        try {
            regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[]{Integer.TYPE, byte[].class, Integer.TYPE});
            regOpenKey.setAccessible(true);
            regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[]{Integer.TYPE});
            regCloseKey.setAccessible(true);
            Class[] clsArr = new Class[REG_NOTFOUND];
            clsArr[0] = Integer.TYPE;
            clsArr[1] = byte[].class;
            regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", clsArr);
            regQueryValueEx.setAccessible(true);
            regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            regEnumValue.setAccessible(true);
            regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", new Class[]{Integer.TYPE});
            regQueryInfoKey.setAccessible(true);
            regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
            regEnumKeyEx.setAccessible(true);
            clsArr = new Class[REG_NOTFOUND];
            clsArr[0] = Integer.TYPE;
            clsArr[1] = byte[].class;
            regCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx", clsArr);
            regCreateKeyEx.setAccessible(true);
            regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx", new Class[]{Integer.TYPE, byte[].class, byte[].class});
            regSetValueEx.setAccessible(true);
            clsArr = new Class[REG_NOTFOUND];
            clsArr[0] = Integer.TYPE;
            clsArr[1] = byte[].class;
            regDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue", clsArr);
            regDeleteValue.setAccessible(true);
            clsArr = new Class[REG_NOTFOUND];
            clsArr[0] = Integer.TYPE;
            clsArr[1] = byte[].class;
            regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey", clsArr);
            regDeleteKey.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WinRegistry() {
    }

    public static String readString(int hkey, String key, String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == HKEY_LOCAL_MACHINE) {
            return readString(systemRoot, hkey, key, valueName);
        }
        if (hkey == HKEY_CURRENT_USER) {
            return readString(userRoot, hkey, key, valueName);
        }
        throw new IllegalArgumentException("hkey=" + hkey);
    }

    public static Map<String, String> readStringValues(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == HKEY_LOCAL_MACHINE) {
            return readStringValues(systemRoot, hkey, key);
        }
        if (hkey == HKEY_CURRENT_USER) {
            return readStringValues(userRoot, hkey, key);
        }
        throw new IllegalArgumentException("hkey=" + hkey);
    }

    public static List<String> readStringSubKeys(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == HKEY_LOCAL_MACHINE) {
            return readStringSubKeys(systemRoot, hkey, key);
        }
        if (hkey == HKEY_CURRENT_USER) {
            return readStringSubKeys(userRoot, hkey, key);
        }
        throw new IllegalArgumentException("hkey=" + hkey);
    }

    public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] ret;
        if (hkey == HKEY_LOCAL_MACHINE) {
            ret = createKey(systemRoot, hkey, key);
            regCloseKey.invoke(systemRoot, new Object[]{new Integer(ret[0])});
        } else if (hkey == HKEY_CURRENT_USER) {
            ret = createKey(userRoot, hkey, key);
            regCloseKey.invoke(userRoot, new Object[]{new Integer(ret[0])});
        } else {
            throw new IllegalArgumentException("hkey=" + hkey);
        }
        if (ret[1] != 0) {
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
        }
    }

    public static void writeStringValue(int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == HKEY_LOCAL_MACHINE) {
            writeStringValue(systemRoot, hkey, key, valueName, value);
        } else if (hkey == HKEY_CURRENT_USER) {
            writeStringValue(userRoot, hkey, key, valueName, value);
        } else {
            throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    public static void deleteKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc = -1;
        if (hkey == HKEY_LOCAL_MACHINE) {
            rc = deleteKey(systemRoot, hkey, key);
        } else if (hkey == HKEY_CURRENT_USER) {
            rc = deleteKey(userRoot, hkey, key);
        }
        if (rc != 0) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
        }
    }

    public static void deleteValue(int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc = -1;
        if (hkey == HKEY_LOCAL_MACHINE) {
            rc = deleteValue(systemRoot, hkey, key, value);
        } else if (hkey == HKEY_CURRENT_USER) {
            rc = deleteValue(userRoot, hkey, key, value);
        }
        if (rc != 0) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
        }
    }

    private static int deleteValue(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS)});
        if (handles[1] != 0) {
            return handles[1];
        }
        Method method = regDeleteValue;
        Object[] objArr = new Object[REG_NOTFOUND];
        objArr[0] = new Integer(handles[0]);
        objArr[1] = toCstr(value);
        int rc = ((Integer) method.invoke(root, objArr)).intValue();
        regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
        return rc;
    }

    private static int deleteKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = regDeleteKey;
        Object[] objArr = new Object[REG_NOTFOUND];
        objArr[0] = new Integer(hkey);
        objArr[1] = toCstr(key);
        return ((Integer) method.invoke(root, objArr)).intValue();
    }

    private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{new Integer(hkey), toCstr(key), new Integer(KEY_READ)});
        if (handles[1] != 0) {
            return null;
        }
        Method method = regQueryValueEx;
        Object[] objArr = new Object[REG_NOTFOUND];
        objArr[0] = new Integer(handles[0]);
        objArr[1] = toCstr(value);
        byte[] valb = (byte[]) method.invoke(root, objArr);
        regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
        return valb != null ? new String(valb).trim() : null;
    }

    private static Map<String, String> readStringValues(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        HashMap<String, String> results = new HashMap();
        if (((int[]) regOpenKey.invoke(root, new Object[]{new Integer(hkey), toCstr(key), new Integer(KEY_READ)}))[1] != 0) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[]{new Integer(handles[0])});
        int count = info[0];
        int maxlen = info[3];
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumValue.invoke(root, new Object[]{new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1)});
            results.put(new String(name).trim(), readString(hkey, key, new String(name)));
        }
        regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
        return results;
    }

    private static List<String> readStringSubKeys(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> results = new ArrayList();
        if (((int[]) regOpenKey.invoke(root, new Object[]{new Integer(hkey), toCstr(key), new Integer(KEY_READ)}))[1] != 0) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[]{new Integer(handles[0])});
        int count = info[0];
        int maxlen = info[3];
        for (int index = 0; index < count; index++) {
            results.add(new String((byte[]) regEnumKeyEx.invoke(root, new Object[]{new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1)})).trim());
        }
        regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
        return results;
    }

    private static int[] createKey(Preferences root, int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = regCreateKeyEx;
        Object[] objArr = new Object[REG_NOTFOUND];
        objArr[0] = new Integer(hkey);
        objArr[1] = toCstr(key);
        return (int[]) method.invoke(root, objArr);
    }

    private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS)});
        regSetValueEx.invoke(root, new Object[]{new Integer(handles[0]), toCstr(valueName), toCstr(value)});
        regCloseKey.invoke(root, new Object[]{new Integer(handles[0])});
    }

    private static byte[] toCstr(String str) {
        byte[] result = new byte[(str.length() + 1)];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = (byte) 0;
        return result;
    }
}
