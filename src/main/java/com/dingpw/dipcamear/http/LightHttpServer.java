package com.dingpw.dipcamear.http;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import org.apache.http.protocol.HttpRequestHandler;

import java.lang.reflect.Constructor;
import java.util.Date;

/**
 * Created by dingpw on 6/19/14.
 */
public class LightHttpServer extends Service{

    private DHttpRequestHandlerRegistry dHttpRequestHandlerRegistry = null;
    private String[] MODULES = new String[] {"ModAssetServer"};
    public Date date = null;
    public Context context = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this.getApplicationContext();
        this.dHttpRequestHandlerRegistry = new DHttpRequestHandlerRegistry();
        try {
            String packageName = this.context.getPackageName();
            date = new Date(this.context.getPackageManager().getPackageInfo(packageName, 0).lastUpdateTime);
        } catch (PackageManager.NameNotFoundException e) {
            date = new Date(0);
        }

//        for (int i=0; i<MODULES.length; i++) {
//            try {
//                Class<?> pluginClass = Class.forName(LightHttpServer.class.getPackage().getName()+"."+MODULES[i]);
//                Constructor<?> pluginConstructor = pluginClass.getConstructor(new Class[]{LightHttpServer.class});
//                addRequestHandler((String) pluginClass.getField("PATTERN").get(null), (HttpRequestHandler)pluginConstructor.newInstance(this));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


        addRequestHandler("*",new ModAssetServer(this));

        new HttpRequestListener(1234);
    }

    protected void addRequestHandler(String pattern, HttpRequestHandler handler) {
        this.dHttpRequestHandlerRegistry.register(pattern, handler);
    }
}
