package com.nojom.model;

import java.io.Serializable;

public class ProfileMenu implements Serializable {
    public String menuName;
    public int id;
    public boolean isShow;

    public ProfileMenu(String menuName, int id) {
        this.menuName = menuName;
        this.id = id;
    }
}
