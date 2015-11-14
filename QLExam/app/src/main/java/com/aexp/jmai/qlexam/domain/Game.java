package com.aexp.jmai.qlexam.domain;

public class Game {
    private final String icon_url;
    private final String name;

    public Game(final String url, final String name) {
        this.icon_url = url;
        this.name = name;
    }

    public String getIconUrl() {
        return icon_url;
    }

    public String getName() {
        return name;
    }
}
