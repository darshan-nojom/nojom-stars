package com.nojom.adapter;

import static com.nojom.util.YouTubeTitleFetcher.fetchVideoTitle;
import static com.nojom.util.YouTubeTitleFetcher.getVideoId;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.textview.TextViewSFTextPro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemYoutubeProfileBinding;
import com.nojom.model.GetYoutube;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileYoutubeAdapter extends RecyclerView.Adapter<ProfileYoutubeAdapter.SimpleViewHolder> {

    private BaseActivity context;
    //    public String path;
    private List<GetYoutube.Data> paymentList;
//    private OnClickListener onClickListener;

    public GetYoutube.Data getData(int pos) {
        return paymentList.get(pos);
    }

    public ProfileYoutubeAdapter(BaseActivity context) {
        this.context = context;
//        onClickListener = listener;
//        this.onClickPlatformListener = updatelistener;
    }

    public void doRefresh(List<GetYoutube.Data> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

//    public void doRefresh(String path) {
//        this.path = path;
//    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemYoutubeProfileBinding itemAccountBinding = ItemYoutubeProfileBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        GetYoutube.Data item = paymentList.get(position);

        String youtubeId = extractYTId(item.link);
        String videothumb = getYoutubeThumbnailUrl(youtubeId);
//        new FetchVideoTitleTask(holder.binding.txtMainPhoto).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.link);

//        String youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"; // Example URL
        String videoId = getVideoId(item.link);

        if (videoId != null) {
            String title = fetchVideoTitle(videoId);
            holder.binding.txtMainPhoto.setText(title);
        } else {
            System.out.println("Invalid YouTube URL");
            holder.binding.txtMainPhoto.setText("----");
        }

        Glide.with(context).load(videothumb).placeholder(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.binding.imgPortfolio);

    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemYoutubeProfileBinding binding;

        SimpleViewHolder(ItemYoutubeProfileBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (context.language.equals("ar")) {
                context.setArFont(binding.txtMainPhoto, Constants.FONT_AR_MEDIUM);
            }
            itemView.getRoot().setOnClickListener(v -> {
                if (!TextUtils.isEmpty(paymentList.get(getAdapterPosition()).link)) {
                    try {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentList.get(getAdapterPosition()).link));
//                        context.startActivity(intent);
                        Intent intent;
                        if (!paymentList.get(getAdapterPosition()).link.startsWith("http")) {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + paymentList.get(getAdapterPosition()).link));
                        } else {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentList.get(getAdapterPosition()).link));
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.google.android.youtube"); // Open in YouTube app if installed

                        // Check if YouTube app is available
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        } else {
                            // Open in a web browser if YouTube app is not available
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentList.get(getAdapterPosition()).link));
                            context.startActivity(webIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public interface OnClickListener {
        void onClickShow(GetYoutube.Data companies, int pos);

        void onClickMenu(GetYoutube.Data companies, int pos, View view, int loc);
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String videoTitle) {
            if (videoTitle != null) {
                txtName.setText(videoTitle);
            } else {
                txtName.setText("------");
            }
        }
    }
}
