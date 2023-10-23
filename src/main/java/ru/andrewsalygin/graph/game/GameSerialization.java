package ru.andrewsalygin.graph.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.newdawn.slick.GameContainer;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;

import static ru.andrewsalygin.graph.game.Game.visualGraph;

public class GameSerialization {
    private final static Logger LOGGER = LogManager.getLogger();
    public static void saveGameToFile(VisualGraph graph) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        fileChooser.setSelectedFile(new File("save.slg"));
        fileChooser.setDialogTitle("Choose a location and specify the file name");

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                FileOutputStream outputStream = new FileOutputStream(selectedFile);
                ObjectOutputStream out = new ObjectOutputStream(outputStream);
                out.writeObject(graph);

                out.close();
                outputStream.close();
                LOGGER.info("Game saved to: " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.error("File save operation canceled.");
        }
    }

    public static VisualGraph openGameFromFile(GameContainer gc) {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setDialogTitle("Choose a file to open");

        int returnValue = fileChooser.showOpenDialog(null);

        VisualGraph graph = new VisualGraph();
        boolean isNotOpen = true;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                FileInputStream fileIn = new FileInputStream(selectedFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                graph = (VisualGraph) in.readObject();
                isNotOpen = false;
                in.close();
                fileIn.close();
                LOGGER.info("Opened " + selectedFile.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.error("File open operation canceled.");
        }

        if (isNotOpen) {
            // Берём левый верхний угол клетчатой сетки
            int x = (gc.getWidth() - visualGraph.gridWidth) / 2;
            int y = (gc.getHeight() - visualGraph.gridHeight) / 2;

            // Распределяем компоненты случайным образом по игровому полю
            visualGraph.randomizeComponentPlacements(x, y);

            // Соедините компоненты связности между собой, добавив рёбра
            visualGraph.connectComponents();

            // Больше отдельные компоненты не нужны
            visualGraph.setComponents(null);
        }

        return graph;
    }
}
