package ru.andrewsalygin.graph.game.utils;

import org.newdawn.slick.SlickException;

// TO DO: Переименовать картинки
public enum Menu {
    RULES_MENU("src/main/resources/UI/RULES_MENU.png"),
    LONG_MENU("src/main/resources/UI/LONG_MENU.png"),
    BASIC_INFO_MENU("src/main/resources/UI/BASIC_INFO_MENU.png"),
    INFO_MENU("src/main/resources/UI/INFO_MENU.png"),
    MESSAGE_CLOUD("src/main/resources/UI/MESSAGE_CLOUD.png");
    private org.newdawn.slick.Image image;

    public org.newdawn.slick.Image getImage() {
        return image;
    }

    Menu(String s) {
        try {
            image = new org.newdawn.slick.Image(s);
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }
}
