package cat.kikevite.transferudp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int PUERTO_SERVIDOR = 9800;
    byte[] buffer = new byte[1024];
    EditText ipEdit, missatgeEdit, portEdit;
    boolean borrarMissatge = true;
    List<String> conversacioTot = new ArrayList<>();
    List<Boolean> conversacioEnviat = new ArrayList<>();
    RecyclerViewAdapter adapter;
    RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEdit = findViewById(R.id.ipEdit);
        missatgeEdit = findViewById(R.id.missatgeEdit);
        portEdit = findViewById(R.id.portEdit);

        portEdit.setText("" + PUERTO_SERVIDOR);

        myRecyclerView = findViewById(R.id.conversaRv);
        RecyclerView.LayoutManager myLayoutManager;
        myRecyclerView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(myLayoutManager);
        adapter = new RecyclerViewAdapter(conversacioTot, conversacioEnviat);
        myRecyclerView.setAdapter(adapter);

        String ip = getIPAddress(true);
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            ipEdit.setText(parts[0] + "." + parts[1] + "." + parts[2] + ".125");
            //ipEdit.setText("90.175.12.204");
            this.setTitle("IP Origen: " + ip + ":" + PUERTO_SERVIDOR);
        } else {
            ipEdit.setText("no conectat");
            this.setTitle("IP Origen: no conectat");
        }

        Thread thread = new Thread(new MyClient());
        thread.start();
    }

    ////////////////////////////////////////
    //////// REBRE MISSATGES ///////////////
    ////////////////////////////////////////
    class MyClient implements Runnable {

        Handler handler = new Handler();

        @Override
        public void run() {
            try {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "waiting...", Toast.LENGTH_SHORT).show();
                    }
                });

                while (true) {
                    DatagramSocket socketUDP = new DatagramSocket(PUERTO_SERVIDOR);
                    DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                    socketUDP.receive(peticion);
                    socketUDP.close();
                    InetAddress direccionServidor = peticion.getAddress();
                    String msg = direccionServidor + ":" + new String(peticion.getData());
                    conversacioTot.add(msg);
                    conversacioEnviat.add(false);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            myRecyclerView.scrollToPosition(conversacioTot.size() - 1);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    ////////////////////////////////////////
    //////// ENVIAR MISSATGES //////////////
    ////////////////////////////////////////
    public void enviar(View view) {
        BackgroundTask b = new BackgroundTask();
        String msg = missatgeEdit.getText().toString();
        String desti = ipEdit.getText().toString();
        String port = portEdit.getText().toString();
        b.execute(msg, desti, port);
        conversacioTot.add(msg);
        conversacioEnviat.add(true);
        adapter.notifyDataSetChanged();
        myRecyclerView.scrollToPosition(conversacioTot.size() - 1);
        if (borrarMissatge) {
            missatgeEdit.setText("");
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            Boolean error = false;
            String msg = params[0];
            String direccioS = params[1];
            String portS = params[2];
            int port = Integer.valueOf(portS);
            try {
                DatagramSocket socketUDP = new DatagramSocket();
                InetAddress direccionServidor = InetAddress.getByName(direccioS);
                buffer = msg.getBytes();
                DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
                socketUDP.send(pregunta);
                //socketUDP.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
            return error;
        }

        @Override
        protected void onPostExecute(Boolean error) {
            super.onPostExecute(error);
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }
}