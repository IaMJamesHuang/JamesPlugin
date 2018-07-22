package com.james.jamesplugin;

import android.content.res.AssetManager;
import android.content.res.Resources;

import dalvik.system.DexClassLoader;

/**
 * Created by James on 2018/7/22.
 */
public class PluginInfo {

    private DexClassLoader mClassLoader;

    private AssetManager mAssetManager;

    private Resources mResources;

    public PluginInfo(DexClassLoader mClassLoader, AssetManager mAssetManager, Resources mResources) {
        this.mClassLoader = mClassLoader;
        this.mAssetManager = mAssetManager;
        this.mResources = mResources;
    }
}
