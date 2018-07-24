package com.james.jamesmall.pluginmanager;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.WeakHashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by James on 2018/7/24.
 */
public class PluginManager {

    private static PluginManager mInstance;

    private Context mContext;
    private File mOptDir;
    private WeakHashMap<String, PluginInfo> mPluginMap;

    private PluginManager(Context context) {
        mContext = context;
        mOptDir = context.getDir("opt", Context.MODE_PRIVATE);
        mPluginMap = new WeakHashMap<>();
    }

    public static PluginManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PluginManager.class) {
                if (mInstance == null) {
                    mInstance = new PluginManager(context);
                }
            }
        }
        return mInstance;
    }

    public PluginInfo loadApk(String apkPath) {
        if (mPluginMap.get(apkPath) != null) {
            return mPluginMap.get(apkPath);
        }
        DexClassLoader classLoader = createPluginClassLoader(apkPath);
        AssetManager assetManager = createPluginAssetManager(apkPath);
        Resources resources = createPluginResources(apkPath);
        PluginInfo pluginInfo = new PluginInfo(classLoader, assetManager, resources);
        mPluginMap.put(apkPath, pluginInfo);
        return pluginInfo;
    }

    private DexClassLoader createPluginClassLoader(String apkPath) {
        return new DexClassLoader(apkPath, mOptDir.getAbsolutePath(),
                null, null);
    }

    private AssetManager createPluginAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Resources createPluginResources(String apkPath) {
        AssetManager assetManager = createPluginAssetManager(apkPath);
        Resources superResources = mContext.getResources();
        Resources pluginResources = new Resources(assetManager,
                superResources.getDisplayMetrics(),
                superResources.getConfiguration());
        return pluginResources;
    }

    public void invokeMethod(String apkPath, String className, String methodName,
                             List<Object> parameter,
                             Class... parameterTypes) {
        PluginInfo pluginInfo = loadApk(apkPath);
        try {
            Class clazz = pluginInfo.getClassLoader().loadClass(className);
            if (clazz != null) {
                Object instance = clazz.newInstance();
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.invoke(instance, parameter.toArray());
            }else {
                throw new ClassNotFoundException("cant find class");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
