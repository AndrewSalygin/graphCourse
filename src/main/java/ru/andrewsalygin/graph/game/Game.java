package ru.andrewsalygin.graph.game;

import org.newdawn.slick.*;

import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.OutlineEffect;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.visualgraph.VisualConnection;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.HashMap;
import java.util.Map;

import static ru.andrewsalygin.graph.game.GameLogic.*;

public class Game extends BasicGame {
    // Размеры таблицы
    public static int cellSize; // Размер каждой ячейки
    public static int nodeRadius; // Размер каждой ячейки
    private int tableScale;
    Image backgroundImage;
    VisualGraph visualGraph;
    private VisualNode highlightedNode;
    private Boolean highlightedNodeFlag;
    private boolean moveVirusFirstFlag;
    private boolean moveVirusSecondFlag;
    private boolean highlightButton;
    private Color highlightColor = Color.yellow;
    private boolean highlightPowerButton;
    private boolean highlightProtectionButton;
    private boolean highlightReplicationButton;
    private UnicodeFont font;
    private UnicodeFont fontMessage;
    private UnicodeFont fontBold;
    private VisualNode startVirusMove;
    private VisualNode endVirusMove;

    public Game() {
        super("Infection Graph");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        startVirusMove = null;
        endVirusMove = null;
        highlightButton = false;
        highlightPowerButton = false;
        highlightProtectionButton = false;
        highlightReplicationButton = false;
        highlightedNodeFlag = false;
        highlightedNode = null;
        moveVirusFirstFlag = false;
        moveVirusSecondFlag = false;

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
        highlightedNodeFlag = false;
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode tmpNode = (VisualNode) entry.getKey();
            if (tmpNode.contains(mouseX, mouseY)) {
                highlightedNode = tmpNode;
                highlightedNodeFlag = true;
                break;
            }
        }

        if (motion.equals("Green move")) {
            if (mouseX >= (20 + gc.getWidth() / 3 - 190) / 2 - 100 && mouseX <= (20 + gc.getWidth() / 3 - 190) / 2 - 100 + 200 &&
            mouseY >= 230 && mouseY <= 280) {
                highlightButton = true;
            } else {
                highlightButton = false;
            }
            if (mouseX >= 430 && mouseX <= 446 && mouseY >= 119 && mouseY <= 135) {
                highlightPowerButton = true;
            } else {
                highlightPowerButton = false;
            }
            if (mouseX >= 430 && mouseX <= 446 && mouseY >= 149 && mouseY <= 165) {
                highlightProtectionButton = true;
            } else {
                highlightProtectionButton = false;
            }
            if (mouseX >= 430 && mouseX <= 446 && mouseY >= 179 && mouseY <= 195) {
                highlightReplicationButton = true;
            } else {
                highlightReplicationButton = false;
            }
        } else {
            if (mouseX >= gc.getWidth() - 350 && mouseX <= gc.getWidth() - 350 + 200 &&
                    mouseY >= 230 && mouseY <= 280) {
                highlightButton = true;
            } else {
                highlightButton = false;
            }
            if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 119 && mouseY <= 135) {
                highlightPowerButton = true;
            } else {
                highlightPowerButton = false;
            }
            if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 149 && mouseY <= 165) {
                highlightProtectionButton = true;
            } else {
                highlightProtectionButton = false;
            }
            if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 179 && mouseY <= 195) {
                highlightReplicationButton = true;
            } else {
                highlightReplicationButton = false;
            }
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (button == Input.MOUSE_LEFT_BUTTON && highlightButton) {
            if (motion.equals("Green move")) {
                motion = "Blue move";
            } else {
                motion = "Green move";
                day++;
            }
        } else if (moveVirusFirstFlag && button == Input.MOUSE_RIGHT_BUTTON) {
            moveVirusSecondFlag = true;
        }
        else if (button == Input.MOUSE_RIGHT_BUTTON && highlightedNodeFlag) {
            moveVirusFirstFlag = true;
        }
        else if (button == Input.MOUSE_LEFT_BUTTON && highlightPowerButton) {
            if (motion.equals("Green move")) {
                GameLogic.powers[0] += powerDelta;
            } else {
                GameLogic.powers[1] += powerDelta;
            }
        }
        else if (button == Input.MOUSE_LEFT_BUTTON && highlightProtectionButton) {
            if (motion.equals("Green move")) {
                GameLogic.protections[0] += protectionDelta;
            } else {
                GameLogic.protections[1] += protectionDelta;
            }
        }
        else if (button == Input.MOUSE_LEFT_BUTTON && highlightReplicationButton) {
            if (motion.equals("Green move")) {
                GameLogic.replications[0] += replicationDelta;
            } else {
                GameLogic.replications[1] += replicationDelta;
            }
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
        Image menu = new Image("src/main/resources/UI/menu.png");
        Image infoMenu = new Image("src/main/resources/UI/infoMenu.png");
        Image button = new Image("src/main/resources/UI/button.png");
        Image buttonHighlighted = new Image("src/main/resources/UI/buttonHighlighted.png");
        Image messageCloud = new Image("src/main/resources/UI/message.png");
        g.drawImage(menu, 20, 20);
        g.drawImage(menu, gc.getWidth() - 460, 20);
        g.drawImage(infoMenu, 20, gc.getHeight() - 150);
        g.drawImage(infoMenu, gc.getWidth() - 460, gc.getHeight() - 150);
        g.setFont(fontBold);

        // Дни
        g.drawImage(buttonHighlighted, gc.getWidth() / 2 - 100, 20);
        g.drawString("Day " + GameLogic.day, gc.getWidth() / 2 - 35, 37);

        // Сообщение
        g.setFont(fontMessage);
        if (day == 1) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Select the initial node to infect", gc.getWidth() / 2 - 140, 88);
        } else if (moveVirusFirstFlag && !moveVirusSecondFlag) {
            startVirusMove = highlightedNode;
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Select second node to transfer virus", gc.getWidth() / 2 - 140, 88);
        } else if (highlightedNodeFlag && moveVirusSecondFlag) {
            endVirusMove = highlightedNode;
            GameLogic.moveVirus(startVirusMove, endVirusMove);
            moveVirusFirstFlag = false;
            moveVirusSecondFlag = false;
        }

        g.setFont(fontBold);
        // Кнопка закончить ход
        if (motion.equals("Green move")) {
            if (!highlightButton) {
                g.drawImage(button, (20 + gc.getWidth() / 3 - 190) / 2 - 100, 230);
            } else {
                g.drawImage(buttonHighlighted, (20 + gc.getWidth() / 3 - 190) / 2 - 100, 230);
            }
            g.drawString("Finish move", (20 + gc.getWidth() / 3 - 190) / 2 - 70, 247);
        } else {
            if (!highlightButton) {
                g.drawImage(button, gc.getWidth() - 350, 230);
            } else {
                g.drawImage(buttonHighlighted, gc.getWidth() - 350, 230);
            }
            g.drawString("Finish move", gc.getWidth() - 320, 247);
        }

        // Отображение очередности хода
        g.drawImage(button, gc.getWidth() / 2 - 100, gc.getHeight() - 80);
        g.drawString(motion, gc.getWidth() / 2 - 65, gc.getHeight() - 63);

        // Основная информация
        g.setFont(fontBold);
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
        Image plusPower = new Image("src/main/resources/UI/plus.png");
        Image plusPowerHighlighted = new Image("src/main/resources/UI/plusHighlighted.png");
        Image plusProtection = new Image("src/main/resources/UI/plus.png");
        Image plusProtectionHighlighted = new Image("src/main/resources/UI/plusHighlighted.png");
        Image plusReplication = new Image("src/main/resources/UI/plus.png");
        Image plusReplicationHighlighted = new Image("src/main/resources/UI/plusHighlighted.png");
        if (motion.equals("Green move") && GameLogic.skillPoints[0] > 0) {
            if (highlightPowerButton) {
                g.drawImage(plusPowerHighlighted, 430, 119);
            } else {
                g.drawImage(plusPower, 430, 119);
            }
            if (highlightProtectionButton) {
                g.drawImage(plusProtectionHighlighted, 430, 149);
            } else {
                g.drawImage(plusProtection, 430, 149);
            }
            if (highlightReplicationButton) {
                g.drawImage(plusReplicationHighlighted, 430, 179);
            } else {
                g.drawImage(plusReplication, 430, 179);
            }
        } else if (motion.equals("Blue move") && GameLogic.skillPoints[1] > 0) {
            if (highlightPowerButton) {
                g.drawImage(plusPowerHighlighted, gc.getWidth() - 50, 119);
            } else {
                g.drawImage(plusPower, gc.getWidth() - 50, 119);
            }
            if (highlightProtectionButton) {
                g.drawImage(plusProtectionHighlighted, gc.getWidth() - 50, 149);
            } else {
                g.drawImage(plusProtection, gc.getWidth() - 50, 149);
            }
            if (highlightReplicationButton) {
                g.drawImage(plusReplicationHighlighted, gc.getWidth() - 50, 179);
            } else {
                g.drawImage(plusReplication, gc.getWidth() - 50, 179);
            }
        }
    }
}