package com.mafintosh.nodeonandroid;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NodeService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
            String jsPath = getCacheDir().getAbsolutePath() + "/index.js";
            copyAssetFile(getAssets(), "index.js", jsPath);
            startNode("node", jsPath);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private native void startNode(String... app);

    private static void copyAssetFile(AssetManager am, String src, String dest) {
        try {
            File destFile = new File(dest);
            if (!destFile.exists()) destFile.createNewFile();

            InputStream in = am.open(src);
            FileOutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        System.loadLibrary("node");
        System.loadLibrary("native-lib");
    }

}
