package com.nojom.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.databinding.ItemChatListCopyBinding;
import com.nojom.model.ChatList;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.SimpleViewHolder> {

    private BaseActivity baseActivity;
    private List<ChatList.Datum> arrUserList;
    private String imgPath;
    private LayoutInflater layoutInflater;
    private final PrettyTime p = new PrettyTime();

    public ChatListAdapter(Context context, List<ChatList.Datum> objects, String imgPath) {
        this.arrUserList = objects;
        this.imgPath = imgPath;
        baseActivity = ((BaseActivity) context);
    }

    public void doRefresh(List<ChatList.Datum> arrUserChatList) {
        this.arrUserList = arrUserChatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemChatListCopyBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_chat_list_copy, parent, false);
        return new SimpleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        final ChatList.Datum item = arrUserList.get(position);
        holder.binding.tvReceiverName.setText(item.username);
        if (item.lastMessageData != null) {
            if (item.lastMessageData.messageCreatedAt != 0) {
                holder.binding.tvTime.setText(getTime(item.lastMessageData.messageCreatedAt));
            }
        }

        if (!TextUtils.isEmpty(item.profilePic)) {
            Glide.with(baseActivity)
                    .load(imgPath + "/" + item.profilePic)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.imgProfile);
        }

        if (item.typing) {
            holder.binding.imgOnline.setVisibility(View.GONE);
            holder.binding.tvLastMessage.setTextColor(baseActivity.getResources().getColor(R.color.online));
            holder.binding.tvLastMessage.setText(baseActivity.getString(R.string.typing_));
        } else {

            if (item.lastMessageData != null) {
                if (baseActivity.getUserID() == item.lastMessageData.senderId) {
                    holder.binding.tvLastMessage.setTextColor(baseActivity.getResources().getColor(R.color.black));
                } else if (item.lastMessageData.isSeenMessage.equalsIgnoreCase("1")) {
                    holder.binding.tvLastMessage.setTextColor(baseActivity.getResources().getColor(R.color.colorPrimary));
                } else {
                    holder.binding.tvLastMessage.setTextColor(baseActivity.getResources().getColor(R.color.black));
                }

                if (!TextUtils.isEmpty(item.lastMessageData.message.trim())) {
                    holder.binding.tvLastMessage.setText(item.lastMessageData.message);
                } else if (item.lastMessageData.file != null && item.lastMessageData.file.files != null) {
                    holder.binding.tvLastMessage.setText(item.lastMessageData.file.files.get(0).file);
                } else {
                    holder.binding.tvLastMessage.setText(baseActivity.getString(R.string._offer));
                }
            } else {
                holder.binding.tvLastMessage.setTextColor(baseActivity.getResources().getColor(R.color.black));
            }
        }

        if (item.isSocketOnline == 1) {
            holder.binding.imgOnline.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imgOnline.setVisibility(View.GONE);
        }

        holder.binding.loutHeader.setOnClickListener(v ->

        {

            Intent intent = new Intent(baseActivity, ChatMessagesActivity.class);

            intent.putExtra("user", item.username);
            intent.putExtra("reciever_id", item.id);
            intent.putExtra("status", item.isSocketOnline);
            intent.putExtra("profilePic", imgPath + "/" + item.profilePic);

            HashMap<String, String> chatMap = new HashMap<>();
            chatMap.put("isProject", "1");//1 mean updated record
            intent.putExtra(Constants.CHAT_DATA, chatMap);
            baseActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrUserList != null ? arrUserList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemChatListCopyBinding binding;

        public SimpleViewHolder(ItemChatListCopyBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public String getTime(long messageCreatedTime) {
        String validTime;
        String msgDate = Utils.convertDate(String.valueOf(messageCreatedTime), "dd MMM yyyy");
        String todayDate = Utils.convertDate(String.valueOf(System.currentTimeMillis()), "dd MMM yyyy");

        if (msgDate.equalsIgnoreCase(todayDate)) {
            validTime = p.format(new Date(messageCreatedTime));
        } else {
            String yesterdayDate = Utils.convertDate(String.valueOf(System.currentTimeMillis() - (1000 * 60 * 60 * 24)), "dd MMM yyyy");
            if (msgDate.equalsIgnoreCase(yesterdayDate)) {
                validTime = baseActivity.getString(R.string.yesterday);
            } else {
                if(baseActivity.language.equals("ar")){
                    validTime = Utils.convertDate(String.valueOf(messageCreatedTime), "dd MMM, yyyy");
                }else{
                    validTime = Utils.convertDate(String.valueOf(messageCreatedTime), "MMM dd, yyyy");
                }

            }
        }

        return validTime;
    }
}
