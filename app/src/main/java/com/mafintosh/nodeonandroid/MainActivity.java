package com.mafintosh.nodeonandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private WebView browser;
    private NodeReceiver receiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(MainActivity.this, NodeService.class);
        stopService(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new NodeReceiver();
        IntentFilter filter = new IntentFilter("com.mafintosh.nodeonandroid.ipc");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.getSettings().setUseWideViewPort(true);
        browser.getSettings().setJavaScriptEnabled(true);

        final Context me = this;

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ServerSocket server = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));

                            Intent i = new Intent(MainActivity.this, NodeService.class);
                            i.putExtra("ipc-port", "" + server.getLocalPort());
                            startService(i);

                            Socket socket = server.accept();
                            BufferedInputStream inp = new BufferedInputStream(socket.getInputStream());
                            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

                            byte[] buf = new byte[65536];

                            while (true) {
                                int read = inp.read(buf);
                                String u = new String(Arrays.copyOfRange(buf, 0, read));
                                Intent in = new Intent("com.mafintosh.nodeonandroid.ipc");
                                in.putExtra("loadUrl", u);
                                LocalBroadcastManager.getInstance(me).sendBroadcast(in);
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
        ).start();

    }

    private class NodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String loadUrl = intent.getStringExtra("loadUrl");
            if (loadUrl != null) browser.loadUrl(loadUrl);
        }
    }
}
