package com.nojom.multitypepicker.filter.callback;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.nojom.multitypepicker.Util;
import com.nojom.multitypepicker.filter.entity.AudioFile;
import com.nojom.multitypepicker.filter.entity.Directory;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.multitypepicker.filter.entity.VideoFile;
import com.nojom.multitypepicker.filter.loader.AudioLoader;
import com.nojom.multitypepicker.filter.loader.FileLoader;
import com.nojom.multitypepicker.filter.loader.ImageLoader;
import com.nojom.multitypepicker.filter.loader.VideoLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.Images.ImageColumns.ORIENTATION;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;
import static android.provider.MediaStore.Video.VideoColumns.DURATION;

public class FileLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_FILE = 3;

    private WeakReference<Context> context;
    private FilterResultCallback resultCallback;

    private int mType;
    private CursorLoader mLoader;
    private String mSuffixRegex;

    public FileLoaderCallbacks(Context context, FilterResultCallback resultCallback, int type) {
        this(context, resultCallback, type, null);
    }

    public FileLoaderCallbacks(Context context, FilterResultCallback resultCallback, int type, String[] suffixArgs) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
        this.mType = type;
        if (suffixArgs != null && suffixArgs.length > 0) {
            mSuffixRegex = obtainSuffixRegex(suffixArgs);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mType) {
            case TYPE_IMAGE:
                mLoader = new ImageLoader(context.get());
                break;
            case TYPE_VIDEO:
                mLoader = new VideoLoader(context.get());
                break;
            case TYPE_AUDIO:
                mLoader = new AudioLoader(context.get());
                break;
            case TYPE_FILE:
                mLoader = new FileLoader(context.get());
                break;
        }

        return mLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (mType) {
            case TYPE_IMAGE:
                onImageResult(data);
                break;
            case TYPE_VIDEO:
                onVideoResult(data);
                break;
            case TYPE_AUDIO:
                onAudioResult(data);
                break;
            case TYPE_FILE:
                onFileResult(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @SuppressWarnings("unchecked")
    private void onImageResult(Cursor data) {
        try {

            List<Directory<ImageFile>> directories = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                ImageFile img = new ImageFile();
                img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                    img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                    img.setOrientation(data.getInt(data.getColumnIndexOrThrow(ORIENTATION)));
                }

                img.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));


                //Create a Directory
                Directory<ImageFile> directory = new Directory<>();
                directory.setId(img.getBucketId());
                directory.setName(img.getBucketName());
                directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                if (!directories.contains(directory)) {
                    directory.addFile(img);
                    directories.add(directory);
                } else {
                    directories.get(directories.indexOf(directory)).addFile(img);
                }
            }

//            data.close();

            if (resultCallback != null) {
                resultCallback.onResult(directories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void onVideoResult(final Cursor data) {
        try {
            List<Directory<VideoFile>> directories = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                VideoFile video = new VideoFile();
                video.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                video.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                video.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                video.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    video.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                    video.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                    video.setDuration(data.getLong(data.getColumnIndexOrThrow(DURATION)));
                }

                video.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

                //Create a Directory
                Directory<VideoFile> directory = new Directory<>();
                directory.setId(video.getBucketId());
                directory.setName(video.getBucketName());
                directory.setPath(Util.extractPathWithoutSeparator(video.getPath()));

                if (!directories.contains(directory)) {
                    directory.addFile(video);
                    directories.add(directory);
                } else {
                    directories.get(directories.indexOf(directory)).addFile(video);
                }
            }

//            data.close();

            if (resultCallback != null) {
                resultCallback.onResult(directories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void onAudioResult(Cursor data) {
        try {

            List<Directory<AudioFile>> directories = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                AudioFile audio = new AudioFile();
                audio.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                audio.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                audio.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                audio.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                audio.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    audio.setDuration(data.getLong(data.getColumnIndexOrThrow(DURATION)));
                }

                //Create a Directory
                Directory<AudioFile> directory = new Directory<>();
                directory.setName(Util.extractFileNameWithSuffix(Util.extractPathWithoutSeparator(audio.getPath())));
                directory.setPath(Util.extractPathWithoutSeparator(audio.getPath()));

                if (!directories.contains(directory)) {
                    directory.addFile(audio);
                    directories.add(directory);
                } else {
                    directories.get(directories.indexOf(directory)).addFile(audio);
                }
            }

//            data.close();

            if (resultCallback != null) {
                resultCallback.onResult(directories);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void onFileResult(Cursor data) {
        try {
            List<Directory<NormalFile>> directories = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                if (path != null && contains(path)) {
                    //Create a File instance
                    NormalFile file = new NormalFile();
                    file.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                    file.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                    file.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                    file.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                    file.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

                    file.setMimeType(data.getString(data.getColumnIndexOrThrow(MIME_TYPE)));

                    //Create a Directory
                    Directory<NormalFile> directory = new Directory<>();
                    directory.setName(Util.extractFileNameWithSuffix(Util.extractPathWithoutSeparator(file.getPath())));
                    directory.setPath(Util.extractPathWithoutSeparator(file.getPath()));

                    if (!directories.contains(directory)) {
                        directory.addFile(file);
                        directories.add(directory);
                    } else {
                        directories.get(directories.indexOf(directory)).addFile(file);
                    }
                }
            }

//            data.close();

            if (resultCallback != null) {
                resultCallback.onResult(directories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean contains(String path) {
        String name = Util.extractFileNameWithSuffix(path);
        Pattern pattern = Pattern.compile(mSuffixRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private String obtainSuffixRegex(String[] suffixes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < suffixes.length; i++) {
            if (i == 0) {
                builder.append(suffixes[i].replace(".", ""));
            } else {
                builder.append("|\\.");
                builder.append(suffixes[i].replace(".", ""));
            }
        }
        return ".+(\\." + builder.toString() + ")$";
    }
}
