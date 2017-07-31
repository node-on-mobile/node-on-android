package com.mafintosh.nodeonandroid;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NodeService extends Service {
    private Thread t;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (t != null) {
            t.stop();
            t = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String ipcPort = intent.getStringExtra("ipc-port");

        if (t == null) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String cache = getCacheDir().getAbsolutePath();
                    String jsPath = cache + "/node";
                    String corePath = cache + "/node_modules";
                    AssetManager am = getAssets();
                    copyAssets(am, "node_modules", corePath);
                    copyAssets(am, "node", jsPath);
                    startNode("node", jsPath, "" + ipcPort);
                }
            });
            t.start();
        }

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private native void startNode(String... app);

    private static void copyAssets (AssetManager am, String src, String dest) {
        try {
            copyAssetFile(am, src, dest);
        } catch (Exception e) {
            try {
                File dir = new File(dest);
                dir.mkdir();
            } catch (Exception e1) {}
            try {
                String[] files = am.list(src);
                for (int i = 0; i < files.length; i++) {
                    copyAssets(am, src + "/" + files[i], dest + "/" + files[i]);
                }
            } catch (Exception e2) {}
        }
    }

    private static void copyAssetFile(AssetManager am, String src, String dest) throws IOException {
        InputStream in = am.open(src);

        File destFile = new File(dest);
        if (!destFile.exists()) destFile.createNewFile();

        FileOutputStream out = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }

    static {
        System.loadLibrary("node");
        System.loadLibrary("native-lib");
    }

}
