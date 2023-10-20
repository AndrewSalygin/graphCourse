package ru.andrewsalygin.graph.game;

import org.newdawn.slick.*;

import org.newdawn.slick.font.effects.ColorEffect;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.utils.*;
import ru.andrewsalygin.graph.game.visualgraph.VisualConnection;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.*;

import static java.lang.System.exit;
import static ru.andrewsalygin.graph.game.GameLogic.*;
import static ru.andrewsalygin.graph.game.utils.Menu.*;
import static ru.andrewsalygin.graph.game.utils.Button.*;
import static ru.andrewsalygin.graph.game.utils.Flag.*;

// TO DO: ПОДПРАВИТЬ КООРДИНАТЫ ДЛЯ UI
public class Game extends BasicGame {
    // Размеры таблицы
    public static int cellSize; // Размер каждой ячейки
    public static int nodeRadius; // Размер каждой ячейки
    private int tableScale;
    private HashMap<Flag, Boolean> flags;
    Image backgroundImage;
    static VisualGraph visualGraph;
    private VisualNode highlightedNode;
    private final Color highlightColor = Color.yellow;
    private MotionError errorMotion;
    final static int minValueRegenerationHealthNode = 1;
    final static int maxValueRegenerationHealthNode = 25;
    private UnicodeFont font;
    private UnicodeFont fontMessage;
    private UnicodeFont fontBold;
    private VisualNode startVirusMove;
    private VisualNode endVirusMove;
    static HashMap<VisualNode, HashMap<Node, Connection>> greenGraph;
    static HashMap<VisualNode, HashMap<Node, Connection>> blueGraph;
    static HashMap<Node, HashMap<Node, Connection>> redGraph;
    private static final Random random = new Random();
    EndGameWin endGameWin;
    RestartGame restartGame;

    public Game() {
        super("Infection Graph");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        flags = new HashMap<>();
        for (Flag flag : Flag.values()) {
            flags.put(flag, false);
        }
        startVirusMove = null;
        endVirusMove = null;
        highlightedNode = null;

        endGameWin = EndGameWin.NONE;
        restartGame = RestartGame.NONE;

        greenGraph = new HashMap<>();
        blueGraph = new HashMap<>();

        font = new UnicodeFont("src/main/resources/fonts/better-vcr.ttf", 16, false, false);
        fontMessage = new UnicodeFont("src/main/resources/fonts/better-vcr.ttf", 10, false, false);
        fontBold = new UnicodeFont("src/main/resources/fonts/better-vcr.ttf", 16, true, false);

        // Устанавливаем эффекты для шрифта
        font.getEffects().add(new ColorEffect(java.awt.Color.black)); // Цвет текста
        fontMessage.getEffects().add(new ColorEffect(java.awt.Color.black)); // Цвет текста
        fontBold.getEffects().add(new ColorEffect(java.awt.Color.black)); // Цвет текста

        // Установка эффекта для сглаживания текста
        font.getEffects().add(new ColorEffect(java.awt.Color.black));
        fontMessage.getEffects().add(new ColorEffect(java.awt.Color.black));
        fontBold.getEffects().add(new ColorEffect(java.awt.Color.black));

        // Инициализируем шрифт
        font.addAsciiGlyphs();
        font.loadGlyphs();

        fontMessage.addAsciiGlyphs();
        fontMessage.loadGlyphs();

        fontBold.addAsciiGlyphs();
        fontBold.loadGlyphs();

        gc.setShowFPS(false); // Скрыть отображение FPS
        backgroundImage = new Image("/src/main/resources/backgrounds/background4.png");

        tableScale = 25;
        cellSize = gc.getHeight() / tableScale;
        nodeRadius = cellSize / 3;

        visualGraph = new VisualGraph();

        // Берём левый верхний угол клетчатой сетки
        int x = (gc.getWidth() - visualGraph.gridWidth) / 2;
        int y = (gc.getHeight() - visualGraph.gridHeight) / 2;

        // Распределяем компоненты случайным образом по игровому полю
        visualGraph.randomizeComponentPlacements(x, y);

        // Соедините компоненты связности между собой, добавив рёбра
        visualGraph.connectComponents();

        // Больше отдельные компоненты не нужны
        visualGraph.setComponents(null);

        GameLogic.startGame();
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Input input = gc.getInput();

        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();

        highlightedNode = null;
        flags.put(HIGHLIGHT_NODE, false);
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode tmpNode = (VisualNode) entry.getKey();
            if (tmpNode.contains(mouseX, mouseY)) {
                highlightedNode = tmpNode;
                flags.put(HIGHLIGHT_NODE, true);
                break;
            }
        }
        if (endGameWin == EndGameWin.NONE) {
            if (motion.equals("Green move")) {
                if (mouseX >= (20 + gc.getWidth() / 3 - 190) / 2 - 100 && mouseX <= (20 + gc.getWidth() / 3 - 190) / 2 - 100 + 200 &&
                        mouseY >= 230 && mouseY <= 280 && greenGraph.size() != 0) {
                    flags.put(HIGHLIGHT_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_BUTTON, false);
                }
                if (mouseX >= 430 && mouseX <= 446 && mouseY >= 119 && mouseY <= 135 && skillPoints[0] > 0) {
                    flags.put(HIGHLIGHT_POWER_INCREASE_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_POWER_INCREASE_BUTTON, false);
                }
                if (mouseX >= 430 && mouseX <= 446 && mouseY >= 149 && mouseY <= 165 && skillPoints[0] > 0) {
                    flags.put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, false);
                }
                if (mouseX >= 430 && mouseX <= 446 && mouseY >= 179 && mouseY <= 195 && skillPoints[0] > 0) {
                    flags.put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, false);
                }
            } else if (motion.equals("Blue move")) {
                if (mouseX >= gc.getWidth() - 350 && mouseX <= gc.getWidth() - 350 + 200 &&
                        mouseY >= 230 && mouseY <= 280 && blueGraph.size() != 0) {
                    flags.put(HIGHLIGHT_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_BUTTON, false);
                }
                if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 119 && mouseY <= 135
                        && skillPoints[1] > 0) {
                    flags.put(HIGHLIGHT_POWER_INCREASE_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_POWER_INCREASE_BUTTON, false);
                }
                if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 149 && mouseY <= 165
                        && skillPoints[1] > 0) {
                    flags.put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, false);
                }
                if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 179 && mouseY <= 195
                        && skillPoints[1] > 0) {
                    flags.put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, true);
                } else {
                    flags.put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, false);
                }
            }

            if (flags.get(SELECTED_NODE_TO_MOVE_VIRUS) && mouseX >= gc.getWidth() / 2 - 115 && mouseX <= gc.getWidth() / 2 - 40 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                flags.put(HIGHLIGHT_SEND_ALL_VIRUS, true);
            } else {
                flags.put(HIGHLIGHT_SEND_ALL_VIRUS, false);
            }
            if (flags.get(SELECTED_NODE_TO_MOVE_VIRUS) && mouseX >= gc.getWidth() / 2 - 38 && mouseX <= gc.getWidth() / 2 + 37 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                flags.put(HIGHLIGHT_SEND_HALF_VIRUS, true);
            } else {
                flags.put(HIGHLIGHT_SEND_HALF_VIRUS, false);
            }
            if (flags.get(SELECTED_NODE_TO_MOVE_VIRUS) && mouseX >= gc.getWidth() / 2 + 39 && mouseX <= gc.getWidth() / 2 + 114 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                flags.put(HIGHLIGHT_SEND_QUARTER_VIRUS, true);
            } else {
                flags.put(HIGHLIGHT_SEND_QUARTER_VIRUS, false);
            }
        } else {
            if (mouseX >= gc.getWidth() / 2 - 79 && mouseX <= gc.getWidth() / 2 - 4 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                flags.put(HIGHLIGHT_YES_BUTTON, true);
            } else {
                flags.put(HIGHLIGHT_YES_BUTTON, false);
            }
            if (mouseX >= gc.getWidth() / 2 + 1 && mouseX <= gc.getWidth() / 2 + 76 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                flags.put(HIGHLIGHT_NO_BUTTON, true);
            } else {
                flags.put(HIGHLIGHT_NO_BUTTON, false);
            }
        }
        // Конец игры
        if (day != 1) {
            if (blueGraph.size() == 0 && greenGraph.size() == 0) {
                endGameWin = EndGameWin.RED;
            } else if (blueGraph.size() == 0) {
                endGameWin = EndGameWin.GREEN;
            } else if (greenGraph.size() == 0) {
                endGameWin = EndGameWin.BLUE;
            }
        }
        if (mouseX >= 1350 && mouseX <= 1400 && mouseY >= 20 && mouseY <= 70) {
            flags.put(HIGHLIGHT_HOME_BUTTON, true);
        } else {
            flags.put(HIGHLIGHT_HOME_BUTTON, false);
        }
        if (mouseX >= 520 && mouseX <= 570 && mouseY >= 20 && mouseY <= 70) {
            flags.put(HIGHLIGHT_REPEAT_BUTTON, true);
        } else {
            flags.put(HIGHLIGHT_REPEAT_BUTTON, false);
        }
        if (mouseX >= 1350 && mouseX <= 1400 && mouseY >= gc.getHeight() - 80 && mouseY <= gc.getHeight() - 30) {
            flags.put(HIGHLIGHT_HELP_BUTTON, true);
        } else {
            flags.put(HIGHLIGHT_HELP_BUTTON, false);
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (endGameWin == EndGameWin.NONE) {
            if (button == Input.MOUSE_LEFT_BUTTON && flags.get(HIGHLIGHT_BUTTON)) {
                if (motion.equals("Green move")) {
                    motion = "Blue move";
                } else {
                    motion = "Green move";
                    day++;
                    List<VisualNode> nodesToDelete = new ArrayList<>();
                    for (Map.Entry<VisualNode, HashMap<Node, Connection>> entry : greenGraph.entrySet()) {
                        protectionHealthNode = random.nextInt(maxValueRegenerationHealthNode - minValueRegenerationHealthNode + 1) + minValueRegenerationHealthNode;
                        int prev_hp = entry.getKey().getHp();
                        int new_hp = prev_hp + replications[0] + protections[0] - protectionHealthNode;
                        if (new_hp > 0 && new_hp <= 300) {
                            entry.getKey().setHp(new_hp);
                        } else if (new_hp <= 0) {
                            nodesToDelete.add(entry.getKey());
                            entry.getKey().setHp(Math.abs(new_hp));
                            entry.getKey().setEllipseColor(new Color(163, 0, 0));
                            redGraph.put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (VisualNode node : nodesToDelete) {
                        greenGraph.remove(node);
                    }
                    nodesToDelete = new ArrayList<>();
                    for (Map.Entry<VisualNode, HashMap<Node, Connection>> entry : blueGraph.entrySet()) {
                        protectionHealthNode = random.nextInt(maxValueRegenerationHealthNode - minValueRegenerationHealthNode + 1) + minValueRegenerationHealthNode;
                        int prev_hp = entry.getKey().getHp();
                        int new_hp = prev_hp + replications[1] + protections[1] - protectionHealthNode;
                        if (new_hp > 0 && new_hp <= 300) {
                            entry.getKey().setHp(new_hp);
                        } else if (new_hp <= 0) {
                            nodesToDelete.add(entry.getKey());
                            entry.getKey().setHp(Math.abs(new_hp));
                            entry.getKey().setEllipseColor(new Color(163, 0, 0));
                            redGraph.put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (VisualNode node : nodesToDelete) {
                        blueGraph.remove(node);
                    }

                    // Регенерация хп у здоровых нод
                    for (Map.Entry<Node, HashMap<Node, Connection>> entry : redGraph.entrySet()) {
                        replicationHealthNode = random.nextInt(maxValueRegenerationHealthNode - minValueRegenerationHealthNode + 1) + minValueRegenerationHealthNode;
                        VisualNode node = (VisualNode) entry.getKey();
                        int prev_hp = node.getHp();
                        int new_hp = prev_hp + replicationHealthNode;
                        if (new_hp <= random.nextInt(99 - 50 + 1) + 50) {
                            node.setHp(new_hp);
                        }
                    }
                }
            } else if (day == 1 && button == Input.MOUSE_LEFT_BUTTON && flags.get(HIGHLIGHT_NODE)) {
                if (motion.equals("Green move") && greenGraph.size() == 0) {
                    highlightedNode.setEllipseColor(Color.green);
                    greenGraph.put(highlightedNode, visualGraph.getGraph().get(highlightedNode));
                    redGraph.remove(highlightedNode);
                    skillPoints[0]++;
                    highlightedNode.setHp(100);
                    highlightedNode.setSkillPoint(false);
                } else if (motion.equals("Blue move") && blueGraph.size() == 0) {
                    highlightedNode.setEllipseColor(Color.blue);
                    blueGraph.put(highlightedNode, visualGraph.getGraph().get(highlightedNode));
                    redGraph.remove(highlightedNode);
                    skillPoints[1]++;
                    highlightedNode.setHp(100);
                    highlightedNode.setSkillPoint(false);
                }
            } else if (button == Input.MOUSE_LEFT_BUTTON && (flags.get(HIGHLIGHT_SEND_ALL_VIRUS) || flags.get(HIGHLIGHT_SEND_HALF_VIRUS) || flags.get(HIGHLIGHT_SEND_QUARTER_VIRUS))) {
                flags.put(MOVE_VIRUS_MODE, true);
            } else if (flags.get(HIGHLIGHT_NODE) && flags.get(SELECTED_NODE_FROM_MOVE_VIRUS) && button == Input.MOUSE_LEFT_BUTTON) {
                flags.put(SELECTED_NODE_TO_MOVE_VIRUS, true);
                endVirusMove = highlightedNode;
            } else if (button == Input.MOUSE_LEFT_BUTTON && flags.get(HIGHLIGHT_NODE)) {
                flags.put(SELECTED_NODE_FROM_MOVE_VIRUS, true);
                startVirusMove = highlightedNode;
            } else if (button == Input.MOUSE_LEFT_BUTTON && flags.get(HIGHLIGHT_POWER_INCREASE_BUTTON)) {
                if (motion.equals("Green move")) {
                    GameLogic.powers[0] += powerDelta;
                    skillPoints[0]--;
                } else {
                    GameLogic.powers[1] += powerDelta;
                    skillPoints[1]--;
                }
            } else if (button == Input.MOUSE_LEFT_BUTTON && flags.get(HIGHLIGHT_PROTECTION_INCREASE_BUTTON)) {
                if (motion.equals("Green move")) {
                    GameLogic.protections[0] += protectionDelta;
                    skillPoints[0]--;
                } else {
                    GameLogic.protections[1] += protectionDelta;
                    skillPoints[1]--;
                }
            } else if (button == Input.MOUSE_LEFT_BUTTON && flags.get(HIGHLIGHT_REPLICATION_INCREASE_BUTTON)) {
                if (motion.equals("Green move")) {
                    GameLogic.replications[0] += replicationDelta;
                    skillPoints[0]--;
                } else {
                    GameLogic.replications[1] += replicationDelta;
                    skillPoints[1]--;
                }
            }
        } else {
            if (button == Input.MOUSE_LEFT_BUTTON) {
                if (flags.get(HIGHLIGHT_YES_BUTTON)) {
                    restartGame = RestartGame.YES;
                } else if (flags.get(HIGHLIGHT_NO_BUTTON)) {
                    restartGame = RestartGame.NO;
                }
            }
        }
        if (flags.get(HIGHLIGHT_HELP_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            if (flags.get(OPEN_HELP_MENU)) {
                flags.put(OPEN_HELP_MENU, false);
            } else {
                flags.put(OPEN_HELP_MENU, true);
            }
        }
        if (flags.get(HIGHLIGHT_REPEAT_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            flags.put(REPEAT_GAME, true);
        }
        if (flags.get(HIGHLIGHT_HOME_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            flags.put(EXIT_GAME, true);
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(new Color(76, 96, 133));
        g.fillRect(0, 0, gc.getWidth(), gc.getHeight());

        g.drawImage(backgroundImage, gc.getWidth() / 3 - 150, 0, gc.getWidth() * 2 / 3 + 150, gc.getHeight(), 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());

        g.setColor(Color.white);

        int gridWidth = visualGraph.cols * cellSize;
        int gridHeight = visualGraph.rows * cellSize;

        // Рассчитываем координаты начала отрисовки, чтобы разместить поле по центру экрана
        int x = (gc.getWidth() - gridWidth) / 2;
        int y = (gc.getHeight() - gridHeight) / 2;

        // Рисуем горизонтальные линии
        for (int row = 0; row <= visualGraph.rows; row++) {
            int lineY = y + row * cellSize;
            g.drawLine(x, lineY, x + gridWidth, lineY);
        }

        // Рисуем вертикальные линии
        for (int col = 0; col <= visualGraph.cols; col++) {
            int lineX = x + col * cellSize;
            g.drawLine(lineX, y, lineX, y + gridHeight);
        }

        // Отрисовка вершин
        g.setLineWidth(3.0f);
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode visualNode = (VisualNode) entry.getKey();
            if (visualNode.equals(highlightedNode)) {
                g.setColor(highlightColor); // Цвет подсветки
                g.drawString(String.valueOf(visualNode.getHp()), visualNode.getEllipse().getCenterX(), visualNode.getEllipse().getCenterY() - 30);
            } else {
                g.setColor(visualNode.getEllipseColor());
                g.drawString(String.valueOf(visualNode.getHp()), visualNode.getEllipse().getCenterX(), visualNode.getEllipse().getCenterY() - 30);
            }
            g.fill(visualNode.getEllipse());
            g.setColor(Color.black);
            g.drawOval(visualNode.getEllipse().getCenterX() - nodeRadius, visualNode.getEllipse().getCenterY() - nodeRadius, cellSize / 1.5f, cellSize / 1.5f);
        }

        // Отрисовка рёбер в компонентах
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            for (Map.Entry<Node, Connection> localEntry : entry.getValue().entrySet()) {
                VisualConnection vc = (VisualConnection) localEntry.getValue();
                g.setColor(vc.getColor());
                int startX = (int) vc.getSrcNode().getEllipse().getCenterX();
                int startY = (int) vc.getSrcNode().getEllipse().getCenterY();
                int endX = (int) vc.getDestNode().getEllipse().getCenterX();
                int endY = (int) vc.getDestNode().getEllipse().getCenterY();
                g.fillOval(startX - 5, startY - 5, 10, 10); // Точка на начале отрезка
                g.fillOval(endX - 5, endY - 5, 10, 10); // Точка на конце отрезка
                g.drawLine(startX, startY, endX, endY);
            }
        }

        // Отрисовка UI
        g.drawImage(BASIC_INFO_MENU.getImage(), 20, 20);
        g.drawImage(BASIC_INFO_MENU.getImage(), gc.getWidth() - 460, 20);
        g.drawImage(INFO_MENU.getImage(), 20, gc.getHeight() - 150);
        g.drawImage(INFO_MENU.getImage(), gc.getWidth() - 460, gc.getHeight() - 150);
        g.setFont(fontBold);

        // Дни
        g.drawImage(REGULAR_BUTTON_HOVERED.getImage(), gc.getWidth() / 2 - 100, 20);
        g.drawString("Day " + GameLogic.day, gc.getWidth() / 2 - 35, 37);

        // Сообщение
        g.setFont(fontMessage);
        if (day == 1) {
            g.drawImage(MESSAGE_CLOUD.getImage(), gc.getWidth() / 2 - 150, 80);
            if (motion.equals("Green move")) {
                switch (greenGraph.size()) {
                    case 0 -> g.drawString("Select the initial node to infect", gc.getWidth() / 2 - 140, 88);
                    case 1 -> g.drawString("Finish the move", gc.getWidth() / 2 - 140, 88);
                }
            } else {
                switch (blueGraph.size()) {
                    case 0 -> g.drawString("Select the initial node to infect", gc.getWidth() / 2 - 140, 88);
                    case 1 -> g.drawString("Finish the move", gc.getWidth() / 2 - 140, 88);
                }
            }
        } else if (flags.get(SELECTED_NODE_FROM_MOVE_VIRUS) && !flags.get(SELECTED_NODE_TO_MOVE_VIRUS)) {
            g.drawImage(MESSAGE_CLOUD.getImage(), gc.getWidth() / 2 - 150, 80);
            g.drawString("Select second node to transfer virus", gc.getWidth() / 2 - 140, 88);
            errorMotion = MotionError.OK;
        } else if (errorMotion != null && errorMotion != MotionError.OK) {
            g.drawImage(MESSAGE_CLOUD.getImage(), gc.getWidth() / 2 - 150, 80);
            switch (errorMotion) {
                case NOT_YOUR_MOTION -> g.drawString(motion + " now!", gc.getWidth() / 2 - 140, 88);
                case RED_NODE_SELECTED -> g.drawString("Red node cannot be selected!", gc.getWidth() / 2 - 140, 88);
                case NOT_ADJACENT -> g.drawString("Nodes are not adjacent!", gc.getWidth() / 2 - 140, 88);
                case SAME_NODE -> g.drawString("This is the same node!", gc.getWidth() / 2 - 140, 88);
            }
        } else if (flags.get(MOVE_VIRUS_MODE)) {
            if (flags.get(HIGHLIGHT_SEND_ALL_VIRUS)) {
                errorMotion = GameLogic.moveVirus(startVirusMove, endVirusMove, MoveVirusPart.ALL);
                flags.put(SELECTED_NODE_FROM_MOVE_VIRUS, false);
                flags.put(SELECTED_NODE_TO_MOVE_VIRUS, false);
                flags.put(MOVE_VIRUS_MODE, false);
            } else if (flags.get(HIGHLIGHT_SEND_HALF_VIRUS)) {
                errorMotion = GameLogic.moveVirus(startVirusMove, endVirusMove, MoveVirusPart.HALF);
                flags.put(SELECTED_NODE_FROM_MOVE_VIRUS, false);
                flags.put(SELECTED_NODE_TO_MOVE_VIRUS, false);
                flags.put(MOVE_VIRUS_MODE, false);
            } else if (flags.get(HIGHLIGHT_SEND_QUARTER_VIRUS)) {
                errorMotion = GameLogic.moveVirus(startVirusMove, endVirusMove, MoveVirusPart.QUARTER);
                flags.put(SELECTED_NODE_FROM_MOVE_VIRUS, false);
                flags.put(SELECTED_NODE_TO_MOVE_VIRUS, false);
                flags.put(MOVE_VIRUS_MODE, false);
            }
        } else if (flags.get(SELECTED_NODE_TO_MOVE_VIRUS)) {
            g.drawImage(LONG_MENU.getImage(), gc.getWidth() / 2 - 150, gc.getHeight() / 2 - 25);
            if (flags.get(HIGHLIGHT_SEND_ALL_VIRUS)) {
                g.drawImage(BUTTON_ALL_VIRUS_MOVE_HOVERED.getImage(), gc.getWidth() / 2 - 115, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(BUTTON_ALL_VIRUS_MOVE.getImage(), gc.getWidth() / 2 - 115, gc.getHeight() / 2 - 15);
            }
            if (flags.get(HIGHLIGHT_SEND_HALF_VIRUS)) {
                g.drawImage(BUTTON_HALF_VIRUS_MOVE_HOVERED.getImage(), gc.getWidth() / 2 - 38, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(BUTTON_HALF_VIRUS_MOVE.getImage(), gc.getWidth() / 2 - 38, gc.getHeight() / 2 - 15);
            }
            if (flags.get(HIGHLIGHT_SEND_QUARTER_VIRUS)) {
                g.drawImage(BUTTON_QUARTER_VIRUS_MOVE_HOVERED.getImage(), gc.getWidth() / 2 + 39, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(BUTTON_QUARTER_VIRUS_MOVE.getImage(), gc.getWidth() / 2 + 39, gc.getHeight() / 2 - 15);
            }
        }

        g.setFont(fontBold);
        // Кнопка закончить ход
        if (endGameWin == EndGameWin.NONE) {
            if (motion.equals("Green move") && greenGraph.size() != 0) {
                if (flags.get(HIGHLIGHT_BUTTON)) {
                    g.drawImage(REGULAR_BUTTON_HOVERED.getImage(), (20 + gc.getWidth() / 3 - 190) / 2 - 100, 230);
                } else {
                    g.drawImage(REGULAR_BUTTON.getImage(), (20 + gc.getWidth() / 3 - 190) / 2 - 100, 230);
                }
                g.drawString("Finish move", (20 + gc.getWidth() / 3 - 190) / 2 - 70, 247);
            } else if (motion.equals("Blue move") && blueGraph.size() != 0) {
                if (flags.get(HIGHLIGHT_BUTTON)) {
                    g.drawImage(REGULAR_BUTTON_HOVERED.getImage(), gc.getWidth() - 350, 230);
                } else {
                    g.drawImage(REGULAR_BUTTON.getImage(), gc.getWidth() - 350, 230);
                }
                g.drawString("Finish move", gc.getWidth() - 320, 247);
            }
        } else {
            g.setFont(fontMessage);
            if (endGameWin == EndGameWin.RED) {
                g.drawImage(MESSAGE_CLOUD.getImage(), gc.getWidth() / 2 - 150, 80);
                g.drawString("Red wins! Restart Game?", gc.getWidth() / 2 - 140, 88);
            } else if (endGameWin == EndGameWin.GREEN) {
                g.drawImage(MESSAGE_CLOUD.getImage(), gc.getWidth() / 2 - 150, 80);
                g.drawString("Green wins! Restart Game?", gc.getWidth() / 2 - 140, 88);
            } else if (endGameWin == EndGameWin.BLUE) {
                g.drawImage(MESSAGE_CLOUD.getImage(), gc.getWidth() / 2 - 150, 80);
                g.drawString("Blue wins! Restart Game?", gc.getWidth() / 2 - 140, 88);
            }
        }

        g.setFont(fontBold);
        // Отображение очередности хода
        g.drawImage(REGULAR_BUTTON.getImage(), gc.getWidth() / 2 - 100, gc.getHeight() - 80);
        g.drawString(motion, gc.getWidth() / 2 - 65, gc.getHeight() - 63);

        // Основная информация
        g.drawString("Green virus", 40, 30);

        g.setFont(font);
        g.drawString("Number of infected vertices: " + GameLogic.virusNodes[0], 40, 60);
        g.drawString("Skill points: " + GameLogic.skillPoints[0], 40, 90);
        g.drawString("Power: " + GameLogic.powers[0], 40, 120);
        g.drawString("Protection: " + GameLogic.protections[0], 40, 150);
        g.drawString("Replication: " + GameLogic.replications[0], 40, 180);


        g.setFont(fontBold);
        g.drawString("Blue virus", gc.getWidth() - 440, 30);

        g.setFont(font);
        g.drawString("Number of infected vertices: " + GameLogic.virusNodes[1], gc.getWidth() - 440, 60);
        g.drawString("Skill points: " + GameLogic.skillPoints[1], gc.getWidth() - 440, 90);
        g.drawString("Power: " + GameLogic.powers[1], gc.getWidth() - 440, 120);
        g.drawString("Protection: " + GameLogic.protections[1], gc.getWidth() - 440, 150);
        g.drawString("Replication: " + GameLogic.replications[1], gc.getWidth() - 440, 180);

        g.drawString("Next power: " + (powers[0] + powerDelta), 40, gc.getHeight() - 130);
        g.drawString("Next protection: " + (protections[0] + protectionDelta), 40, gc.getHeight() - 100);
        g.drawString("Next replication: " + (replications[0] + replicationDelta), 40, gc.getHeight() - 70);

        g.drawString("Next power: " + (powers[1] + powerDelta), gc.getWidth() - 440, gc.getHeight() - 130);
        g.drawString("Next protection: " + (protections[1] + protectionDelta), gc.getWidth() - 440, gc.getHeight() - 100);
        g.drawString("Next replication: " + (replications[1] + replicationDelta), gc.getWidth() - 440, gc.getHeight() - 70);

        // Отрисовка улучшений навыков
        if (endGameWin == EndGameWin.NONE) {
            if (motion.equals("Green move") && greenGraph.size() != 0 && GameLogic.skillPoints[0] > 0) {
                if (flags.get(HIGHLIGHT_POWER_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), 430, 119);
                } else {
                    g.drawImage(PLUS.getImage(), 430, 119);
                }
                if (flags.get(HIGHLIGHT_PROTECTION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), 430, 149);
                } else {
                    g.drawImage(PLUS.getImage(), 430, 149);
                }
                if (flags.get(HIGHLIGHT_REPLICATION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), 430, 179);
                } else {
                    g.drawImage(PLUS.getImage(), 430, 179);
                }
            } else if (motion.equals("Blue move") && blueGraph.size() != 0 && GameLogic.skillPoints[1] > 0) {
                if (flags.get(HIGHLIGHT_POWER_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), gc.getWidth() - 50, 119);
                } else {
                    g.drawImage(PLUS.getImage(), gc.getWidth() - 50, 119);
                }
                if (flags.get(HIGHLIGHT_PROTECTION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), gc.getWidth() - 50, 149);
                } else {
                    g.drawImage(PLUS.getImage(), gc.getWidth() - 50, 149);
                }
                if (flags.get(HIGHLIGHT_REPLICATION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), gc.getWidth() - 50, 179);
                } else {
                    g.drawImage(PLUS.getImage(), gc.getWidth() - 50, 179);
                }
            }
        } else {
            g.drawImage(REGULAR_BUTTON.getImage(), gc.getWidth() / 2 - 100, gc.getHeight() / 2 - 25);
            if (flags.get(HIGHLIGHT_YES_BUTTON)) {
                g.drawImage(YES_BUTTON.getImage(), gc.getWidth() / 2 - 79, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(YES_BUTTON_HOVERED.getImage(), gc.getWidth() / 2 - 79, gc.getHeight() / 2 - 15);
            }
            if (flags.get(HIGHLIGHT_NO_BUTTON)) {
                g.drawImage(NO_BUTTON.getImage(), gc.getWidth() / 2 + 1, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(NO_BUTTON_HOVERED.getImage(), gc.getWidth() / 2 + 1, gc.getHeight() / 2 - 15);
            }
            if (restartGame == RestartGame.YES) {
                init(gc);
            } else if (restartGame == RestartGame.NO) {
                exit(0);
            }
        }

        if (flags.get(HIGHLIGHT_HOME_BUTTON)) {
            g.drawImage(HOME_BUTTON_HOVERED.getImage(), 1350, 20);
        } else {
            g.drawImage(HOME_BUTTON.getImage(), 1350, 20);
        }
        if (flags.get(HIGHLIGHT_REPEAT_BUTTON)) {
            g.drawImage(REPEAT_BUTTON_HOVERED.getImage(), 520, 20);
        } else {
            g.drawImage(REPEAT_BUTTON.getImage(), 520, 20);
        }
        if (flags.get(HIGHLIGHT_HELP_BUTTON)) {
            g.drawImage(HELP_BUTTON_HOVERED.getImage(), 1350, gc.getHeight() - 80);
        } else {
            g.drawImage(HELP_BUTTON.getImage(), 1350, gc.getHeight() - 80);
        }

        if (flags.get(OPEN_HELP_MENU)) {
            g.drawImage(RULES_MENU.getImage(), 550, 100);
            String rules = """
                                                RULES

                    The main goal of the game is to destroy the opposite virus and
                    prevent the disappearance of your virus. Players control
                    viruses, aiming to infect cells and develop their viruses to
                    achieve this goal.

                    All red vertices can heal themselves and try to get rid of the
                    virus (each time the number of regenerated cells is different).

                    Viruses can neutralize each other. For each successful
                    infection of a previously uninfected red node, the player
                    receives skill points that can be used to improve the
                    qualities of the virus.

                    Power is used when attacking a healthy or infected vertex.
                    Protection is used when attacking your infected node with
                    another virus.
                    Replication is used to increase the number of infected cells.

                    To counteract the immunity of a healthy cell, both protection
                    and replication of the virus are used.

                    Victory is achieved when one of the players destroys the
                    opposite virus and does not let his virus disappear.

                    Players can use virus development strategies as well as
                    attack and defense tactics to achieve this goal.
                    """;
            g.drawString(rules, 580, 130);
        }

        if (flags.get(REPEAT_GAME)) {
            init(gc);
            flags.put(REPEAT_GAME, false);
        }

        if (flags.get(EXIT_GAME)) {
            exit(0);
        }
    }
}