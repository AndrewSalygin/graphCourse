package ru.andrewsalygin.graph.game.utils;

import org.newdawn.slick.SlickException;

public enum Button {
    PLUS("src/main/resources/UI/PLUS.png"),
    PLUS_HOVERED("src/main/resources/UI/PLUS_HOVERED.png"),
    YES_BUTTON("src/main/resources/UI/YES_BUTTON.png"),
    YES_BUTTON_HOVERED("src/main/resources/UI/YES_BUTTON_HOVERED.png"),
    NO_BUTTON("src/main/resources/UI/NO_BUTTON.png"),
    NO_BUTTON_HOVERED("src/main/resources/UI/NO_BUTTON_HOVERED.png"),
    HELP_BUTTON("src/main/resources/UI/HELP_BUTTON.png"),
    HELP_BUTTON_HOVERED("src/main/resources/UI/HELP_BUTTON_HOVERED.png"),
    HOME_BUTTON("src/main/resources/UI/HOME_BUTTON.png"),
    HOME_BUTTON_HOVERED("src/main/resources/UI/HOME_BUTTON_HOVERED.png"),
    REPEAT_BUTTON("src/main/resources/UI/REPEAT_BUTTON.png"),
    REPEAT_BUTTON_HOVERED("src/main/resources/UI/REPEAT_BUTTON_HOVERED.png"),
    SAVE_BUTTON("src/main/resources/UI/SAVE_BUTTON.png"),
    SAVE_BUTTON_HOVERED("src/main/resources/UI/SAVE_BUTTON_HOVERED.png"),
    OPEN_SAVING_BUTTON("src/main/resources/UI/OPEN_SAVING_BUTTON.png"),
    OPEN_SAVING_BUTTON_HOVERED("src/main/resources/UI/OPEN_SAVING_BUTTON_HOVERED.png"),
    REGULAR_BUTTON("src/main/resources/UI/REGULAR_BUTTON.png"), // 200x50
    REGULAR_BUTTON_HOVERED("src/main/resources/UI/REGULAR_BUTTON_HOVERED.png"),
    BUTTON_ALL_VIRUS_MOVE("src/main/resources/UI/BUTTON_ALL_VIRUS_MOVE.png"),
    BUTTON_ALL_VIRUS_MOVE_HOVERED("src/main/resources/UI/BUTTON_ALL_VIRUS_MOVE_HOVERED.png"),
    BUTTON_HALF_VIRUS_MOVE("src/main/resources/UI/BUTTON_HALF_VIRUS_MOVE.png"),
    BUTTON_HALF_VIRUS_MOVE_HOVERED("src/main/resources/UI/BUTTON_HALF_VIRUS_MOVE_HOVERED.png"),
    BUTTON_QUARTER_VIRUS_MOVE("src/main/resources/UI/BUTTON_QUARTER_VIRUS_MOVE.png"),
    BUTTON_QUARTER_VIRUS_MOVE_HOVERED("src/main/resources/UI/BUTTON_QUARTER_VIRUS_MOVE_HOVERED.png");
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
