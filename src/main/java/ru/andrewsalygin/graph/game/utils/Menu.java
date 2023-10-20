package ru.andrewsalygin.graph.game.utils;

import org.newdawn.slick.SlickException;

// TO DO: Переименовать картинки
public enum Menu {
    RULES_MENU("src/main/resources/UI/infoMenuBig.png"),
    LONG_MENU("src/main/resources/UI/buttonLong.png"),
    BASIC_INFO_MENU("src/main/resources/UI/menu.png"),
    INFO_MENU("src/main/resources/UI/infoMenu.png"),
    MESSAGE_CLOUD("src/main/resources/UI/message.png");
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
