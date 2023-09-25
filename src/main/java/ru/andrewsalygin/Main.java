package ru.andrewsalygin;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import ru.andrewsalygin.graph.game.Game;

import java.io.File;

/**
 * @author Andrew Salygin
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", String.valueOf(new File(System.getProperty("user.dir"), "/libs/slick2d")));

        try {
            AppGameContainer app = new AppGameContainer(new Game());
            app.setDisplayMode(800, 600, false);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}