package com.nojom.multitypepicker.filter.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageFile extends BaseFile implements Parcelable {
    private int orientation, isServerUrl;   //0, 90, 180, 270
    public boolean isSelected;


    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getIsServerUrl() {
        return isServerUrl;
    }

    public void setIsServerUrl(int isServerUrl) {
        this.isServerUrl = isServerUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeLong(getSize());
        dest.writeString(getBucketId());
        dest.writeString(getBucketName());
        dest.writeLong(getDate());
        dest.writeByte((byte) (isSelected() ? 1 : 0));
        dest.writeInt(orientation);
        dest.writeInt(isServerUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageFile> CREATOR = new Creator<ImageFile>() {
        @Override
        public ImageFile[] newArray(int size) {
            return new ImageFile[size];
        }

        @Override
        public ImageFile createFromParcel(Parcel in) {
            ImageFile file = new ImageFile();
            file.setId(in.readLong());
            file.setName(in.readString());
            file.setPath(in.readString());
            file.setSize(in.readLong());
            file.setBucketId(in.readString());
            file.setBucketName(in.readString());
            file.setDate(in.readLong());
            file.setSelected(in.readByte() != 0);
            file.setOrientation(in.readInt());
            file.setIsServerUrl(in.readInt());
            return file;
        }
    };
}
