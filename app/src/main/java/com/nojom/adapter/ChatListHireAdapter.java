package com.nojom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.nojom.R;
import com.nojom.databinding.ItemChatListHireBinding;
import com.nojom.model.ChatList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class ChatListHireAdapter extends RecyclerSwipeAdapter<ChatListHireAdapter.SimpleViewHolder> {

    private Context context;
    private List<ChatList.Datum> arrUserList;
    private String filePath;
    private PrettyTime p = new PrettyTime();
    private BaseActivity baseActivity;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    public ChatListHireAdapter(Context context, List<ChatList.Datum> data, String filePath) {
        this.context = context;
        arrUserList = data;
        this.filePath = filePath;
        baseActivity = ((BaseActivity) context);
    }

    public void doRefresh(List<ChatList.Datum> paymentList) {
        this.arrUserList = paymentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemChatListHireBinding itemAccountBinding =
                ItemChatListHireBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        if (arrUserList != null && arrUserList.size() > 0) {

            holder.binding.swipe.setShowMode(SwipeLayout.ShowMode.LayDown);

            final ChatList.Datum item = arrUserList.get(position);
            holder.binding.tvReceiverName.setText(item.username);
            if (item.lastMessageData != null) {
                if (item.lastMessageData.messageCreatedAt != 0) {
                    holder.binding.tvTime.setText(getTime(item.lastMessageData.messageCreatedAt));
                }
            }

            if (!TextUtils.isEmpty(item.profilePic)) {
                Glide.with(context)
                        .load(filePath + "/" + item.profilePic)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.binding.imgProfile);
            }

            if (item.typing) {
                holder.binding.imgOnline.setVisibility(View.GONE);
                holder.binding.tvLastMessage.setTextColor(context.getResources().getColor(R.color.online));
                holder.binding.tvLastMessage.setText(baseActivity.getString(R.string.typing_));
            } else {

                if (item.lastMessageData != null) {
                    if (baseActivity.getUserID() == item.lastMessageData.senderId) {
                        holder.binding.tvLastMessage.setTextColor(context.getResources().getColor(R.color.black));
                    } else if (item.lastMessageData.isSeenMessage.equalsIgnoreCase("1")) {
                        holder.binding.tvLastMessage.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    } else {
                        holder.binding.tvLastMessage.setTextColor(context.getResources().getColor(R.color.black));
                    }

                    if (!TextUtils.isEmpty(item.lastMessageData.message)) {
                        holder.binding.tvLastMessage.setText(item.lastMessageData.message);
                    } else if (item.lastMessageData.file != null && item.lastMessageData.file.files != null) {
                        holder.binding.tvLastMessage.setText(item.lastMessageData.file.files.get(0).file);
                    }
                } else {
                    holder.binding.tvLastMessage.setTextColor(context.getResources().getColor(R.color.black));
                }
            }

            if (item.isSocketOnline == 1) {
                holder.binding.imgOnline.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgOnline.setVisibility(View.GONE);
            }
        }
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrUserList != null ? arrUserList.size() : 0;
    }

    public List<ChatList.Datum> getData() {
        return arrUserList;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemChatListHireBinding binding;

        SimpleViewHolder(ItemChatListHireBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.relRoot.setOnClickListener(v -> {

            });

            itemView.llBlock.setOnClickListener(v -> mItemManger.closeAllItems());

            itemView.llArchive.setOnClickListener(view -> mItemManger.closeAllItems());
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
                validTime = Utils.convertDate(String.valueOf(messageCreatedTime), "MMM dd, yyyy");
            }
        }

        return validTime;
    }
}
