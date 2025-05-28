package com.github.sneakytowelsuit.sample;

public class Preferences {
    private Boolean darkMode;
    private String secondaryUser;
    public Preferences() {}
    public Preferences(Boolean darkMode, String secondaryUser) {
        this.darkMode = darkMode;
        this.secondaryUser = secondaryUser;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void setSecondaryUser(String secondaryUser) {
        this.secondaryUser = secondaryUser;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }
    public String getSecondaryUser() {
        return secondaryUser;
    }
}
