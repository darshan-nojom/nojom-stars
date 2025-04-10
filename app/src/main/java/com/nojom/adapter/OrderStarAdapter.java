package com.nojom.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.databinding.ItemOrderStarsBinding;
import com.nojom.model.CampService;
import com.nojom.model.CampSocialPlatform;
import com.nojom.model.Profile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderStarAdapter extends RecyclerView.Adapter<OrderStarAdapter.ViewHolder> {

    private List<Profile> timelineItems;
    private BaseActivity activity;
    private final PrettyTime p = new PrettyTime();
    private OnClickStarListener onClickStarListener;
    private boolean isWhiteBg;
    private List<CampService> platformList;

    public void setWhiteBackground(boolean b) {
        isWhiteBg = b;
    }

    public interface OnClickStarListener {
        void onClickStar(int pos, Profile profile);

        void onClickChat(int pos, Profile profile);
    }

    public OrderStarAdapter(BaseActivity activity, List<Profile> timelineItems, List<CampService> services, OnClickStarListener listener) {
        this.timelineItems = timelineItems;
        this.platformList = services;
        this.activity = activity;
        this.onClickStarListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderStarsBinding itemCardListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_order_stars, parent, false);
        return new ViewHolder(itemCardListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile item = timelineItems.get(position);
        holder.binding.tvTitle.setText(item.firstName + " " + item.lastName);

        Glide.with(activity).load(item.profile_picture).error(R.color.orange).into(holder.binding.imgProfile);
//        holder.binding.tvStatus.setText(capitalizeWords(item.req_status));
        holder.binding.txtPrice.setText(Utils.decimalFormat(String.valueOf(item.total_service_price)) + " " + activity.getString(R.string.sar));

        if (item.req_status != null) {
            switch (item.req_status) {
                case "approved":
                case "in_progress": {
                    holder.binding.txtStatus.setText(activity.getString(R.string.approved_pending_your_progress));
                    holder.binding.txtStatus.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.C_D4E4FA)));
                    holder.binding.txtStatus.setTextColor(ContextCompat.getColor(activity, R.color.C_007AFF));
                    setProgressTintColor(holder.binding.progress1, R.color.C_007AFF);
                    setProgressTintColor(holder.binding.progress2, R.color.C_007AFF);
                    setProgressTintColor(holder.binding.progress3, R.color.C_F2F2F7);
                    setProgressTintColor(holder.binding.progress4, R.color.C_F2F2F7);
                    setProgress(holder.binding.progress1, 100);
                    setProgress(holder.binding.progress2, 100);
                    setProgress(holder.binding.progress3, 0);
                    setProgress(holder.binding.progress4, 0);
                    break;
                }
                case "pending": {
                    holder.binding.txtStatus.setText(activity.getString(R.string.requested_pending_star_approval));
                    holder.binding.txtStatus.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.C_F2F2F7)));
                    holder.binding.txtStatus.setTextColor(ContextCompat.getColor(activity, R.color.black));
                    setProgressTintColor(holder.binding.progress1, R.color.black);
                    setProgressTintColor(holder.binding.progress2, R.color.C_F2F2F7);
                    setProgressTintColor(holder.binding.progress3, R.color.C_F2F2F7);
                    setProgressTintColor(holder.binding.progress4, R.color.C_F2F2F7);
                    setProgress(holder.binding.progress1, 100);
                    setProgress(holder.binding.progress2, 0);
                    setProgress(holder.binding.progress3, 0);
                    setProgress(holder.binding.progress4, 0);
                    break;
                }
                case "completed": {
                    if (item.is_released == 1) {
                        holder.binding.txtStatus.setText(activity.getString(R.string.you_approved_completed));
                        holder.binding.txtStatus.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.C_C7591A)));
                        holder.binding.txtStatus.setTextColor(ContextCompat.getColor(activity, R.color.C_34C759));
                        setProgressTintColor(holder.binding.progress1, R.color.C_34C759);
                        setProgressTintColor(holder.binding.progress2, R.color.C_34C759);
                        setProgressTintColor(holder.binding.progress3, R.color.C_34C759);
                        setProgressTintColor(holder.binding.progress4, R.color.C_34C759);
                        setProgress(holder.binding.progress1, 100);
                        setProgress(holder.binding.progress2, 100);
                        setProgress(holder.binding.progress3, 100);
                        setProgress(holder.binding.progress4, 100);
                    } else {
                        holder.binding.txtStatus.setText(activity.getString(R.string.done_pending_client_review));
                        holder.binding.txtStatus.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.C_FF95001A)));
                        holder.binding.txtStatus.setTextColor(ContextCompat.getColor(activity, R.color.C_FF9500));
                        setProgressTintColor(holder.binding.progress1, R.color.C_FF9500);
                        setProgressTintColor(holder.binding.progress2, R.color.C_FF9500);
                        setProgressTintColor(holder.binding.progress3, R.color.C_FF9500);
                        setProgressTintColor(holder.binding.progress4, R.color.C_F2F2F7);
                        setProgress(holder.binding.progress1, 100);
                        setProgress(holder.binding.progress2, 100);
                        setProgress(holder.binding.progress3, 100);
                        setProgress(holder.binding.progress4, 0);
                    }
                    break;
                }
                case "canceled":
                case "rejected": {
                    holder.binding.txtStatus.setText(activity.getString(R.string.star_declined_cancelled));
                    holder.binding.txtStatus.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.C_FF3B30_10)));
                    holder.binding.txtStatus.setTextColor(ContextCompat.getColor(activity, R.color.C_FF3B30));
                    setProgressTintColor(holder.binding.progress1, R.color.C_FF3B30);
                    setProgressTintColor(holder.binding.progress2, R.color.C_FF3B30);
                    setProgressTintColor(holder.binding.progress3, R.color.C_FF3B30);
                    setProgressTintColor(holder.binding.progress4, R.color.C_FF3B30);
                    setProgress(holder.binding.progress1, 100);
                    setProgress(holder.binding.progress2, 100);
                    setProgress(holder.binding.progress3, 100);
                    setProgress(holder.binding.progress4, 100);
                    break;
                }
            }
        }

        if (platformList != null && platformList.size() > 0) {
            if (platformList.get(0).socialPlatform != null && platformList.get(0).socialPlatform.size() > 0) {
                holder.binding.imageContainer.removeAllViews();
                addOverlappingImages(holder.binding.imageContainer, platformList.get(0).socialPlatform);
            }
        }
    }

    private void setProgressTintColor(ProgressBar progressBar, int color) {
        progressBar.setProgressTintList(ColorStateList.valueOf(activity.getResources().getColor(color)));
    }

    private void setProgress(ProgressBar progressBar, int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public int getItemCount() {
        return timelineItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderStarsBinding binding;

        public ViewHolder(@NonNull ItemOrderStarsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (isWhiteBg) {
                binding.loutHeader.setBackground(activity.getResources().getDrawable(R.drawable.white_button_bg_7));
            }
            binding.loutHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //start star activity
                    if (onClickStarListener != null) {
                        onClickStarListener.onClickStar(getAdapterPosition(), timelineItems.get(getAdapterPosition()));
                    }
                }
            });

            binding.relChat.setOnClickListener(view -> {
                HashMap<String, String> chatMap = new HashMap<>();
                chatMap.put(Constants.RECEIVER_ID, timelineItems.get(getAdapterPosition()).id + "");
                chatMap.put(Constants.RECEIVER_NAME, timelineItems.get(getAdapterPosition()).username);
                chatMap.put(Constants.RECEIVER_PIC, timelineItems.get(getAdapterPosition()).profile_picture);
                chatMap.put(Constants.SENDER_ID, activity.getUserID() + "");
                chatMap.put(Constants.SENDER_NAME, activity.getProfileData().username);
                chatMap.put(Constants.SENDER_PIC, activity.getProfileData().filePaths.clientAttachments + activity.getProfileData().profilePic);
                chatMap.put("isProject", "1");//1 mean updated record
//                if (expertGigDetail.gigType.equalsIgnoreCase("1")) {
//                    chatMap.put("projectType", "3");//2=job & 1= gig
//                } else {
                chatMap.put("projectType", "1");//2=job & 1= gig
//                }
                chatMap.put("isDetailScreen", "true");

                Intent chatIntent = new Intent(activity, ChatMessagesActivity.class);
                chatIntent.putExtra(Constants.CHAT_ID, activity.getProfileData().id + "-" + timelineItems.get(getAdapterPosition()).id);  // ClientId - AgentId
                chatIntent.putExtra(Constants.CHAT_DATA, chatMap);
                if (activity.getIsVerified() == 1) {
                    activity.startActivity(chatIntent);
                } else {
                    activity.toastMessage(activity.getString(R.string.verification_is_pending_please_complete_the_verification_first_before_chatting_with_them));
                }
            });
        }
    }

    private void addOverlappingImages(LinearLayout container, List<CampSocialPlatform> imageRes) {
        int overlapOffset = -5; // Adjust the overlap offset in dp
        int size = 30; // Circle size in dp

        // Convert dp to pixels
        float scale = activity.getResources().getDisplayMetrics().density;
//        int offsetPx = (int) (overlapOffset * scale);
        int sizePx = (int) (size * scale);

        for (int i = 0; i < imageRes.size(); i++) {
            CircleImageView imageView = new CircleImageView(activity);
//            imageView.setImageResource(imageRes.get(i).image.path+imageRes.get(i).image.fileName);
            Glide.with(activity).load(imageRes.get(i).filename).placeholder(R.drawable.dp).error(R.drawable.dp).into(imageView);
            // Set circular shape
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBorderColor(Color.WHITE);
            imageView.setBorderWidth(2);
            //imageView.setBackgroundResource(R.drawable.circle_round_gray); // Circular shape drawable


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
//            if (i != 0) {
//                params.leftMargin = -30; // Overlap the images
//            }
            imageView.setLayoutParams(params);

            container.addView(imageView);

            if (i == 2) {
                break;
            }
        }

//        if (imageRes.size() > 3) {
//            TextView textView = new TextView(activity);
//            textView.setText("+" + (imageRes.size() - 3));
//            textView.setTextColor(Color.WHITE);
//            textView.setGravity(Gravity.CENTER);
//            textView.setTextColor(Color.BLACK);
//            textView.setBackgroundResource(R.drawable.circle_round_gray);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
//            params.leftMargin = -30; // Position after the last image
//            textView.setLayoutParams(params);
//
//            container.addView(textView);
//        }
    }
}
