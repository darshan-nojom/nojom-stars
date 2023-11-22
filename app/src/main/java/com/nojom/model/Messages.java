package com.nojom.model;

import java.io.Serializable;
import java.util.Date;

public class Messages implements Serializable {

    public String message;
    public Date date;
    public String id;
    public String fileName;
    public String type;  // text, image, video, file
    public boolean seen;
    public boolean isDayChange;

    public Messages() {
    }
}
