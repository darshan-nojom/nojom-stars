package com.nojom.adapter;

import static android.text.util.Linkify.EMAIL_ADDRESSES;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nojom.R;
import com.nojom.databinding.ItemChatMsgBinding;
import com.nojom.model.ChatMessageList;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.chat.ActivityLeaveApp;
import com.nojom.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;


public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.SimpleViewHolder> {
    private BaseActivity baseActivity;
    private ArrayList<ChatMessageList.DataChatList> chatMessage;
    private LayoutInflater layoutInflater;
    private WithdrawOfferListener withdrawOfferListener;


    public interface WithdrawOfferListener {
        void onClickWithdrawOffer(ChatMessageList.DataChatList data, int pos);

        void onClickOfferView(ChatMessageList.DataChatList data, int pos);
    }

    public ChatMessageAdapter(ArrayList<ChatMessageList.DataChatList> objects, Context context, WithdrawOfferListener listener) {
        this.chatMessage = objects;
        baseActivity = ((BaseActivity) context);
        this.withdrawOfferListener = listener;
        setHasStableIds(true);
    }

    public static String getFileNameFromUrl(String url) {
        try {
            URL resource = new URL(url);
            String urlString = resource.getFile();
            return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemChatMsgBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_chat_msg, parent, false);
        return new SimpleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        final ChatMessageList.DataChatList item = chatMessage.get(position);

        if (item.senderId.equalsIgnoreCase(String.valueOf(baseActivity.getUserID()))) {
            holder.binding.frameOutgoing.setVisibility(View.VISIBLE);
            holder.binding.frameIncoming.setVisibility(View.GONE);
            holder.binding.tvMyTimestamp.setVisibility(View.VISIBLE);
            holder.binding.tvTimestamp.setVisibility(View.GONE);
            if (item.isSeenMessage.equalsIgnoreCase("1")) {
                if (item.offer != null) {
                    holder.binding.imgSeenOffer.setImageResource(R.drawable.sent);
                } else {
                    holder.binding.imgSeen.setImageResource(R.drawable.sent);
                }
            } else {
                if (item.offer != null) {
                    holder.binding.imgSeenOffer.setImageResource(R.drawable.seen);
                } else {
                    holder.binding.imgSeen.setImageResource(R.drawable.seen);
                }
            }
            try {
                if (item.file != null && item.file.files != null) {
                    if (item.file.files.get(0).fileStorage != null && item.file.files.get(0).fileStorage.equalsIgnoreCase("firebase")) {//firebase URL
                        holder.binding.tvMyMessage.setVisibility(View.GONE);
                        holder.binding.rlImageSender.setVisibility(View.VISIBLE);

                        String fileUrl = item.file.files.get(0).firebaseUrl;
                        redirectIntent(fileUrl, holder, true);
                    } else if (!TextUtils.isEmpty(item.file.files.get(0).file)) {//socket url
                        holder.binding.tvMyMessage.setVisibility(View.GONE);
                        holder.binding.rlImageSender.setVisibility(View.VISIBLE);

                        String fileUrl = item.file.path + item.file.files.get(0).file;
                        redirectIntent(fileUrl, holder, true);

                    } else {
                        holder.binding.rlImageSender.setVisibility(View.GONE);
                        holder.binding.tvMyMessage.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.binding.rlImageSender.setVisibility(View.GONE);
                    holder.binding.tvMyMessage.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(item.message)) {
                holder.binding.tvMyMessage.setText(item.message.trim());
            }


            if (item.offer != null) {
                holder.binding.frameOutgoing.setVisibility(View.GONE);
                holder.binding.linOffer.setVisibility(View.VISIBLE);

                holder.binding.txtOfferTitle.setText(item.offer.offerTitle);
                holder.binding.txtOfferDesc.setText(item.offer.description);
                holder.binding.txtOfferPrice.setText(Utils.priceWith$(Utils.getDecimalValue(String.valueOf(item.offer.price))));

                if (item.offer.offerStatus == 0 || item.offer.offerStatus == 3) {
                    holder.binding.txtWithdrawOffer.setText(item.offer.offerStatus == 0 ? baseActivity.getString(R.string.deleted) : baseActivity.getString(R.string.rejected));
                    holder.binding.txtWithdrawOffer.setTextColor(baseActivity.getResources().getColor(R.color.red));
                    holder.binding.relWithdrawOffer.setBackground(null);
                } else if (item.offer.offerStatus == 2 || item.offer.offerStatus == 5) {
                    holder.binding.txtWithdrawOffer.setText(item.offer.offerStatus == 2 ? baseActivity.getString(R.string.accepted) : baseActivity.getString(R.string.completed));
                    holder.binding.txtWithdrawOffer.setTextColor(baseActivity.getResources().getColor(R.color.full_dark_green));
                    holder.binding.relWithdrawOffer.setBackground(null);
                } else if (item.offer.offerStatus == 1) {
                    holder.binding.txtWithdrawOffer.setText(baseActivity.getString(R.string.withdraw_the_offer));
                    holder.binding.txtWithdrawOffer.setTextColor(baseActivity.getResources().getColor(R.color.black));
                    holder.binding.relWithdrawOffer.setBackground(baseActivity.getResources().getDrawable(R.drawable.gray_button_bg));

                } else if (item.offer.offerStatus == 4 || item.offer.offerStatus == 6) {
                    holder.binding.txtWithdrawOffer.setText(item.offer.offerStatus == 4 ? baseActivity.getString(R.string.withdraw) : baseActivity.getString(R.string.expired));
                    holder.binding.txtWithdrawOffer.setTextColor(baseActivity.getResources().getColor(R.color.red));
                    holder.binding.relWithdrawOffer.setBackground(null);
                }

                if (item.messageCreatedAt != 0) {
                    holder.binding.linTimestampOffer.setVisibility(View.VISIBLE);
                    holder.binding.tvMyTimestampOffer.setVisibility(View.VISIBLE);
                    holder.binding.tvMyTimestampOffer.setText(Utils.convertDate(String.valueOf(item.messageCreatedAt), "hh:mm a"));
                } else {
                    holder.binding.linTimestampOffer.setVisibility(View.GONE);
                    holder.binding.tvMyTimestampOffer.setVisibility(View.GONE);
                }
            } else {
                holder.binding.linTimestampOffer.setVisibility(View.GONE);
                holder.binding.linOffer.setVisibility(View.GONE);
                holder.binding.linTimestampOffer.setVisibility(View.GONE);
                if (item.messageCreatedAt != 0) {
                    holder.binding.tvMyTimestamp.setVisibility(View.VISIBLE);
                    holder.binding.tvMyTimestamp.setText(Utils.convertDate(String.valueOf(item.messageCreatedAt), "hh:mm a"));
                } else {
                    holder.binding.tvMyTimestamp.setVisibility(View.GONE);
                }
            }

        } else {
            holder.binding.frameOutgoing.setVisibility(View.GONE);
            holder.binding.frameIncoming.setVisibility(View.VISIBLE);
            holder.binding.tvMyTimestamp.setVisibility(View.GONE);
            holder.binding.tvTimestamp.setVisibility(View.VISIBLE);

            try {
                if (item.file != null && item.file.files != null) {
                    if (item.file.files.get(0).fileStorage != null && item.file.files.get(0).fileStorage.equalsIgnoreCase("firebase")) {//firebase URL
                        holder.binding.tvMyMessage.setVisibility(View.GONE);
                        holder.binding.rlImageSender.setVisibility(View.VISIBLE);

                        String fileUrl = item.file.files.get(0).firebaseUrl;
                        redirectIntent(fileUrl, holder, true);
                    } else if (!TextUtils.isEmpty(item.file.files.get(0).file)) {
                        holder.binding.tvMessage.setVisibility(View.GONE);
                        holder.binding.rlImageReceiver.setVisibility(View.VISIBLE);

                        String fileUrl = item.file.path + item.file.files.get(0).file;
                        redirectIntent(fileUrl, holder, false);

                    } else {
                        holder.binding.rlImageReceiver.setVisibility(View.GONE);
                        holder.binding.tvMessage.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.binding.rlImageReceiver.setVisibility(View.GONE);
                    holder.binding.tvMessage.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(item.message)) {
//                holder.binding.tvMessage.setVisibility(View.VISIBLE);
                holder.binding.tvMessage.setText(item.message.trim());
            }

            if (item.messageCreatedAt != 0) {
                holder.binding.tvTimestamp.setVisibility(View.VISIBLE);
                holder.binding.tvTimestamp.setText(Utils.convertDate(String.valueOf(item.messageCreatedAt), "hh:mm a"));
            } else {
                holder.binding.tvTimestamp.setVisibility(View.GONE);
            }

            holder.binding.linTimestampOffer.setVisibility(View.GONE);
            holder.binding.linOffer.setVisibility(View.GONE);
        }

        if (item.isDayChange) {
            holder.binding.dayDate.setVisibility(View.VISIBLE);
            holder.binding.dayDate.setText(getTime(item.messageCreatedAt));

        } else {
            holder.binding.dayDate.setVisibility(View.GONE);
        }

        if (item.offer != null) {
            if (item.isShowOfferProgress) {
                holder.binding.txtWithdrawOffer.setVisibility(View.INVISIBLE);
                holder.binding.progressBarView.setVisibility(View.VISIBLE);
            } else {
                holder.binding.txtWithdrawOffer.setVisibility(View.VISIBLE);
                holder.binding.progressBarView.setVisibility(View.GONE);
            }
        }
    }

    public void refreshOfferView(int pos, boolean isShow, int offerStatus) {
        if(chatMessage!=null && chatMessage.size()>0) {
            chatMessage.get(pos).isShowOfferProgress = isShow;
            if (offerStatus == 4) {
                if (chatMessage.get(pos).offer != null) {
                    chatMessage.get(pos).offer.offerStatus = 4;
                }
            }
        }
        notifyItemChanged(pos);
    }

    private void commonLayout(boolean isMyData, SimpleViewHolder holder, Drawable drawable, String fileName, String fileUrl) {
        if (isMyData) {
            holder.binding.llMyDoc.setVisibility(View.VISIBLE);
            holder.binding.ivMyDoc.setImageDrawable(drawable);
            holder.binding.tvMyDocName.setText(fileName);
            holder.binding.ivMyDoc.setTag(fileUrl);
        } else {
            holder.binding.llDoc.setVisibility(View.VISIBLE);
            holder.binding.tvDocName.setText(fileName);
            holder.binding.ivDoc.setImageDrawable(drawable);
            holder.binding.ivDoc.setTag(fileUrl);
        }
    }

    private void redirectIntent(String fileUrl, SimpleViewHolder holder, boolean isMyData) {
        try {
            holder.binding.ivOutgoing.setVisibility(View.GONE);
            holder.binding.llDoc.setVisibility(View.GONE);
            holder.binding.llMyDoc.setVisibility(View.GONE);
            holder.binding.ivIncoming.setVisibility(View.GONE);

            String fileName = getFileNameFromUrl(fileUrl);

            if (fileUrl.contains(".doc") || fileUrl.contains(".docx") || fileUrl.contains(".DOC") || fileUrl.contains(".DOCX")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_word), fileName, fileUrl);
            } else if (fileUrl.contains(".txt") || fileUrl.contains(".TXT")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_txt), fileName, fileUrl);
            } else if (fileUrl.contains(".pdf") || fileUrl.contains(".PDF")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_pdf), fileName, fileUrl);
            } else if (fileUrl.contains(".ppt") || fileUrl.contains(".pptx") || fileUrl.contains(".PPT") || fileUrl.contains(".PPTX")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_ppt), fileName, fileUrl);
            } else if (fileUrl.contains(".xls") || fileUrl.contains(".xlsx") || fileUrl.contains(".XLS") || fileUrl.contains(".XLSX")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_excel), fileName, fileUrl);
            } else if (fileUrl.contains(".jpg") || fileUrl.contains(".png") || fileUrl.contains(".jpeg") || fileUrl.contains(".gif")
                    || fileUrl.contains(".JPG") || fileUrl.contains(".PNG") || fileUrl.contains(".JPEG") || fileUrl.contains(".GIF")) {
                if (isMyData) {
                    holder.binding.ivOutgoing.setVisibility(View.VISIBLE);
                    Glide.with(baseActivity)
                            .load(fileUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
//                            .resize(512, 512)
                            .centerCrop()
                            .into(holder.binding.ivOutgoing);
                    holder.binding.ivOutgoing.setTag(fileUrl);
                } else {
                    holder.binding.ivIncoming.setVisibility(View.VISIBLE);
                    Glide.with(baseActivity)
                            .load(fileUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
//                            .resize(512, 512)
                            .centerCrop()
                            .into(holder.binding.ivIncoming);
                    holder.binding.ivIncoming.setTag(fileUrl);
                }

            } else if (fileUrl.contains(".mp4") || fileUrl.contains(".avi") || fileUrl.contains(".MOV") || fileUrl.contains(".m4a")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_video_camera), fileName, fileUrl);
            } else if (fileUrl.contains(".zip") || fileUrl.contains(".rar")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_video_camera), fileName, fileUrl);
            } else if (fileUrl.contains(".mp3")) {
                commonLayout(isMyData, holder, baseActivity.getResources().getDrawable(R.drawable.vw_ic_video_camera), fileName, fileUrl);
            }
        } catch (Exception e) {
            // baseActivity.toastMessage("No application available to view this type of file");
            e.printStackTrace();
        }

    }

    private void redirectIntent(String fileUrl, ImageView view) {
        try {

            String driveUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=";
            String finalFileUrl;
            String mime;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));

            if (fileUrl.contains(".doc") || fileUrl.contains(".docx")) {
                mime = "application/msword";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".txt")) {
                mime = "text/plain";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".pdf")) {
                mime = "application/pdf";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".ppt") || fileUrl.contains(".pptx")) {
                mime = "application/vnd.ms-powerpoint";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".xls") || fileUrl.contains(".xlsx")) {
                mime = "application/vnd.ms-excel";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".jpg") || fileUrl.contains(".png") || fileUrl.contains(".jpeg") || fileUrl.contains(".gif")) {
                mime = "image/*";
                finalFileUrl = fileUrl;

            } else if (fileUrl.contains(".mp4") || fileUrl.contains(".avi") || fileUrl.contains(".MOV") || fileUrl.contains(".m4a")) {
                mime = "video/*";
                finalFileUrl = fileUrl;
            } else if (fileUrl.contains(".zip") || fileUrl.contains(".rar")) {
                mime = "application/x-wav";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".mp3")) {
                mime = "audio/*";
                finalFileUrl = fileUrl;
            } else {
                mime = "/";
                finalFileUrl = driveUrl + fileUrl;
            }
            Log.e("AAAAAA", "finalFileUrl" + finalFileUrl);
            if (mime.equalsIgnoreCase("image/*")) {
                intent.setDataAndType(Uri.parse(finalFileUrl), mime);
            } else {
                intent.setData(Uri.parse(finalFileUrl));
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            baseActivity.startActivity(intent);
            view.setTag(null);
        } catch (Exception e) {
            baseActivity.toastMessage("No application available to view this type of file");
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return chatMessage != null ? chatMessage.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        try {
            return chatMessage.get(position).messageId;
        } catch (Exception e) {
            e.printStackTrace();
            return position;
        }
    }

    private void clickListener(ImageView view) {
        view.setOnClickListener(v -> {
            try {
                if (view.getTag() != null)
                    redirectIntent(view.getTag().toString(), view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemChatMsgBinding binding;

        public SimpleViewHolder(ItemChatMsgBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            clickListener(binding.ivMyDoc);
            clickListener(binding.ivDoc);
            clickListener(binding.ivIncoming);
            clickListener(binding.ivOutgoing);

            binding.txtWithdrawOffer.setOnClickListener(v -> {
                if (withdrawOfferListener != null) {
                    ChatMessageList.DataChatList item = chatMessage.get(getAdapterPosition());
                    if (item.offer != null && item.offer.offerStatus == 1) {
                        withdrawOfferListener.onClickWithdrawOffer(item, getAdapterPosition());
                    } else if (item.offer != null && (item.offer.offerStatus == 2 || item.offer.offerStatus == 5)) {
                        withdrawOfferListener.onClickOfferView(item, getAdapterPosition());
                    }
                }
            });

            binding.linOffer.setOnClickListener(v -> {
                if (withdrawOfferListener != null) {
                    ChatMessageList.DataChatList item = chatMessage.get(getAdapterPosition());
                    if (item.offer != null && (item.offer.offerStatus == 2 || item.offer.offerStatus == 5)) {
                        withdrawOfferListener.onClickOfferView(item, getAdapterPosition());
                    }
                }
            });

            binding.tvMyMessage.setMovementMethod(BetterLinkMovementMethod.newInstance());
            Linkify.addLinks(binding.tvMyMessage, Linkify.WEB_URLS | EMAIL_ADDRESSES);
            binding.tvMessage.setMovementMethod(BetterLinkMovementMethod.newInstance());
            Linkify.addLinks(binding.tvMessage, Linkify.WEB_URLS | EMAIL_ADDRESSES);
            binding.txtOfferDesc.setMovementMethod(BetterLinkMovementMethod.newInstance());
            Linkify.addLinks(binding.txtOfferDesc, Linkify.WEB_URLS | EMAIL_ADDRESSES);
            binding.txtOfferTitle.setMovementMethod(BetterLinkMovementMethod.newInstance());
            Linkify.addLinks(binding.txtOfferTitle, Linkify.WEB_URLS | EMAIL_ADDRESSES);
            BetterLinkMovementMethod.linkify(Linkify.WEB_URLS | EMAIL_ADDRESSES, baseActivity)
                    .setOnLinkClickListener((textView, url) -> {
                        Intent intent = new Intent(baseActivity, ActivityLeaveApp.class);
                        intent.putExtra("link", url);
                        baseActivity.startActivity(intent);
                        return true;
                    })
                    .setOnLinkLongClickListener((textView, url) -> {
                        // Handle long-clicks.
                        return true;
                    });
        }
    }


    public String getTime(long messageCreatedTime) {
        String validTime;
        String msgDate = Utils.convertDate(String.valueOf(messageCreatedTime), "dd MMM yyyy");
        String todayDate = Utils.convertDate(String.valueOf(System.currentTimeMillis()), "dd MMM yyyy");

        if (msgDate.equalsIgnoreCase(todayDate)) {
            validTime = baseActivity.getString(R.string.today);
        } else {
            String yesterdayDate = Utils.convertDate(String.valueOf(System.currentTimeMillis() - (1000 * 60 * 60 * 24)), "dd MMM yyyy");
            if (msgDate.equalsIgnoreCase(yesterdayDate)) {
                validTime = baseActivity.getString(R.string.yesterday);
            } else {
                validTime = Utils.convertDate(String.valueOf(messageCreatedTime), "dd MMM yyyy");
            }
        }

        return validTime;
    }
}
