package com.wheezygold.happybot.util;

public enum Constants {

    GUILD_ID("237363812842340363"),
    OWNER_ID("194473148161327104"),
    HAPPYHEART_TWITTER_ID("865017489213673472");

    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }

}