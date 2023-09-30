package ru.andrewsalygin;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
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
            app.setDisplayMode(1920, 1080, true);
            app.setTargetFrameRate(60); // Устанавливаем желаемый FPS
            app.setVSync(true); // Включаем вертикальную синхронизацию
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}