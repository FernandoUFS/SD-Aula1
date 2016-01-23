package com.cumbetech.sd_aula1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cumbetech.sd_aula1.Objects.Message;
import com.cumbetech.sd_aula1.R;

import java.util.ArrayList;

/**
 * Created by Fernando on 23/01/2016.
 */
public class MessageAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<Message> messages;

    public MessageAdapter(Context ctx, ArrayList<Message> messages) {
        this.ctx = ctx;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message msg = messages.get(position);

        LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (msg.type == Message.RECEIVED) {
            v = vi.inflate(R.layout.message_received, parent, false);
        } else {
            v = vi.inflate(R.layout.message_sent, parent, false);
        }
        TextView tvMessage = (TextView) v.findViewById(R.id.tvMessage);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);

        tvMessage.setText(msg.message);
        if (tvName != null) {
            tvName.setText(msg.name);
        }

        return v;
    }
}
