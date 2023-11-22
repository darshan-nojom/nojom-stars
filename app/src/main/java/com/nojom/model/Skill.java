package com.nojom.model;

import java.io.Serializable;

public class Skill implements Serializable {

    public String skillTitle;
    public String skillValue;
    public int skillId;
    public boolean isSelected;

    public Skill(String skillTitle, String skillValue) {
        this.skillTitle = skillTitle;
        this.skillValue = skillValue;
    }

    public Skill(int skillId, String skillTitle, String skillValue) {
        this.skillId = skillId;
        this.skillTitle = skillTitle;
        this.skillValue = skillValue;
    }

    public Skill(int skillId, String skillTitle, String skillValue, boolean isSelected) {
        this.skillId = skillId;
        this.skillTitle = skillTitle;
        this.skillValue = skillValue;
        this.isSelected = isSelected;
    }
}
