package ru.andrewsalygin.graph.game.utils;

import org.newdawn.slick.SlickException;

public enum Button {
    PLUS("src/main/resources/UI/plus.png"),
    PLUS_HOVERED("src/main/resources/UI/plusHighlighted.png"),
    YES_BUTTON("src/main/resources/UI/yesButton.png"),
    YES_BUTTON_HOVERED("src/main/resources/UI/yesButtonHighlighted.png"),
    NO_BUTTON("src/main/resources/UI/noButton.png"),
    NO_BUTTON_HOVERED("src/main/resources/UI/noButtonHighlighted.png"),
    HELP_BUTTON("src/main/resources/UI/help.png"),
    HELP_BUTTON_HOVERED("src/main/resources/UI/helpHighlighted.png"),
    HOME_BUTTON("src/main/resources/UI/home.png"),
    HOME_BUTTON_HOVERED("src/main/resources/UI/homeHighlighted.png"),
    REPEAT_BUTTON("src/main/resources/UI/repeat.png"),
    REPEAT_BUTTON_HOVERED("src/main/resources/UI/repeatHighlighted.png"),
    REGULAR_BUTTON("src/main/resources/UI/button.png"),
    REGULAR_BUTTON_HOVERED("src/main/resources/UI/buttonHighlighted.png"),
    BUTTON_ALL_VIRUS_MOVE("src/main/resources/UI/oneButton.png"),
    BUTTON_ALL_VIRUS_MOVE_HOVERED("src/main/resources/UI/oneButtonHighlighted.png"),
    BUTTON_HALF_VIRUS_MOVE("src/main/resources/UI/innerButton12.png"),
    BUTTON_HALF_VIRUS_MOVE_HOVERED("src/main/resources/UI/innerButtonHighlighted12.png"),
    BUTTON_QUARTER_VIRUS_MOVE("src/main/resources/UI/innerButton14.png"),
    BUTTON_QUARTER_VIRUS_MOVE_HOVERED("src/main/resources/UI/innerButtonHighlighted14.png");
    private org.newdawn.slick.Image image;

    public org.newdawn.slick.Image getImage() {
        return image;
    }

    Button(String s) {
        try {
            image = new org.newdawn.slick.Image(s);
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }
}
