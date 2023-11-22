package com.nojom.multitypepicker.filter;

import androidx.fragment.app.FragmentActivity;

import com.nojom.multitypepicker.filter.callback.FileLoaderCallbacks;
import com.nojom.multitypepicker.filter.callback.FilterResultCallback;
import com.nojom.multitypepicker.filter.entity.AudioFile;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.multitypepicker.filter.entity.VideoFile;

import static com.nojom.multitypepicker.filter.callback.FileLoaderCallbacks.TYPE_AUDIO;
import static com.nojom.multitypepicker.filter.callback.FileLoaderCallbacks.TYPE_FILE;
import static com.nojom.multitypepicker.filter.callback.FileLoaderCallbacks.TYPE_IMAGE;
import static com.nojom.multitypepicker.filter.callback.FileLoaderCallbacks.TYPE_VIDEO;

public class FileFilter {
    public static void getImages(FragmentActivity activity, FilterResultCallback<ImageFile> callback){
        activity.getSupportLoaderManager().initLoader(0, null,
                new FileLoaderCallbacks(activity, callback, TYPE_IMAGE));
    }

    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback){
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, TYPE_VIDEO));
    }

    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback){
        activity.getSupportLoaderManager().initLoader(2, null,
                new FileLoaderCallbacks(activity, callback, TYPE_AUDIO));
    }

    public static void getFiles(FragmentActivity activity,
                                FilterResultCallback<NormalFile> callback, String[] suffix){
        activity.getSupportLoaderManager().initLoader(3, null,
                new FileLoaderCallbacks(activity, callback, TYPE_FILE, suffix));
    }
}
