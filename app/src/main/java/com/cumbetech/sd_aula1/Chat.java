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

import com.cumbetech.sd_aula1.Adapters.MensagemAdapter;
import com.cumbetech.sd_aula1.Objects.Mensagem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Chat extends AppCompatActivity {
    public static final String TAG = "SistemasDistribuidos";

    private DatagramSocket socket;
    private EditText edtIp, edtMsg, edtPort;
    private ListView lv;
    private ArrayList<Mensagem> mensagens = new ArrayList<>();
    private MensagemAdapter adapter;
    private String nome = "Sem nome";

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
        edtPort = (EditText) findViewById(R.id.edtPorta);
        edtMsg = (EditText) findViewById(R.id.edtMsg);
        lv = (ListView) findViewById(R.id.lv);
        adapter = new MensagemAdapter(this, mensagens);
        lv.setAdapter(adapter);
        ((Button) findViewById(R.id.btnSend)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        Intent i = getIntent();
        if (i.hasExtra("nome")){
            if (!i.getStringExtra("nome").isEmpty()) {
                nome = i.getStringExtra("nome");
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
            return 3000;
        }
    }


    private String getMessage() {
        return edtMsg.getText().toString();
    }

    private void openSocket() {
        try {
            int p = 3000;
            Intent i = getIntent();
            if (i.hasExtra("port")) {
                p = i.getIntExtra("port", 3000);
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
                            byte[] buf = new byte[64];

                            DatagramPacket pack = new DatagramPacket(buf, buf.length);

                            Log.d(TAG, "Esperando mensagem...");
                            socket.receive(pack);

                            InetAddress addr = pack.getAddress();
                            String rec = (new String(pack.getData())).trim();
                            Log.d(TAG, "Mensagem recebida: " + rec);
                            String[] recSplit = rec.split(";", 2);

                            String nome = "Sem nome";
                            String msg = "";

                            if (recSplit.length == 2) {
                                nome = recSplit[0];
                                msg = recSplit[1];
                            } else {
                                msg = recSplit[0];
                            }

                            mensagens.add(new Mensagem(Mensagem.RECEIVED, nome, msg, addr));
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
                        byte[] buf = (nome + ";" + msg).getBytes();
                        InetAddress ip = InetAddress.getByName(getIp());
                        DatagramPacket pack = new DatagramPacket(buf, buf.length, ip, getPort());
                        socket.send(pack);
                        mensagens.add(new Mensagem(Mensagem.SENDED, nome, msg, null));
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
