package com.github.sneakytowelsuit.purerules.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
public class Preferences {
    private Boolean darkMode;
    private String secondaryUser;
    public Preferences() {}
    public Preferences(Boolean darkMode, String secondaryUser) {
        this.darkMode = darkMode;
        this.secondaryUser = secondaryUser;
    }
}
