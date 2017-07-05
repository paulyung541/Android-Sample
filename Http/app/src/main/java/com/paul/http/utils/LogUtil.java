package com.paul.http.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2016/12/23.
 * paulyung@outlook.com
 */

public class LogUtil {
    private static boolean _DEBUG = true;

    public static final String TAG0 = "debug_code";
    public static final String TAG1 = "debug_http";
    public static final String TAG2 = "find_class";

    private static String TAG = TAG0;

    public static void setDebug(boolean debug) {
        _DEBUG = debug;
    }

    //设置默认tag
    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (_DEBUG)
            Log.d(tag, msg);
    }

    //格式化打印
    public static void df(String msgFormat, Object... arg) {
        if (_DEBUG)
            Log.d(TAG, String.format(msgFormat, arg));
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (_DEBUG)
            Log.i(tag, msg);
    }

    public static void d(List list) {
        d(TAG, list);
    }

    //只打印 private 成员
    public static void d(String tag, List list) {
        if (_DEBUG) {
            if (list == null) {
                d(tag, "the list is NULL");
                return;
            }

            d(tag, "================================== list start ==================================\n");
            if (list.size() == 0) {
                d(tag, "the list size is 0");
                return;
            }
            d(tag, "list size is " + list.size() + "\n================================================================================\n");
            for (int i = 0; i < list.size(); i++) {
                d(tag, "------------------------------------- the " + i + " ------------------------------------------\n");
                Class clazz = list.get(i).getClass();
                Field[] fields = clazz.getDeclaredFields();
                Object obj = list.get(i);
                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                        d(tag, field.getName() + " = " + getFieldValue(field, obj) + '\n');
                    }
                }
            }
            d(tag, "================================== list end ==================================\n");
        }
    }

    public static void d(Object obj) {
        d(TAG, obj);
    }

    //打印所有private protect public 成员
    public static void d(String tag, Object obj) {
        if (_DEBUG) {
            Class clazz = obj.getClass();
            d(tag, "================================== object start ==================================\n");
            d(tag, "object name is " + clazz.getSimpleName() + "\n================================================================================\n");
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                d(tag, field.getName() + " = " + getFieldValue(field, obj) + '\n');
            }
            d(tag, "================================== object end ==================================\n");
        }
    }

    public static void d(Map map) {
        d(TAG, map);
    }

    public static void d(String tag, Map map) {
        if (_DEBUG) {
            d(tag, "================================== map start ==================================\n");
            Set<Map.Entry> entrySet = map.entrySet();
            for (Map.Entry entry : entrySet) {
                d(tag, entry.getKey().toString() + " = " + entry.getValue() + '\n');
            }
            d(tag, "================================== map end ==================================\n");
        }
    }

    //----------------------------------------------------------------------------------------------

    private static String getFieldValue(Field field, Object obj) {
        String typeName = field.getType().getName();
        String result = "";
        try {
            if (field.get(obj) == null) {
                return "null";
            }
            if (typeName.endsWith("String")) {
                result = field.get(obj).toString();
            } else if (typeName.endsWith("int") || typeName.endsWith("Integer")) {
                result = String.valueOf(field.getInt(obj));
            } else if (typeName.endsWith("boolean") || typeName.endsWith("Boolean")) {
                result = String.valueOf(field.getBoolean(obj));
            } else if (typeName.endsWith("long") || typeName.endsWith("Long")) {
                result = String.valueOf(field.getLong(obj));
            } else if (typeName.endsWith("float") || typeName.endsWith("Float")) {
                result = String.valueOf(field.getFloat(obj));
            } else if (typeName.endsWith("double") || typeName.endsWith("Double")) {
                result = String.valueOf(field.getDouble(obj));
            } else {
                result = field.get(obj).toString();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
