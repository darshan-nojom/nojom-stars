package com.nojom.multitypepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.multitypepicker.Util;
import com.nojom.multitypepicker.filter.entity.AudioFile;
import com.nojom.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class AudioPickAdapter extends BaseAdapter<AudioFile, AudioPickAdapter.AudioPickViewHolder> {
    private int mMaxNumber;
    private int mCurrentNumber = 0;

    public AudioPickAdapter(Context ctx, int max) {
        this(ctx, new ArrayList<>(), max);
    }

    private AudioPickAdapter(Context ctx, ArrayList<AudioFile> list, int max) {
        super(ctx, list);
        mMaxNumber = max;
    }

    @NonNull
    @Override
    public AudioPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_audio_pick, parent, false);
        return new AudioPickViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AudioPickViewHolder holder, final int position) {
        final AudioFile file = mList.get(position);

        holder.mTvTitle.setText(file.getName());
        holder.mTvTitle.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (holder.mTvTitle.getMeasuredWidth() >
                Util.getScreenWidth(mContext) - Util.dip2px(mContext, 10 + 32 + 10 + 48 + 10 * 2)) {
            holder.mTvTitle.setLines(2);
        } else {
            holder.mTvTitle.setLines(1);
        }

        holder.mTvDuration.setText(Util.getDurationString(file.getDuration()));
        if (file.isSelected()) {
            holder.mCbx.setSelected(true);
        } else {
            holder.mCbx.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class AudioPickViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private TextView mTvDuration;
        private ImageView mCbx;

        AudioPickViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_audio_title);
            mTvDuration = itemView.findViewById(R.id.tv_duration);
            mCbx = itemView.findViewById(R.id.cbx);

            mCbx.setOnClickListener(v -> {
                if (!v.isSelected() && isUpToMax()) {
                    Utils.toastMessage((Activity) mContext, mContext.getString(R.string.vw_up_to_max));
                    return;
                }

                if (v.isSelected()) {
                    mCbx.setSelected(false);
                    mCurrentNumber--;
                } else {
                    mCbx.setSelected(true);
                    mCurrentNumber++;
                }

                mList.get(getAdapterPosition()).setSelected(mCbx.isSelected());

                if (mListener != null) {
                    mListener.OnSelectStateChanged(mCbx.isSelected(), mList.get(getAdapterPosition()));
                }
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    File f = new File(mList.get(getAdapterPosition()).getPath());
                    uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", f);
                } else {
                    uri = Uri.parse("file://" + mList.get(getAdapterPosition()).getPath());
                }
                intent.setDataAndType(uri, "audio/mp3");
                if (Util.detectIntent(mContext, intent)) {
                    mContext.startActivity(intent);
                } else {
                    Utils.toastMessage((Activity) mContext, mContext.getString(R.string.vw_no_audio_play_app));
                }
            });
        }
    }

    public boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    public void setCurrentNumber(int number) {
        mCurrentNumber = number;
    }
}
