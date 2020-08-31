package com.lviiier.totem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;

import npi.sdk.NPrinterLib;
import npi.sdk.common.NRet;
import npi.sdk.data.NInt;

public class MainActivity extends AppCompatActivity {
    boolean ConnessioneAttiva = false;
    //  String paginatotem="https://pediatotem.it/totem/indexh.html";
 /*  String paginatotem = "https://pediatotem.it/totem/index10.html";
   String sottopagina = "totem";*/

     String paginatotem = "https://pediatotem.it/totem/index.html";
     String sottopagina = "totem";

    //  String paginatotem = "https://pediatotem.it/assistente/index.html";
    //  String sottopagina = "assistente";

    private NPrinterLib objLib;
    NInt objnumSize = null;
    int nmsRet = 0;
    String mStrPrinter = "PRT001";

    WebView myWebView;
    ServerSocket serverSocket;
    ServerSocket serverSocket2;

    String foldervideo = "/storage/sdcard0/Download/";
    List<String> FileScaricati = new ArrayList<>();
    File file[];
    String s = "";
    boolean b1 = false;
    boolean b2 = false;
    boolean b3 = false;
    private FrameLayout menu;
    TTSManager ttsManager = null;
    Timer timer;
    // MediaPlayer mp =null;
    String deviceID = "No Id";
    private GoogleApiClient client;
    public static int coont = 1;
    // final MediaPlayer mp = MediaPlayer.create(this, R.raw.aaa);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            //importante anti doppio
            System.exit(2);

        } else {
            CookieSyncManager.createInstance(this);
            CookieSyncManager.getInstance().startSync();
            deviceID = Build.SERIAL;
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie("https://pediatotem.it", "PediaTotem=" + deviceID + "; path=/" + sottopagina);
            CookieSyncManager.getInstance().sync();

            RootTools.debugMode = true; // debug mode
            hideSystemBar();

       /*     Toast bread = Toast.makeText(getApplicationContext(), "ddddd " + coont, Toast.LENGTH_LONG);
            bread.show();*/
            coont += 1;
            myWebView = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = myWebView.getSettings();
            myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.getSettings().setAllowFileAccess(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                webSettings.setMediaPlaybackRequiresUserGesture(false);
            }
            myWebView.setWebViewClient(new MyWebViewClient());//per aggiornare pagina con click
            // myWebView.loadUrl(paginatotem);

      /*      myWebView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
                    Toast bread = Toast.makeText(getApplicationContext(),  "getExtra = "+ hr.getClass(), Toast.LENGTH_LONG);
                    bread.show();
                    return false;
                }
            });
            myWebView.addJavascriptInterface(new Object()
            {
                @JavascriptInterface           // For API 17+
                public void performClick(String strl)
                {

                    Toast.makeText (MainActivity.this, strl, Toast.LENGTH_SHORT).show();

                }
            }, "ok");*/

            myWebView.setWebChromeClient(new WebChromeClient());
            myWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onLoadResource(WebView view, String url) {
                    if (url.indexOf("comando.php") > 0) {
                         Toast.makeText (MainActivity.this, url, Toast.LENGTH_SHORT).show();

                        String NomeFile = url.split("comando.php")[1];
                        //    Toast.makeText (MainActivity.this, "comando" + NomeFile, Toast.LENGTH_SHORT).show();
//                                      s = s.replaceAll("GET /", "");
//                                         s = s.replaceAll("HTTP/1.1", "");
                        s = com.lviiier.totem.UTFEncodingUtil.decodeUTF(NomeFile);
                        codice = s.split("~")[1];
                        lingua = s.split("~")[0];
                        //  Toast.makeText (MainActivity.this, "codice" + codice + ". lingua: " + lingua, Toast.LENGTH_SHORT).show();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //  mp.start();
                                Toast bread = Toast.makeText(getApplicationContext(), codice, Toast.LENGTH_LONG);
                                bread.show();
                                ttsManager.addQueue(codice, lingua);
                            }
                        });
                    }
                    if (url.indexOf("stampa.php") > 0) {
                         Toast.makeText (MainActivity.this, url, Toast.LENGTH_SHORT).show();
                        String NomeFile = url.split("stampa.php")[1];
                        s = com.lviiier.totem.UTFEncodingUtil.decodeUTF(NomeFile);
                        ArayMessaggio = s.split("~");
                        //  Toast.makeText (MainActivity.this, "codice" + codice + ". lingua: " + lingua, Toast.LENGTH_SHORT).show();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                 Stampa(ArayMessaggio);
                            }
                        });
                    }
                }
             /*   @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    if (url.indexOf("comando.php")>0) {
                        Toast.makeText(MainActivity.this, "aaa" + url, Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }*/
            });

            Button buttonIntent1 = (Button) findViewById(R.id.button1);
            assert buttonIntent1 != null;
            buttonIntent1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            b1 = true;
                            b2 = false;
                            b3 = false;
                            break;
                    }
                    return true;
                }
            });

            Button buttonIntent2 = (Button) findViewById(R.id.button2);
            assert buttonIntent2 != null;
            buttonIntent2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (b1 == true) {
                                b1 = true;
                                b2 = true;
                                b3 = false;
                            } else {
                                b1 = false;
                                b2 = false;
                                b3 = false;
                            }

                            break;
                    }
                    return true;
                }
            });

            Button buttonIntent3 = (Button) findViewById(R.id.button3);
            assert buttonIntent3 != null;
            buttonIntent3.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (b2 == true & b1 == true) {
                                b1 = true;
                                b2 = true;
                                b3 = true;
                            } else {
                                b1 = false;
                                b2 = false;
                                b3 = false;
                            }

                            break;
                    }
                    return true;
                }
            });

            Button buttonIntent4 = (Button) findViewById(R.id.button4);
            assert buttonIntent4 != null;
            buttonIntent4.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (b1 == true & b2 == true & b3 == true) {

                                menu.setVisibility(View.VISIBLE);
                            } else {
                                b1 = false;
                                b2 = false;
                                b3 = false;
                            }

                            break;
                    }
                    return true;
                }
            });

            Button buttonannulla = (Button) findViewById(R.id.buttonannulla);
            assert buttonannulla != null;
            buttonannulla.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            menu.setVisibility(View.GONE);
                            menu.setVisibility(View.INVISIBLE);
                            b1 = false;
                            b2 = false;
                            b3 = false;


                            break;
                    }
                    return true;
                }
            });

            Button buttonIntentwifi = (Button) findViewById(R.id.buttonwifi);
            assert buttonIntentwifi != null;
            buttonIntentwifi.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            b1 = false;
                            b2 = false;
                            b3 = false;
                            menu.setVisibility(View.GONE);
                            menu.setVisibility(View.INVISIBLE);
                            //   showSystemBar();
                            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                            // showSystemBar();
                            break;
                    }
                    return true;
                }
            });

            Button buttonIntentqs = (Button) findViewById(R.id.buttonqs);
            assert buttonIntentqs != null;
            buttonIntentqs.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            b1 = false;
                            b2 = false;
                            b3 = false;
                            menu.setVisibility(View.GONE);
                            menu.setVisibility(View.INVISIBLE);
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.teamviewer.quicksupport.market");
                            startActivity(launchIntent);
                            break;
                    }
                    return true;
                }
            });
            File f = new File(foldervideo);
            File file[] = f.listFiles();

            ttsManager = new TTSManager();
            ttsManager.init(this);

//        Handler myHandler3 = new Handler();
//        myHandler3.postDelayed(aaa, 3000);

//        Handler myHandler2 = new Handler();
//        myHandler2.postDelayed(aaa2, 3000);

            Handler myHandler = new Handler();
            myHandler.postDelayed(mMyRunnable, 10000);

            FullScreencall();
    /*    try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
            Thread socketServerThread = new Thread(new SocketServerThread());
            socketServerThread.start();

       /*     Thread socketServerThread2 = new Thread(new SocketServerThread2());
            socketServerThread2.start(); */
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

            CheckConnessione();

            menu = (FrameLayout) findViewById(R.id.menu);
            menu.setVisibility(View.GONE);
            menu.setVisibility(View.INVISIBLE);



            //nasconde la barra in basso
            getWindow().getDecorView().setSystemUiVisibility(View.GONE);

       /*     // NPI SDKn
            objnumSize = new NInt();
            NString strPrinter = new NString();
            objLib = new NPrinterLib(MainActivity.this);
            nmsRet = objLib.NEnumPrinters(strPrinter, objnumSize);
            if (nmsRet == NRet.SUCCESS) {
            } else {
                Toast bread = Toast.makeText(getApplicationContext(), "stampanti red: " + nmsRet, Toast.LENGTH_LONG);
                bread.show();
            }
            objLib.NOpenPrinter(mStrPrinter);
            nmsRet = objLib.NOpenResult(mStrPrinter);*/

        }
    }

    private void showSystemBar() {
        String commandStr = "am startservice -n com.android.systemui/.SystemUIService";
        runAsRoot(commandStr);

    }

    private void hideSystemBar() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            //REQUIRES ROOT
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; //HONEYCOMB AND OLDER

            //v.RELEASE  //4.0.3
            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }
            //  killall com.android.systemui
            String commandStr = "service call activity " +
                    ProcID + " s16 com.android.systemui";
            runAsRoot(commandStr);


        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }

    private void runAsRoot(String commandStr) {
        try {
            CommandCapture command = new CommandCapture(0, commandStr);
            RootTools.getShell(true).add(command).waitForFinish();
        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }
    /*    private Runnable aaa = new Runnable() {
            @Override
            public void run() {
                Thread socketServerThread2 = new Thread(new SocketServerThread2());
                socketServerThread2.start();
            }
        };
        private Runnable aaa2 = new Runnable() {
            @Override
            public void run() {
                Thread socketServerThread = new Thread(new SocketServerThread());
                socketServerThread.start();
            }
        };*/
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (serverSocket2 != null) {
            try {
                serverSocket2.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    protected String DOWNLOAD3(String aurl, String nomefileSrv) {
        int count;
        try {
            URL url = new URL(aurl);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
//            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(foldervideo + nomefileSrv);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
//                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast bread = Toast.makeText(getApplicationContext(), "Reload", Toast.LENGTH_LONG);
                    bread.show();
                    WebView myWebView = (WebView) findViewById(R.id.webview);
                    myWebView.loadUrl(paginatotem);
                }
            });

        } catch (Exception e) {
        }


        return null;

    }


    void showError(final String err) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
            }
        });
    }

    String codice = "";
    String lingua = "";
    String[] ArayMessaggio = null;

    private class SocketServerThread extends Thread {
        static final int SocketServerPORT = 5001;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);//,0,getInetAddress());
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast bread = Toast.makeText(getApplicationContext(), "5001", Toast.LENGTH_LONG);
                        bread.show();

                    }
                });
                while (true) {
                    Socket socket = serverSocket.accept();
                    if (socket.getInetAddress().isLoopbackAddress()) {

                        count++;
                  /*  message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";*/

                        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    if (in1.ready()) {
                        s = in1.readLine();
                      /*  MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast bread = Toast.makeText(getApplicationContext(),  s  , Toast.LENGTH_LONG);
                                bread.show();
                                // ttsManager.initQueue(s);
                            }
                        });*/
                        s = s.replaceAll("GET /", "");
                        s = s.split(" ")[0];

                        s = s.replaceAll("_", " ");
                        // s = URLDecoder.decode(s);
                        String NomeFile = s;

                        s = s.replaceAll("GET /", "");
                        s = s.replaceAll("HTTP/1.1", "");

//                        message+= "replayed: " + msgReply + " "+"server request: "+s;
                        if (s.lastIndexOf("http:", 0) == 0) {
                            String hh = "?";
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            NomeFile = s.split("/")[s.split("/").length - 1];

                        }

                        File f = new File(foldervideo + NomeFile);

                        if (f.exists()) {
                         /*   MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast bread = Toast.makeText(getApplicationContext(), "File esiste. "  , Toast.LENGTH_LONG);
                                    bread.show();
                                    // ttsManager.initQueue(s);
                                }
                            });*/
                        } else {
                            if (spaziolibero() > 200) {
//                                if (FileScaricati.contains((NomeFile))) {
//
//                                } else {
                                FileScaricati.add(NomeFile);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast bread = Toast.makeText(getApplicationContext(), "Download " + s, Toast.LENGTH_LONG);
                                        bread.show();
                                        // ttsManager.initQueue(s);
                                    }
                                });
                                if (s.lastIndexOf("http:", 0) == 0) {
                                    DOWNLOAD3(s, NomeFile);
                                } else {
                                    DOWNLOAD3("http://pediatotem.it/totem/media/" + s, NomeFile);
                                }
//                                }
                            } else {
                                //svuota
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast bread = Toast.makeText(getApplicationContext(), "Svuota disco pieno ", Toast.LENGTH_LONG);
                                        bread.show();
                                        // ttsManager.initQueue(s);
                                    }
                                });
                                File curDir = new File(foldervideo);
                                long length = 0;
                                for (File fi : curDir.listFiles()) {
                                    if (f.isDirectory()) {
                                        for (File child : fi.listFiles()) {
                                            fi.delete();
                                            break;
                                        }
                                    } else {
                                        fi.delete();
                                        break;
                                    }
                                }
                            }
                        }

                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                                socket, count);
                        socketServerReplyThread.run();

                   /* MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast bread = Toast.makeText(getApplicationContext(), "'" + s + "'", Toast.LENGTH_LONG);
                            bread.show();
                            ttsManager.initQueue(s);
                        }
                    });*/

                    } else {
                        //firewall
                        final String ipclient = socket.getInetAddress().toString();
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast bread = Toast.makeText(getApplicationContext(), "firewall " + ipclient, Toast.LENGTH_LONG);
                                bread.show();

                                //ttsManager.initQueue(s);

                            }
                        });
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                showError("E11. " + e.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                showError("E1. " + e.getMessage());
            }
            Thread socketServerThread = new Thread(new SocketServerThread());
            socketServerThread.start();
        }
    }

    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "OK" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                //message += "replayed: " + msgReply + "\n";

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
               /* e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";*/
            }

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // msg.setText(message);
                }
            });
        }

    }

/*
    private class SocketServerThread2 extends Thread {
        static final int SocketServerPORT2 = 5000;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket2 = new ServerSocket(SocketServerPORT2);//,0,getInetAddress());
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast bread = Toast.makeText(getApplicationContext(), "5000", Toast.LENGTH_LONG);
                        bread.show();

                    }
                });
                while (true) {
                    Socket socket = serverSocket2.accept();
                    if (socket.getInetAddress().isLoopbackAddress()) {
                        count++;
                  */
/*  message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";*//*

                        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    if (in1.ready()) {
                        s = in1.readLine();
                        s = s.replaceAll("GET /", "");
                        s = s.split(" ")[0];
                        s = s.replaceAll("_", " ");
                        String NomeFile = s;
                        s = s.replaceAll("GET /", "");
                        s = s.replaceAll("HTTP/1.1", "");
                        s = com.lviiier.totem.UTFEncodingUtil.decodeUTF(s);
                        codice = s.split("~")[1];
                        lingua = s.split("~")[0];
//                       s = URLDecoder.decode(s);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                              //  mp.start();
                                Toast bread = Toast.makeText(getApplicationContext(), codice, Toast.LENGTH_LONG);
                                bread.show();
                                ttsManager.addQueue( codice, lingua);
                            }
                        });

                        SocketServerReplyThread2 socketServerReplyThread2 = new SocketServerReplyThread2(socket, count);
                        socketServerReplyThread2.run();

                   */
/* MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast bread = Toast.makeText(getApplicationContext(), "'" + s + "'", Toast.LENGTH_LONG);
                            bread.show();

                            ttsManager.initQueue(s);

                        }
                    });*//*


                    } else {
                        //firewall
                        final String ipclient = socket.getInetAddress().toString();
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast bread = Toast.makeText(getApplicationContext(), "firewall " + ipclient, Toast.LENGTH_LONG);
                                bread.show();
                                //ttsManager.initQueue(s);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                showError("E22. " + e.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                showError("E2. " + e.getMessage());
            }
            Thread socketServerThread2 = new Thread(new SocketServerThread2());
            socketServerThread2.start();
        }
    }

    private class SocketServerThread extends Thread {
        static final int SocketServerPORT = 5001;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);//,0,getInetAddress());
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast bread = Toast.makeText(getApplicationContext(), "5001", Toast.LENGTH_LONG);
                        bread.show();

                    }
                });
                while (true) {
                    Socket socket = serverSocket.accept();
                    if (socket.getInetAddress().isLoopbackAddress()) {

                        count++;
                  */
/*  message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";*//*


                        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    if (in1.ready()) {
                        s = in1.readLine();
                      */
/*  MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast bread = Toast.makeText(getApplicationContext(),  s  , Toast.LENGTH_LONG);
                                bread.show();
                                // ttsManager.initQueue(s);
                            }
                        });*//*

                        s = s.replaceAll("GET /", "");
                        s = s.split(" ")[0];

                        s = s.replaceAll("_", " ");
                        // s = URLDecoder.decode(s);
                        String NomeFile = s;

                        s = s.replaceAll("GET /", "");
                        s = s.replaceAll("HTTP/1.1", "");

//                        message+= "replayed: " + msgReply + " "+"server request: "+s;
                        if (s.lastIndexOf("http:", 0) == 0) {
                            String hh = "?";
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            s = s.replace(hh, "/");
                            NomeFile = s.split("/")[s.split("/").length - 1];

                        }

                        File f = new File(foldervideo + NomeFile);

                        if (f.exists()) {
                         */
/*   MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast bread = Toast.makeText(getApplicationContext(), "File esiste. "  , Toast.LENGTH_LONG);
                                    bread.show();
                                    // ttsManager.initQueue(s);
                                }
                            });*//*

                        } else {
                            if (spaziolibero() > 200) {
//                                if (FileScaricati.contains((NomeFile))) {
//
//                                } else {
                                FileScaricati.add(NomeFile);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast bread = Toast.makeText(getApplicationContext(), "Download " + s, Toast.LENGTH_LONG);
                                        bread.show();
                                        // ttsManager.initQueue(s);
                                    }
                                });
                                if (s.lastIndexOf("http:", 0) == 0) {
                                    DOWNLOAD3(s, NomeFile);
                                } else {
                                    DOWNLOAD3("https://pediatotem.it/totem/media/" + s, NomeFile);
                                }
//                                }
                            } else {
                                //svuota
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast bread = Toast.makeText(getApplicationContext(), "Svuota disco pieno ", Toast.LENGTH_LONG);
                                        bread.show();
                                        // ttsManager.initQueue(s);
                                    }
                                });
                                File curDir = new File(foldervideo);
                                long length = 0;
                                for (File fi : curDir.listFiles()) {
                                    if (f.isDirectory()) {
                                        for (File child : fi.listFiles()) {
                                            fi.delete();
                                            break;
                                        }
                                    } else {
                                        fi.delete();
                                        break;
                                    }
                                }
                            }
                        }

                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                                socket, count);
                        socketServerReplyThread.run();

                   */
/* MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast bread = Toast.makeText(getApplicationContext(), "'" + s + "'", Toast.LENGTH_LONG);
                            bread.show();
                            ttsManager.initQueue(s);
                        }
                    });*//*


                    } else {
                        //firewall
                        final String ipclient = socket.getInetAddress().toString();
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast bread = Toast.makeText(getApplicationContext(), "firewall " + ipclient, Toast.LENGTH_LONG);
                                bread.show();

                                //ttsManager.initQueue(s);

                            }
                        });
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                showError("E11. " + e.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                showError("E1. " + e.getMessage());
            }
            Thread socketServerThread = new Thread(new SocketServerThread());
            socketServerThread.start();
        }

    }
*/

    private long spaziolibero() {
        long megAvailable = -1;
        try {
            StatFs stat = new StatFs(foldervideo);
            long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            megAvailable = bytesAvailable / (1024 * 1024);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return megAvailable;
    }


    private class SocketServerReplyThread2 extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread2(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "OK" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                //message += "replayed: " + msgReply + "\n";

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
               /* e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";*/
            }

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // msg.setText(message);
                }
            });
        }
    }

    private InetAddress getInetAddress() {

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
//                        ip += "SiteLocalAddress: "
//                                + inetAddress.getHostAddress() + "\n";
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // ip += "Something Wrong! " + e.toString() + "\n";
        }

        return null;
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    private Runnable mMyRunnable = new Runnable() {
        @Override
        public void run() {

            WebView myWebView = (WebView) findViewById(R.id.webview);
            myWebView.loadUrl(paginatotem);
            Toast bread = Toast.makeText(getApplicationContext(), "Spazio libero " + spaziolibero() + "Mb", Toast.LENGTH_LONG);
            bread.show();
            FrameLayout spinner = (FrameLayout) findViewById(R.id.clessidera);
            // spinner.setVisibility(View.GONE);
            spinner.setVisibility(View.INVISIBLE);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //  mp.start();

                    ttsManager.addQueue("Pronto", "IT-it");
                }
            });

        }
    };


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.lviiier.totem/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (serverSocket2 != null) {
            try {
                serverSocket2.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.lviiier.totem/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //FUNZIONE RICARICA PAGINA SULLA WEB VIEW
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String weburl) {
            view.loadUrl(weburl);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String weburl) {
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                switch (keyCode) {
                    case KeyEvent.KEYCODE_F4:
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.webkey");
                        startActivity(launchIntent);
                        break;
                    case KeyEvent.KEYCODE_F5:
                        Handler myHandler = new Handler();
                        myHandler.postDelayed(mMyRunnable, 1000);
                        break;
                    case KeyEvent.KEYCODE_F9:
                        myHandler = new Handler();
                        myHandler.postDelayed(mMyRunnable, 1000);
                        break;
                    case KeyEvent.KEYCODE_F6:
                        try {
                            Process p = Runtime.getRuntime().exec("su");
                            Runtime.getRuntime().exec(new String[]{"/system/bin/su", "-c", "reboot"});
                        } catch (IOException e) {
                        }
                        break;
                    case KeyEvent.KEYCODE_F7:
                        launchIntent = getPackageManager().getLaunchIntentForPackage("com.hardkernel.odroid");
                        startActivity(launchIntent);
//                        try {
//                            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","odroid-utility.sh"});
//                        } catch (IOException e) {
//                        }
//                        startActivity(new Intent(Settings.ACTION_SETTINGS));
//                        Intent i;
//                        PackageManager manager = getPackageManager();
//                        try {
//                            i = manager.getLaunchIntentForPackage("ODROID Utility");
//                            if (i == null)
//                                throw new PackageManager.NameNotFoundException();
//                            i.addCategory(Intent.CATEGORY_LAUNCHER);
//                            startActivity(i);
//                        } catch (PackageManager.NameNotFoundException e) {
//
//                        }
                        break;
                    case KeyEvent.KEYCODE_F8:
                        startActivity(new Intent(Settings.ACTION_SETTINGS));

                        break;
                    default:
//                        Cf+=event.getDisplayLabel();
                        break;
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }


    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
            //NASCONDE LA BARRA DELL'OROLOGIO IN ALTO
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //NASCONDE LA BARRA DEI PULSANTI VIRTUALI
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

   /* public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c1 -w3 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }
*/

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void CheckConnessione() {
        Handler myHandler = new Handler();
     /*   int remainder = cont % 5;
        if (remainder==0){
*/
        myHandler.postDelayed(faiCheckConnessione, 60000);
//        }
//        else
//        {
//            myHandler.postDelayed(faiCheckConnessione, 2000);
//        }

    }

    private Runnable faiCheckConnessione = new Runnable() {
        @Override
        public void run() {
//start background processing
            Toast bread = null;
            try {
                boolean Isconnesso = isOnline();
                if (Isconnesso == true) {

                    if (ConnessioneAttiva == false) {
                        bread = Toast.makeText(getApplicationContext(), "Internet OK ", Toast.LENGTH_LONG);
                        bread.show();
                        WebView myWebView = (WebView) findViewById(R.id.webview);
                        myWebView.loadUrl(paginatotem);

                        FrameLayout spinner = (FrameLayout) findViewById(R.id.clessidera);
                        // spinner.setVisibility(View.GONE);
                        spinner.setVisibility(View.INVISIBLE);


                    }
                    ConnessioneAttiva = true;
                } else {
                    bread = Toast.makeText(getApplicationContext(), "Internet assente!!! ", Toast.LENGTH_LONG);
                    bread.show();
                    ConnessioneAttiva = false;
                    FrameLayout spinner = (FrameLayout) findViewById(R.id.clessidera);
                    // spinner.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            CheckConnessione();
        }
    };

    private void StampaStringa(String text) {
        NInt objnumSize = null;
        NInt objJobID = null;
        try {

            String strCommand = null;
            StringBuilder strCmdBuffer = new StringBuilder("1B4C10");
            objLib.NOpenPrinter(mStrPrinter);
       /*     nmsRet = NRet.SUCCESS;
            nmsRet = objLib.NOpenResult(mStrPrinter);
            if (nmsRet != NRet.SUCCESS && nmsRet != NRet.WRN_PRTALREADYOPEN) {
                Toast  bread5 = Toast.makeText(getApplicationContext(), "errore apertura " + nmsRet, Toast.LENGTH_LONG);
                bread5.show();

            }*/

            strCmdBuffer = new StringBuilder("1B4C10");
            strCommand = strCmdBuffer.toString();
            if ((nmsRet = objLib.NPrint(mStrPrinter, strCommand, strCommand.getBytes().length, objJobID)) != NRet.SUCCESS) {
                Toast bread = Toast.makeText(getApplicationContext(), "Error A9..." + "Print Text Error..." + nmsRet, Toast.LENGTH_LONG);
                bread.show();
            }
            byte[] rawSendData;
            objJobID = null;
            strCmdBuffer = new StringBuilder("1B40");
            strCmdBuffer.append("1B6101");

            //sottolineato 1B2D01
            //strCmdBuffer.append("1B2D02");//sottolineato

            strCmdBuffer.append("1B2148");//GRassetto 16px
            strCmdBuffer.append("1B2138");//GRassetto 12px normale marcato

            strCmdBuffer.append("\"" + text + "\"0A1B6100");

            strCmdBuffer.append("1B2100");// reset altezza
            objJobID = new NInt();
            strCommand = strCmdBuffer.toString();
            if ((nmsRet = objLib.NPrint(mStrPrinter, strCommand, strCommand.getBytes().length, objJobID)) != NRet.SUCCESS) {
                Toast bread1 = Toast.makeText(getApplicationContext(), "error " + nmsRet, Toast.LENGTH_LONG);
                bread1.show();
            }
            strCmdBuffer = new StringBuilder("1B40");
            strCmdBuffer.append("\"\"0A0A");
        /*    strCmdBuffer.append("\"\"0A0A");
            strCmdBuffer.append("\"\"0A0A");*/
            objJobID = new NInt();
            strCommand = strCmdBuffer.toString();
            if (objLib.NPrint(mStrPrinter, strCommand, strCommand.getBytes().length, objJobID) != NRet.SUCCESS) {
                Toast bread1 = Toast.makeText(getApplicationContext(), "error " + nmsRet, Toast.LENGTH_LONG);
                bread1.show();
            }


        } catch (Exception e) {
            // showAlertMsg("Error...", "Print Text Error...");
            Toast bread = Toast.makeText(getApplicationContext(), "Error A2..." + "Print Text Error...", Toast.LENGTH_LONG);
            bread.show();
        }
        try {
            // NDPrint process
            byte[] rawSendData;

            objJobID = new NInt();
            // Cut command
            rawSendData = new byte[2];
            rawSendData[0] = 0x1B;
            rawSendData[1] = 0x6D;
            nmsRet = objLib.NDPrint(mStrPrinter, rawSendData, rawSendData.length, objJobID);
            if (nmsRet != NRet.SUCCESS) {
                Toast bread = Toast.makeText(getApplicationContext(), "error cut", Toast.LENGTH_LONG);
                bread.show();
            }
        } catch (Exception e) {
            // showAlertMsg("Error...", "Print Text Error...");
            Toast bread = Toast.makeText(getApplicationContext(), "Error A2..." + "Print Text Error...", Toast.LENGTH_LONG);
            bread.show();
        }

    }

    private void Stampa(String[] text) {
        try {
            int nmsRet = 0;
            NInt objJobID = null;
            NInt objnumSize = null;
            String strCommand = null;
            StringBuilder strCmdBuffer = new StringBuilder("1B4C10");

            nmsRet = NRet.SUCCESS;
            nmsRet = objLib.NOpenResult(mStrPrinter);
/*            if (nmsRet != NRet.SUCCESS && nmsRet != NRet.ERR_PRTALREADYOPEN) {
//                    bread = Toast.makeText(getApplicationContext(), "error apertura " + nmsRet, Toast.LENGTH_LONG);
//                    bread.show();

            }*/


            strCmdBuffer = new StringBuilder("1B4C10");


     /*       strCmdBuffer.append("1B6101");
            strCmdBuffer.append("\"Pediatotem.it\"0A1B6100");
            strCmdBuffer.append("1B5C2000\"1-5-12, Unoki, Ohtaku, Tokyo 146-8650 Japan\"0A");
            strCmdBuffer.append("1B5C2000\"TEL    : 81-3-3750-5817\"0A");

            strCmdBuffer.append("1B5C2000\"E-Mail : overseas@primex.co.jp\"0A");
            strCmdBuffer.append("1B5C2000\"******************************************\"0A");
            strCmdBuffer.append("1B6101\"Thank you for the coming to the store.\"0A");
            strCmdBuffer.append("\"I rest and do business this month.\"1B5C00000A");
            strCmdBuffer.append("1B61001B5C2000\"******************************************\"0A");
            strCmdBuffer.append("1B5C2000\"Date : ");
            strCmdBuffer.append("\"0A0A");
            strCmdBuffer.append("1B5C2000\"Coke\"0A");
            strCmdBuffer.append("1B5C2000\"     $ 2.00 * 2                   $ 4.00\"0A");
            strCmdBuffer.append("1B5C2000\"Orange juice\"0A");
            strCmdBuffer.append("1B5C2000\"     $ 2.00 * 1                   $ 2.00\"0A");
            strCmdBuffer.append("1B5C2000\"Beer\"0A");
            strCmdBuffer.append("1B5C2000\"     $ 6.00 * 3                  $ 18.00\"0A");
            strCmdBuffer.append("1B5C2000\"-------------------------------------------\"0A");
            strCmdBuffer.append("1B5C20001B21201C2104\"TOTAL    USD   24.00\"1C21001B21000A");
            strCmdBuffer.append("1B5C2000\"-------------------------------------------\"0A");
            strCmdBuffer.append("1B5C2000\"Deposit \"1B21201C2104\"     USD   30.00\"1C21001B21000A");
            strCmdBuffer.append("1B5C2000\"Change\"1B21201C2104\"      USD    6.00\"1C21001B21000A");
            strCmdBuffer.append("\"\"0A0A");
            strCmdBuffer.append("\"\"0A0A");
            strCmdBuffer.append("\"\"0A0A");*/
            //***********************
      /*      strCmdBuffer.append("1B4C11050060000001");
            strCmdBuffer.append("1B61001B5C2000\"Date of issuance    : ");
            SimpleDateFormat sdf 	= new SimpleDateFormat("MM/dd/yyyy HH:mm");
            strCmdBuffer.append(sdf.format(new Date()));
            strCmdBuffer.append("\"0A");
            //strCmdBuffer.append("1B6100\"Coupon type         : Ticket test print\"0A");
            strCmdBuffer.append("1B5C5000");
            strCmdBuffer.append("1B2138");
            strCmdBuffer.append("1B2D02");
            strCmdBuffer.append("\"All item 20% OFF!\"0A");
            strCmdBuffer.append("1B21000A");




            strCmdBuffer.append("1B5C8000");
            strCmdBuffer.append("1B2118");//16px grassetto condensato
            strCmdBuffer.append("1B2D02");//sottolineato
            strCmdBuffer.append("\"18\"0A0A");
            strCmdBuffer.append("1B2100");// reset altezza

            strCmdBuffer.append("1B2128");//GRandezza
            strCmdBuffer.append("1B2D02");//sottolineato
            strCmdBuffer.append("\"28\"0A0A");
            strCmdBuffer.append("1B2100");// reset altezza

            strCmdBuffer.append("1B2138");//GRassetto 16px
            strCmdBuffer.append("1B2D02");//sottolineato
            strCmdBuffer.append("\"38\"0A0A");
            strCmdBuffer.append("1B2100");// reset altezza

            strCmdBuffer.append("1B2148");//GRassetto 12px
            strCmdBuffer.append("1B2D02");//sottolineato
            strCmdBuffer.append("\"48\"0A0A");
            strCmdBuffer.append("1B2100");// reset altezza

            // strCmdBuffer.append("1B21FF");//MARCATO


            //  strCmdBuffer.append("1B2A01");//GRandezza
            // strCmdBuffer.append("1B2D02");//sottolineato
            strCmdBuffer.append("\"Doppio\"0A0A");
            strCmdBuffer.append("1B2100");// reset altezza*/

            strCommand = strCmdBuffer.toString();
            if ((nmsRet = objLib.NPrint(mStrPrinter, strCommand, strCommand.getBytes().length, objJobID)) != NRet.SUCCESS) {
                Toast bread = Toast.makeText(getApplicationContext(), "Error A9..." + "Print Text Error...", Toast.LENGTH_LONG);
                bread.show();
            }

            int cont = 1;
            for (String str : text) {
                //  prnDevice.printTextLF(cont + " " +str,fnt1 );
                if (cont > 3) {
                    byte[] rawSendData;
                    objJobID = null;
                    strCmdBuffer = new StringBuilder("1B40");
                    strCmdBuffer.append("1B6101");

                    //sottolineato 1B2D01
                    //strCmdBuffer.append("1B2D02");//sottolineato

                    String A = str.split("§")[0];
                    String B = str.split("§")[1];
                    if (A.equals("1")) {

                    }
                    ;
                    if (A.equals("2")) {

                    }
                    ;
                    if (A.equals("3")) {
                        strCmdBuffer.append("1B2138");//GRassetto 12px normale marcato
                    }
                    ;
                    if (A.equals("4")) {
                        strCmdBuffer.append("1B2148");//GRassetto 16px
                        strCmdBuffer.append("1B2138");//GRassetto 12px normale marcato
                    }
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");
                  /*  strCmdBuffer.append("\"1\"0A1B6100");
                   strCmdBuffer.append("1B21A1");//piccolo sottolineato centrato font simpatico
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");

                    strCmdBuffer.append("1B21B1");//Medio sottolineato centrato font simpatico
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");


                    strCmdBuffer.append("1B21C1");//piccolissimo sottolineato centrato font simpatico
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");

                    strCmdBuffer.append("1B21D1");//
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");

                    strCmdBuffer.append("1B21E1");//
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");

                    strCmdBuffer.append("1B21F1");//
                    strCmdBuffer.append("\"" + B + "\"0A1B6100");

                    strCmdBuffer.append("1B2100");// reset altezza
                    strCmdBuffer.append("\"2\"0A1B6100");

                    strCmdBuffer.append("1B21A2");//piccolo sottolineato centrato font simpatico
                    strCmdBuffer.append("\"" + B + "\"1B2138");

                    strCmdBuffer.append("1B21B2");//Medio sottolineato centrato font simpatico
                    strCmdBuffer.append("\"" + B + "\"1B2138");


                    strCmdBuffer.append("1B21C2");//piccolissimo sottolineato centrato font simpatico
                    strCmdBuffer.append("\"" + B + "\"1B2138");

                    strCmdBuffer.append("1B21D2");//
                    strCmdBuffer.append("\"" + B + "\"1B2138");

                    strCmdBuffer.append("1B21E2");//
                    strCmdBuffer.append("\"" + B + "\"1B2138");

                    strCmdBuffer.append("1B21F2");//
                    strCmdBuffer.append("\"" + B + "\"1B2138");*/

                    strCmdBuffer.append("1B2100");// reset altezza
                    objJobID = new NInt();
                    strCommand = strCmdBuffer.toString();
                    if ((nmsRet = objLib.NPrint(mStrPrinter, strCommand, strCommand.getBytes().length, objJobID)) != NRet.SUCCESS) {
                         /*   bread = Toast.makeText(getApplicationContext(), "error " + nmsRet, Toast.LENGTH_LONG);
                            bread.show();*/
                    }
                }
                cont += 1;
            }
            strCmdBuffer = new StringBuilder("1B40");
            strCmdBuffer.append("\"\"0A0A");
        /*    strCmdBuffer.append("\"\"0A0A");
            strCmdBuffer.append("\"\"0A0A");*/
            objJobID = new NInt();
            strCommand = strCmdBuffer.toString();
            if ((nmsRet = objLib.NPrint(mStrPrinter, strCommand, strCommand.getBytes().length, objJobID)) != NRet.SUCCESS) {
                         /*   bread = Toast.makeText(getApplicationContext(), "error " + nmsRet, Toast.LENGTH_LONG);
                            bread.show();*/
            }
        } catch (Exception e) {
            // showAlertMsg("Error...", "Print Text Error...");
            Toast bread = Toast.makeText(getApplicationContext(), "Error A2..." + "Print Text Error...", Toast.LENGTH_LONG);
            bread.show();
        }
        try {
            // NDPrint process
            byte[] rawSendData;
            int nmsRet = 0;
            NInt objJobID = null;
            objJobID = new NInt();
            // Cut command
            rawSendData = new byte[2];
            rawSendData[0] = 0x1B;
            rawSendData[1] = 0x6D;
            nmsRet = objLib.NDPrint(mStrPrinter, rawSendData, rawSendData.length, objJobID);
            if (nmsRet != NRet.SUCCESS) {
                Toast bread = Toast.makeText(getApplicationContext(), "error cut", Toast.LENGTH_LONG);
                bread.show();
            }
        } catch (Exception e) {
            // showAlertMsg("Error...", "Print Text Error...");
            Toast bread = Toast.makeText(getApplicationContext(), "Error A2..." + "Print Text Error...", Toast.LENGTH_LONG);
            bread.show();
        }
    }
    //fine programma************************
}
