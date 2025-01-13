package com.nojom.adapter;

import static com.nojom.util.YouTubeTitleFetcher.fetchVideoTitle;
import static com.nojom.util.YouTubeTitleFetcher.getVideoId;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.textview.TextViewSFTextPro;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemAgentCompanyBinding;
import com.nojom.model.GetAgentCompanies;
import com.nojom.model.GetYoutube;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.ReOrderYoutubeMoveCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.SimpleViewHolder> implements ReOrderYoutubeMoveCallback.ItemTouchHelperContract {

    private BaseActivity context;
    //    public String path;
    private List<GetYoutube.Data> paymentList;
    private OnClickListener onClickListener;
    private boolean isDropDone = true;

    public GetYoutube.Data getData(int pos) {
        return paymentList.get(pos);
    }

    public YoutubeAdapter(BaseActivity context, OnClickListener listener, UpdateSwipeListener updatelistener) {
        this.context = context;
        onClickListener = listener;
        this.onClickPlatformListener = updatelistener;
    }

    public void doRefresh(List<GetYoutube.Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    public void doRefresh(List<GetYoutube.Data> paymentList, int pos) {

        if (paymentList != null) {
            if (TextUtils.isEmpty(paymentList.get(pos).name) && this.paymentList != null
                    && this.paymentList.size() > 0) {
                if (paymentList.get(pos).link.equals(this.paymentList.get(pos).link)) {
                    paymentList.get(pos).name = this.paymentList.get(pos).name;
                    paymentList.get(pos).nameAr = this.paymentList.get(pos).nameAr;
                    this.paymentList.get(pos).link = paymentList.get(pos).link;
                }
//                paymentList.get(pos).id = this.paymentList.get(pos).id;
//                paymentList.get(pos).link = this.paymentList.get(pos).link;
//                paymentList.get(pos).public_status = this.paymentList.get(pos).public_status;
                this.paymentList.set(pos, paymentList.get(pos));
            }
//            this.paymentList = paymentList;
            notifyItemChanged(pos, paymentList.get(pos));
//            notifyDataSetChanged();
        }

    }

//    public void doRefresh(String path) {
//        this.path = path;
//    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAgentCompanyBinding itemAccountBinding = ItemAgentCompanyBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        GetYoutube.Data item = paymentList.get(position);

        holder.binding.txtName.setText(item.getName(context.language));

        switch (item.public_status) {
            case 2://brands
                holder.binding.txtStatus.setText(context.getString(R.string.brand_only));
                holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.c_075E45));
                DrawableCompat.setTint(holder.binding.txtStatus.getBackground(), ContextCompat.getColor(context, R.color.c_C7EBD1));
                break;
            case 3://only me
                holder.binding.txtStatus.setText(context.getString(R.string.only_me));
                holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(holder.binding.txtStatus.getBackground(), ContextCompat.getColor(context, R.color.c_FADCD9));
                break;
            default:
                holder.binding.txtStatus.setText(context.getString(R.string.public_));
                holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(holder.binding.txtStatus.getBackground(), ContextCompat.getColor(context, R.color.c_D4E4FA));
                break;
        }

        String youtubeId = extractYTId(item.link);
        String videothumb = getYoutubeThumbnailUrl(youtubeId);
//        Log.e("YT Thumb ", "" + videothumb);
        holder.binding.txtName.setTag("" + position);
        if (TextUtils.isEmpty(item.getName(context.language))) {
//            new FetchVideoTitleTask(holder.binding.txtName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.link);
            String videoId = getVideoId(item.link);

            if (videoId != null) {
                String title = fetchVideoTitle(videoId);
                holder.binding.txtName.setText(title);
            } else {
                System.out.println("Invalid YouTube URL");
                holder.binding.txtName.setText("----");
            }
        }
        Glide.with(context).load(videothumb).placeholder(R.drawable.dp).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                holder.binding.imgProfile.setImageDrawable(resource);
                return false;
            }
        }).into(holder.binding.imgProfile);

    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(paymentList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(paymentList, i, i - 1);
            }
        }
        Log.e("onRowMoved", " -- From - " + fromPosition + "  --- To - " + toPosition);
        isDropDone = false;
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onActionDone() {
        if (onClickPlatformListener != null && !isDropDone) {
            onClickPlatformListener.onSwipeSuccess(paymentList);
            isDropDone = true;
        }
    }

    @Override
    public void onRowSelected(SimpleViewHolder myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(SimpleViewHolder myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    int selectedAdapterPos;

    public void setSelectedAdapter(int selectedAdapterPos) {
        this.selectedAdapterPos = selectedAdapterPos;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemAgentCompanyBinding binding;

        SimpleViewHolder(ItemAgentCompanyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.txtUname.setVisibility(View.GONE);
            binding.txtContract.setVisibility(View.GONE);
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtName, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtUname, Constants.FONT_AR_MEDIUM);
                context.setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
                context.setArFont(binding.txtContract, Constants.FONT_AR_MEDIUM);
            }
            itemView.getRoot().setOnClickListener(v -> {
//                Intent i = new Intent(context, PaymentActivity.class);
//                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(getAdapterPosition()));
//                context.startActivity(i);
            });
            /*binding.imgEdit.setOnClickListener(v -> {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
            });*/
            binding.txtStatus.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClickShow(paymentList.get(getAdapterPosition()), getAbsoluteAdapterPosition());
                }
            });
            binding.imgMenu.setOnClickListener(v -> {
                if (onClickListener != null) {
                    int[] location = new int[2];
                    binding.imgEdit.getLocationOnScreen(location);
                    Point point = new Point();
                    point.x = location[0];

                    onClickListener.onClickMenu(paymentList.get(getAdapterPosition()), getAbsoluteAdapterPosition(), binding.imgMenu, point.x,
                            binding.txtName.getText().toString());
                }
            });
        }
    }

    public interface OnClickListener {
        void onClickShow(GetYoutube.Data companies, int pos);

        void onClickMenu(GetYoutube.Data companies, int pos, View view, int loc, String title);
    }

    private UpdateSwipeListener onClickPlatformListener;

    public interface UpdateSwipeListener {
        void onSwipeSuccess(List<GetYoutube.Data> mDatasetFiltered);

    }

    public String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        if (vId == null) {
            vId = getYouTubeId(ytUrl);
        }
        return vId;
    }

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    public String getYoutubeThumbnailUrl(String videoId) {
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    private class FetchVideoTitleTask extends AsyncTask<String, Void, String> {
        private TextView txtName;

        public FetchVideoTitleTask(TextViewSFTextPro txtName) {
            this.txtName = txtName;
        }

        @Override
        protected String doInBackground(String... strings) {
            String youtubeUrl = strings[0];
            try {
                Document doc = Jsoup.connect(youtubeUrl).get();
                Elements titleElements = doc.select("meta[name=title]");
                if (!titleElements.isEmpty()) {
                    return titleElements.attr("content");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String videoTitle) {
            if (videoTitle != null) {
                txtName.setText(videoTitle);
                int pos = Integer.parseInt(txtName.getTag().toString());
                paymentList.get(pos).name = videoTitle;
            } else {
                txtName.setText(" ");
            }
        }
    }
}
