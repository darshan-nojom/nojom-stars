package com.nojom.model;

public class Attachment {

    public String filepath;
    public boolean isImage;

    public Attachment(String filepath, boolean isImage) {
        this.filepath = filepath;
        this.isImage = isImage;
    }
}
