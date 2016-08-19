package com.bbpro.app.cache;


import com.bbpro.app.BBProApp;
import com.bbpro.app.cache.ImageFetcher;

import android.content.Context;

public class DefaultLoader extends ImageFetcher {

    public DefaultLoader(Context context) {
        this(context, 0);
    }

    public DefaultLoader(Context context, int imageSize) {
        this(context, imageSize, imageSize);
    }

    public DefaultLoader(Context context, int imageWidth, int imageHeight) {
        super(context, imageWidth, imageHeight);
        init();
    }

    private void init() {
        setImageCache(BBProApp.getInstance().getImageCache());
    }

}
