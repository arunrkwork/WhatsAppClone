package kapp.chat.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import kapp.chat.R;
import kapp.chat.pojo.ChatMessage;
import kapp.chat.utill.L;

/**
 * Created by Arunraj on 11/27/2017.
 */

public class MessagingAdapter extends RecyclerView.Adapter {

    List<ChatMessage> list;
    Context context;
    OnMessageItemClick mListener;

    public interface OnMessageItemClick{
        void setOnMessageItemClick(String message_id, String file_path);
    }

    public MessagingAdapter(Fragment fragment, Context context, List<ChatMessage> list) {
        mListener = (OnMessageItemClick) fragment;
        this.context = context;
        this.list = list;
        L.i("chat message : " + list.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ChatMessage.RECEIVER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left, parent, false);
                return new LeftHolder(view);
            case ChatMessage.SENDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right, parent, false);
                return new RightHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ChatMessage chatMessage = list.get(position);
        if (chatMessage != null) {
            switch (chatMessage.TYPE) {
                case ChatMessage.RECEIVER:
                    L.i("chat message receiver: " + list.get(position).message);
                    if (chatMessage.message_type.equals(chatMessage.IMG)) {
                        ((LeftHolder) holder).imgAttach.setVisibility(View.VISIBLE);
                        ((LeftHolder) holder).txtLeftMessage.setText("");

                        ((LeftHolder) holder).imgAttach.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListener.setOnMessageItemClick(chatMessage.message_id, chatMessage.message);
                            }
                        });

                        Glide.with(context).load(chatMessage.message).into(((LeftHolder) holder).imgAttach);
                    } else {
                        ((LeftHolder) holder).imgAttach.setVisibility(View.GONE);
                        ((LeftHolder) holder).txtLeftMessage.setText(list.get(position).message);
                    }
                    break;
                case ChatMessage.SENDER:
                    L.i("chat message sender: " + list.get(position).message);
                    if (chatMessage.message_type.equals(chatMessage.IMG)) {
                        ((RightHolder) holder).imgAttach.setVisibility(View.VISIBLE);
                        ((RightHolder) holder).txtRightMessage.setText("");

                        ((RightHolder) holder).imgAttach.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListener.setOnMessageItemClick(chatMessage.message_id, chatMessage.message);
                            }
                        });

                        Glide.with(context).load(chatMessage.message).into(((RightHolder) holder).imgAttach);
                    } else {
                        ((RightHolder) holder).imgAttach.setVisibility(View.GONE);
                        ((RightHolder) holder).txtRightMessage.setText(list.get(position).message);
                    }
                    if (list.get(position).isRead == 1) ((RightHolder) holder).txtRightMessage.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_sent,0);
                    else if (list.get(position).isRead == 2) ((RightHolder) holder).txtRightMessage.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_received,0);
                    else if (list.get(position).isRead == 3) ((RightHolder) holder).txtRightMessage.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_read,0);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).TYPE) {
            case ChatMessage.RECEIVER:
                return ChatMessage.RECEIVER;
            case ChatMessage.SENDER:
                return ChatMessage.SENDER;
            default:
                return -1;
        }
    }

    public class LeftHolder extends RecyclerView.ViewHolder{

        TextView txtLeftMessage;
        ImageView imgAttach;

        public LeftHolder(View itemView) {
            super(itemView);
            imgAttach = itemView.findViewById(R.id.imgAttach);
            txtLeftMessage = itemView.findViewById(R.id.txtLeftMessage);
        }
    }

    public class RightHolder extends RecyclerView.ViewHolder {

        TextView txtRightMessage;
        ImageView imgAttach;

        public RightHolder(View itemView) {
            super(itemView);
            imgAttach = itemView.findViewById(R.id.imgAttach);
            txtRightMessage = itemView.findViewById(R.id.txtRightMessage);
        }
    }

}
