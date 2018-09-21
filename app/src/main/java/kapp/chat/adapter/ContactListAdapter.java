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
 * Created by Arunraj on 11/24/2017.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.DataObjectHolder>{

    List<User> list;
    Context context;
    OnInteractContactListAdapter mListener;

    public interface OnInteractContactListAdapter {
        void setOnInteractContactListAdapter(String contactName, String contactNumber, String contactImage, String contactLastSeen);
    }

    public ContactListAdapter(Context context, Fragment fragment, List<User> list) {
        this.context = context;
        this.list = list;
        mListener = (OnInteractContactListAdapter) fragment;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataObjectHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        final String username = list.get(position).user_name;
        final String mobile = list.get(position).mobile_no;
        String status = list.get(position).user_status;
        final String profile_image = list.get(position).profile_image_path;
        final String lastSeen = String.valueOf(list.get(position).last_seen);

        holder.txtUserName.setText(username == null ? mobile : username);
        holder.txtStatus.setText(status == null ? "Hey! there i'm using KChat" : status);

        Glide.with(context.getApplicationContext()).load(profile_image == null ? R.drawable.ic_user_24 : profile_image).apply(RequestOptions.circleCropTransform()).into(holder.imgProfile);
        holder.lContactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.setOnInteractContactListAdapter(username, mobile, profile_image, lastSeen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {

        LinearLayout lContactItem;
        TextView txtUserName, txtStatus;
        ImageView imgProfile;

        public DataObjectHolder(View itemView) {
            super(itemView);
            lContactItem = itemView.findViewById(R.id.lContactItem);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }
}
