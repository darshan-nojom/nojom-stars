package com.nojom.model;

public class CampFile {

    public String filepath, fileName;
    public boolean isImage;
    public boolean isLink = false;
    public boolean isUploaded = false;
    public long fileSize;

    public CampFile(String filepath, String fileName, long fileSize, boolean isImage) {
        this.filepath = filepath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.isImage = isImage;
    }

    public CampFile() {

    }
}
