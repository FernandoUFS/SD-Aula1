package com.cumbetech.sd_aula1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cumbetech.sd_aula1.Adapters.MessageAdapter;
import com.cumbetech.sd_aula1.Objects.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Chat extends AppCompatActivity {
    public static final String TAG = "SistemasDistribuidos";

    private DatagramSocket socket;
    private EditText edtIp, edtMsg, edtPort;
    private ListView lv;
    private ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private String name = "No name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p1);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        initialize();
        openSocket();
    }

    private void initialize() {
        edtIp = (EditText) findViewById(R.id.edtIp);
        edtPort = (EditText) findViewById(R.id.edtPort);
        edtMsg = (EditText) findViewById(R.id.edtMsg);
        lv = (ListView) findViewById(R.id.lv);
        adapter = new MessageAdapter(this, messages);
        lv.setAdapter(adapter);
        ((Button) findViewById(R.id.btnSend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        Intent i = getIntent();
        if (i.hasExtra("name")){
            if (!i.getStringExtra("name").isEmpty()) {
                name = i.getStringExtra("name");
            }
        }
    }

    private String getIp() {
        return edtIp.getText().toString();
    }

    private int getPort() {
        try {
            return Integer.parseInt(edtPort.getText().toString());
        } catch (Exception e) {
            return 5000;
        }
    }


    private String getMessage() {
        return edtMsg.getText().toString();
    }

    private void openSocket() {
        try {
            int p = 5000;
            Intent i = getIntent();
            if (i.hasExtra("port")) {
                p = i.getIntExtra("port", 5000);
            }

            final int port = p;

            socket = new DatagramSocket(port);

            new AsyncTask<Void, Void, Void>(){

                @Override
                protected void onPostExecute(Void aVoid) {
                    adapter.notifyDataSetChanged();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        while(true){
                            byte[] buf = new byte[512];

                            DatagramPacket pack = new DatagramPacket(buf, buf.length);

                            Log.d(TAG, "Esperando mensagem...");
                            socket.receive(pack);

                            InetAddress addr = pack.getAddress();
                            String rec = (new String(pack.getData())).trim();
                            Log.d(TAG, "Mensagem recebida: " + rec);
                            String[] recSplit = rec.split(";", 2);

                            String name = "No name";
                            String msg = "";

                            if (recSplit.length == 2) {
                                name = recSplit[0];
                                msg = recSplit[1];
                            } else {
                                msg = recSplit[0];
                            }

                            messages.add(new Message(Message.RECEIVED, name, msg, addr));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro no socket: ", e);
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Log.e(TAG, "Erro no socket: ", e);
        }
    }

    public void sendMessage() {
        adapter.notifyDataSetChanged();

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean res) {
                if (res != null && res) {
                    adapter.notifyDataSetChanged();
                    edtMsg.setText("");
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    String msg = getMessage().trim();
                    if (msg.length() > 0) {
                        byte[] buf = (name + ";" + msg).getBytes();
                        InetAddress ip = InetAddress.getByName(getIp());
                        DatagramPacket pack = new DatagramPacket(buf, buf.length, ip, getPort());
                        socket.send(pack);
                        //only github test
                        messages.add(new Message(Message.SENDED, name, msg, null));
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao enviar socket: ", e);
                }
                int i = 0;
                return true;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        socket.close();
        super.onDestroy();
    }
}
