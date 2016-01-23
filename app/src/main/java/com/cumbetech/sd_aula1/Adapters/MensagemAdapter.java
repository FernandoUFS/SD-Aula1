package com.cumbetech.sd_aula1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cumbetech.sd_aula1.Objects.Mensagem;
import com.cumbetech.sd_aula1.R;

import java.util.ArrayList;

/**
 * Created by Fernando on 23/01/2016.
 */
public class MensagemAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(Context ctx, ArrayList<Mensagem> mensagens) {
        this.ctx = ctx;
        this.mensagens = mensagens;
    }

    @Override
    public int getCount() {
        return mensagens.size();
    }

    @Override
    public Mensagem getItem(int position) {
        return mensagens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Mensagem msg = mensagens.get(position);

        LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (msg.tipo == Mensagem.RECEIVED) {
            v = vi.inflate(R.layout.message_received, parent, false);
        } else {
            v = vi.inflate(R.layout.message_sended, parent, false);
        }
        TextView tvMsg = (TextView) v.findViewById(R.id.tvMsg);
        TextView tvNome = (TextView) v.findViewById(R.id.tvNome);

        tvMsg.setText(msg.msg);
        if (tvNome != null) {
            tvNome.setText(msg.nome);
        }

        return v;
    }
}
