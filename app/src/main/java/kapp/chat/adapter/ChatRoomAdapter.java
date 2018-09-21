package kapp.chat.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import kapp.chat.R;
import kapp.chat.db.pojo.User;

/**
 * Created by Arunraj on 12/4/2017.
 */

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.DataObjectHolder> {

    List<User> list;
    Context context;
    OnInteractChatRoomListAdapter mListener;


    public interface OnInteractChatRoomListAdapter {
        void setOnInteractChatRoomListAdapter(String contactName, String contactNumber, String contactImage, String contactLastSeen);
    }

    public ChatRoomAdapter(Context context, Fragment fragment, List<User> list) {
        this.context = context;
        this.list = list;
        mListener = (OnInteractChatRoomListAdapter) fragment;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataObjectHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false));
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        final String username = list.get(position).user_name;
        final String mobile = list.get(position).mobile_no;
        String status = list.get(position).user_status;
        int unReadCount = list.get(position).unread_message_count;
        final String profile_image = list.get(position).profile_image_path;
        final String lastSeen = String.valueOf(list.get(position).last_seen);

        holder.txtUserName.setText(username == null ? mobile : username);
        holder.txtStatus.setText(status == null ? "Hey! there i'm using KChat" : status);
        if (unReadCount == 0) {
            holder.lRight.setVisibility(View.INVISIBLE);
            holder.txtNewMessageCount.setVisibility(View.INVISIBLE);
            holder.txtNewMessageCount.setText("");
        } else {
            holder.lRight.setVisibility(View.VISIBLE);
            holder.txtNewMessageCount.setVisibility(View.VISIBLE);
            holder.txtNewMessageCount.setText(list.get(position).unread_message_count + "");
        }

        Glide.with(context.getApplicationContext())
                .load(profile_image == null ? R.drawable.ic_user_24 : profile_image)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imgProfile);

        holder.lContactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.setOnInteractChatRoomListAdapter(username, mobile, profile_image, lastSeen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {

        LinearLayout lContactItem, lRight;
        TextView txtUserName, txtStatus, txtNewMessageCount;
        ImageView imgProfile;

        public DataObjectHolder(View itemView) {
            super(itemView);
            lContactItem = itemView.findViewById(R.id.lContactItem);
            lRight = itemView.findViewById(R.id.lRight);
            txtNewMessageCount = itemView.findViewById(R.id.txtNewMessageCount);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }

    }

}
