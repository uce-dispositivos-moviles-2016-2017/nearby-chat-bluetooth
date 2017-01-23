package com.darwindeveloper.nearby.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.darwindeveloper.nearby.MainActivity;
import com.darwindeveloper.nearby.R;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

/**
 * Created by DARWIN on 31/12/2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private ArrayList<SMS> messages;
    private Context context;
    private OnCheckBoxItemClickListener onCheckBoxItemClickListener;
    private OnLongItemClickListener onLongItemClickListener;

    public ChatAdapter(Context context, ArrayList<SMS> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sms_chat, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SMS sms = messages.get(position);

        holder.from.setText(sms.getDate_time());
        holder.sms.setText(sms.getMessage());
        if (sms.getFrom().equals("YO")) {
            holder.sms.setBackgroundResource(R.drawable.out_sms);
            holder.sms.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.from.setGravity(Gravity.END);
            holder.view_tmp.setVisibility(View.VISIBLE);
            holder.layout.setPadding(150, 0, 0, 0);

        } else {

            holder.view_tmp.setVisibility(View.GONE);
            holder.from.setGravity(Gravity.START);
            holder.sms.setBackgroundResource(R.drawable.in_sms);
            holder.sms.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.layout.setPadding(0, 0, 150, 0);

        }


        //si hay un sms del chat seleccionado mostramos el checkbox
        if (MainActivity.is_sms_select) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }


        //in some cases, it will prevent unwanted situations
        holder.checkBox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.setChecked(sms.is_selected());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                sms.setIs_selected(isChecked);
                holder.checkBox.setSelected(isChecked);
                onCheckBoxItemClickListener.onCheckBoxItemClick(holder.checkBox, sms, position);

            }
        });


        holder.sms.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!MainActivity.is_sms_select) {
                    onLongItemClickListener.onLongSmsItemClick(view, sms, position);
                }

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        View view_tmp;
        CheckBox checkBox;
        TextView from;
        EmojiTextView sms;

        public MyViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.layout_item_chat);
            view_tmp = (View) itemView.findViewById(R.id.view_tmp);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_sms);
            from = (TextView) itemView.findViewById(R.id.textView_sms_from);
            sms = (EmojiTextView) itemView.findViewById(R.id.textView_sms);
        }
    }


    public interface OnCheckBoxItemClickListener {
        void onCheckBoxItemClick(CheckBox checkBox, SMS sms, int position);
    }


    public interface OnLongItemClickListener {
        void onLongSmsItemClick(View view, SMS sms, int position);
    }


    public void setOnCheckBoxItemClickListener(OnCheckBoxItemClickListener onCheckBoxItemClickListener) {
        this.onCheckBoxItemClickListener = onCheckBoxItemClickListener;
    }


    public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener) {
        this.onLongItemClickListener = onLongItemClickListener;
    }


}
