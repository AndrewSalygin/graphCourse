package ru.andrewsalygin.graph.game;

import org.newdawn.slick.*;

import org.newdawn.slick.font.effects.ColorEffect;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.utils.EndGameWin;
import ru.andrewsalygin.graph.game.utils.MotionError;
import ru.andrewsalygin.graph.game.utils.MoveVirusPart;
import ru.andrewsalygin.graph.game.utils.RestartGame;
import ru.andrewsalygin.graph.game.visualgraph.VisualConnection;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.*;

import static java.lang.System.arraycopy;
import static java.lang.System.exit;
import static ru.andrewsalygin.graph.game.GameLogic.*;

public class Game extends BasicGame {
    // Размеры таблицы
    public static int cellSize; // Размер каждой ячейки
    public static int nodeRadius; // Размер каждой ячейки
    private int tableScale;
    Image backgroundImage;
    static VisualGraph visualGraph;
    private VisualNode highlightedNode;
    private Boolean highlightedNodeFlag;
    private boolean moveVirusFirstFlag;
    private boolean moveVirusSecondFlag;
    private boolean highlightButton;
    private boolean openHelp;
    private boolean repeatGame;
    private boolean exitGame;
    private Color highlightColor = Color.yellow;
    private boolean highlightPowerButton;
    private boolean highlightButton12;
    private boolean highlightButton14;
    private boolean highlightButtonYes;
    private boolean highlightButtonNo;
    private boolean highlightHome;
    private boolean highlightRepeat;
    private boolean highlightHelp;
    private boolean moveVirus;
    private boolean highlightProtectionButton;
    private MotionError errorMotion;
    final static int minValueRegenerationHealthNode = 5;
    final static int maxValueRegenerationHealthNode = 30;
    private boolean highlightReplicationButton;
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
    private Image plusPower;
    private Image plusPowerHighlighted;
    private Image plusProtection;
    private Image plusProtectionHighlighted;
    private Image plusReplication;
    private Image plusReplicationHighlighted;
    private Image yesButton;
    private Image yesButtonHighlighted;
    private Image noButton;
    private Image buttonLong;
    private Image oneButton;
    private Image oneButtonHighlighted;
    private boolean highlightButton1;
    private Image noButtonHighlighted;
    private Image infoMenuBig;
    private Image help;
    private Image helpHighlighted;
    private Image home;
    private Image homeHighlighted;
    private Image repeat;
    private Image repeatHighlighted;

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
        highlightButton12 = false;
        highlightButton14 = false;
        moveVirusFirstFlag = false;
        moveVirusSecondFlag = false;
        moveVirus = false;
        highlightHelp = false;
        highlightHome = false;
        highlightRepeat = false;
        repeatGame = false;
        exitGame = false;
        highlightButton1 = false;

        endGameWin = EndGameWin.NONE;
        restartGame = RestartGame.NONE;

        plusPower = new Image("src/main/resources/UI/plus.png");
        plusPowerHighlighted = new Image("src/main/resources/UI/plusHighlighted.png");
        plusProtection = new Image("src/main/resources/UI/plus.png");
        plusProtectionHighlighted = new Image("src/main/resources/UI/plusHighlighted.png");
        plusReplication = new Image("src/main/resources/UI/plus.png");
        plusReplicationHighlighted = new Image("src/main/resources/UI/plusHighlighted.png");
        yesButton = new Image("src/main/resources/UI/yesButton.png");
        yesButtonHighlighted = new Image("src/main/resources/UI/yesButtonHighlighted.png");
        noButton = new Image("src/main/resources/UI/noButton.png");
        noButtonHighlighted = new Image("src/main/resources/UI/noButtonHighlighted.png");
        infoMenuBig = new Image("src/main/resources/UI/infoMenuBig.png");
        help = new Image("src/main/resources/UI/help.png");
        helpHighlighted = new Image("src/main/resources/UI/helpHighlighted.png");
        home = new Image("src/main/resources/UI/home.png");
        homeHighlighted = new Image("src/main/resources/UI/homeHighlighted.png");
        repeat = new Image("src/main/resources/UI/repeat.png");
        repeatHighlighted = new Image("src/main/resources/UI/repeatHighlighted.png");
        buttonLong = new Image("src/main/resources/UI/buttonLong.png");
        oneButton = new Image("src/main/resources/UI/oneButton.png");
        oneButtonHighlighted = new Image("src/main/resources/UI/oneButtonHighlighted.png");

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
        highlightedNodeFlag = false;
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode tmpNode = (VisualNode) entry.getKey();
            if (tmpNode.contains(mouseX, mouseY)) {
                highlightedNode = tmpNode;
                highlightedNodeFlag = true;
                break;
            }
        }
        if (endGameWin == EndGameWin.NONE) {
            if (motion.equals("Green move")) {
                if (mouseX >= (20 + gc.getWidth() / 3 - 190) / 2 - 100 && mouseX <= (20 + gc.getWidth() / 3 - 190) / 2 - 100 + 200 &&
                        mouseY >= 230 && mouseY <= 280 && greenGraph.size() != 0) {
                    highlightButton = true;
                } else {
                    highlightButton = false;
                }
                if (mouseX >= 430 && mouseX <= 446 && mouseY >= 119 && mouseY <= 135 && skillPoints[0] > 0) {
                    highlightPowerButton = true;
                } else {
                    highlightPowerButton = false;
                }
                if (mouseX >= 430 && mouseX <= 446 && mouseY >= 149 && mouseY <= 165 && skillPoints[0] > 0) {
                    highlightProtectionButton = true;
                } else {
                    highlightProtectionButton = false;
                }
                if (mouseX >= 430 && mouseX <= 446 && mouseY >= 179 && mouseY <= 195 && skillPoints[0] > 0) {
                    highlightReplicationButton = true;
                } else {
                    highlightReplicationButton = false;
                }
            } else if (motion.equals("Blue move")) {
                if (mouseX >= gc.getWidth() - 350 && mouseX <= gc.getWidth() - 350 + 200 &&
                        mouseY >= 230 && mouseY <= 280 && blueGraph.size() != 0) {
                    highlightButton = true;
                } else {
                    highlightButton = false;
                }
                if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 119 && mouseY <= 135
                        && skillPoints[1] > 0) {
                    highlightPowerButton = true;
                } else {
                    highlightPowerButton = false;
                }
                if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 149 && mouseY <= 165
                        && skillPoints[1] > 0) {
                    highlightProtectionButton = true;
                } else {
                    highlightProtectionButton = false;
                }
                if (mouseX >= gc.getWidth() - 50 && mouseX <= gc.getWidth() - 34 && mouseY >= 179 && mouseY <= 195
                        && skillPoints[1] > 0) {
                    highlightReplicationButton = true;
                } else {
                    highlightReplicationButton = false;
                }
            }

            if (moveVirusSecondFlag && mouseX >= gc.getWidth() / 2 - 115 && mouseX <= gc.getWidth() / 2 - 40 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                highlightButton1 = true;
            } else {
                highlightButton1 = false;
            }
            if (moveVirusSecondFlag && mouseX >= gc.getWidth() / 2 - 38 && mouseX <= gc.getWidth() / 2 + 37 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                highlightButton12 = true;
            } else {
                highlightButton12 = false;
            }
            if (moveVirusSecondFlag && mouseX >= gc.getWidth() / 2 + 39 && mouseX <= gc.getWidth() / 2 + 114 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                highlightButton14 = true;
            } else {
                highlightButton14 = false;
            }

//            g.drawImage(buttonLong, gc.getWidth() / 2 - 150, gc.getHeight() / 2 - 25);
//            if (highlightButton1) {
//                g.drawImage(oneButtonHighlighted, gc.getWidth() / 2 - 115, gc.getHeight() / 2 - 15);
//            } else {
//                g.drawImage(oneButton, gc.getWidth() / 2 - 115, gc.getHeight() / 2 - 15);
//            }
//            if (highlightButton12) {
//                g.drawImage(innerButtonHighlighted12, gc.getWidth() / 2 - 38, gc.getHeight() / 2 - 15);
//            } else {
//                g.drawImage(innerButton12, gc.getWidth() / 2 - 38, gc.getHeight() / 2 - 15);
//            }
//            if (highlightButton14) {
//                g.drawImage(innerButtonHighlighted14, gc.getWidth() / 2 + 39, gc.getHeight() / 2 - 15);
//            } else {
//                g.drawImage(innerButton14, gc.getWidth() / 2 + 39, gc.getHeight() / 2 - 15);
//            }
        } else {
            if (mouseX >= gc.getWidth() / 2 - 79 && mouseX <= gc.getWidth() / 2 - 4 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                highlightButtonYes = true;
            } else {
                highlightButtonYes = false;
            }
            if (mouseX >= gc.getWidth() / 2 + 1 && mouseX <= gc.getWidth() / 2 + 76 &&
                    mouseY >= gc.getHeight() / 2 - 15 && mouseY <= gc.getHeight() / 2 + 15) {
                highlightButtonNo = true;
            } else {
                highlightButtonNo = false;
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
            highlightHome = true;
        } else {
            highlightHome = false;
        }
        if (mouseX >= 500 && mouseX <= 550 && mouseY >= 20 && mouseY <= 70) {
            highlightRepeat = true;
        } else {
            highlightRepeat = false;
        }
        if (mouseX >= 1350 && mouseX <= 1400 && mouseY >= 920 && mouseY <= 970) {
            highlightHelp = true;
        } else {
            highlightHelp = false;
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (endGameWin == EndGameWin.NONE) {
            if (button == Input.MOUSE_LEFT_BUTTON && highlightButton) {
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
            }
            else if (day == 1 && button == Input.MOUSE_LEFT_BUTTON && highlightedNodeFlag) {
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
            }
            else if (button == Input.MOUSE_LEFT_BUTTON && (highlightButton1 || highlightButton12 || highlightButton14)) {
                moveVirus = true;
            }
            else if (highlightedNodeFlag && moveVirusFirstFlag && button == Input.MOUSE_LEFT_BUTTON) {
                moveVirusSecondFlag = true;
                endVirusMove = highlightedNode;
            }
            else if (button == Input.MOUSE_LEFT_BUTTON && highlightedNodeFlag) {
                moveVirusFirstFlag = true;
                startVirusMove = highlightedNode;
            }
            else if (button == Input.MOUSE_LEFT_BUTTON && highlightPowerButton) {
                if (motion.equals("Green move")) {
                    GameLogic.powers[0] += powerDelta;
                    skillPoints[0]--;
                } else {
                    GameLogic.powers[1] += powerDelta;
                    skillPoints[1]--;
                }
            }
            else if (button == Input.MOUSE_LEFT_BUTTON && highlightProtectionButton) {
                if (motion.equals("Green move")) {
                    GameLogic.protections[0] += protectionDelta;
                    skillPoints[0]--;
                } else {
                    GameLogic.protections[1] += protectionDelta;
                    skillPoints[1]--;
                }
            }
            else if (button == Input.MOUSE_LEFT_BUTTON && highlightReplicationButton) {
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
                if (highlightButtonYes) {
                    restartGame = RestartGame.YES;
                } else if (highlightButtonNo) {
                    restartGame = RestartGame.NO;
                }
            }
        }
        if (highlightHelp && button == Input.MOUSE_LEFT_BUTTON) {
            if (openHelp) {
                openHelp = false;
            } else {
                openHelp = true;
            }
        }
        if (highlightRepeat && button == Input.MOUSE_LEFT_BUTTON) {
            repeatGame = true;
        }
        if (highlightHome && button == Input.MOUSE_LEFT_BUTTON) {
            exitGame = true;
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
        Image innerButton12 = new Image("src/main/resources/UI/innerButton12.png");
        Image innerButtonHighlighted12 = new Image("src/main/resources/UI/innerButtonHighlighted12.png");
        Image innerButton14 = new Image("src/main/resources/UI/innerButton14.png");
        Image innerButtonHighlighted14 = new Image("src/main/resources/UI/innerButtonHighlighted14.png");
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
        if (day == 1 && greenGraph.size() == 0 && motion.equals("Green move")) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Select the initial node to infect", gc.getWidth() / 2 - 140, 88);
        }
        else if (day == 1 && greenGraph.size() == 1 && motion.equals("Green move")) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Finish the move", gc.getWidth() / 2 - 140, 88);
        }
        else if (day == 1 && blueGraph.size() == 0 && motion.equals("Blue move")) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Select the initial node to infect", gc.getWidth() / 2 - 140, 88);
        }
        else if (day == 1 && greenGraph.size() == 1 && motion.equals("Blue move")) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Finish the move", gc.getWidth() / 2 - 140, 88);
        }
        else if (moveVirusFirstFlag && !moveVirusSecondFlag) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Select second node to transfer virus", gc.getWidth() / 2 - 140, 88);
            errorMotion = MotionError.OK;
        } else if (errorMotion == MotionError.NOT_YOUR_MOTION) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString(motion + " now!", gc.getWidth() / 2 - 140, 88);
        } else if (errorMotion == MotionError.RED_NODE_SELECTED) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Red node cannot be selected!", gc.getWidth() / 2 - 140, 88);
        } else if (errorMotion == MotionError.NOT_ADJACENT) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("Nodes are not adjacent!", gc.getWidth() / 2 - 140, 88);
        } else if (errorMotion == MotionError.SAME_NODE) {
            g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
            g.drawString("This is the same node!", gc.getWidth() / 2 - 140, 88);
        }
        else if (moveVirus) {
            if (highlightButton1) {
                errorMotion = GameLogic.moveVirus(startVirusMove, endVirusMove, MoveVirusPart.ALL);
                moveVirusFirstFlag = false;
                moveVirusSecondFlag = false;
                moveVirus = false;
            }
            else if (highlightButton12) {
                errorMotion = GameLogic.moveVirus(startVirusMove, endVirusMove, MoveVirusPart.HALF);
                moveVirusFirstFlag = false;
                moveVirusSecondFlag = false;
                moveVirus = false;
            } else if (highlightButton14) {
                errorMotion = GameLogic.moveVirus(startVirusMove, endVirusMove, MoveVirusPart.QUARTER);
                moveVirusFirstFlag = false;
                moveVirusSecondFlag = false;
                moveVirus = false;
            }
        }
        else if (moveVirusSecondFlag) {
            g.drawImage(buttonLong, gc.getWidth() / 2 - 150, gc.getHeight() / 2 - 25);
            if (highlightButton1) {
                g.drawImage(oneButtonHighlighted, gc.getWidth() / 2 - 115, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(oneButton, gc.getWidth() / 2 - 115, gc.getHeight() / 2 - 15);
            }
            if (highlightButton12) {
                g.drawImage(innerButtonHighlighted12, gc.getWidth() / 2 - 38, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(innerButton12, gc.getWidth() / 2 - 38, gc.getHeight() / 2 - 15);
            }
            if (highlightButton14) {
                g.drawImage(innerButtonHighlighted14, gc.getWidth() / 2 + 39, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(innerButton14, gc.getWidth() / 2 + 39, gc.getHeight() / 2 - 15);
            }
        }

        g.setFont(fontBold);
        // Кнопка закончить ход
        if (endGameWin == EndGameWin.NONE) {
            if (motion.equals("Green move") && greenGraph.size() != 0) {
                if (!highlightButton) {
                    g.drawImage(button, (20 + gc.getWidth() / 3 - 190) / 2 - 100, 230);
                } else {
                    g.drawImage(buttonHighlighted, (20 + gc.getWidth() / 3 - 190) / 2 - 100, 230);
                }
                g.drawString("Finish move", (20 + gc.getWidth() / 3 - 190) / 2 - 70, 247);
            } else if (motion.equals("Blue move") && blueGraph.size() != 0) {
                if (!highlightButton) {
                    g.drawImage(button, gc.getWidth() - 350, 230);
                } else {
                    g.drawImage(buttonHighlighted, gc.getWidth() - 350, 230);
                }
                g.drawString("Finish move", gc.getWidth() - 320, 247);
            }
        } else {
            g.setFont(fontMessage);
            if (endGameWin == EndGameWin.RED) {
                g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
                g.drawString("Red wins! Restart Game?", gc.getWidth() / 2 - 140, 88);
            }
            else if (endGameWin == EndGameWin.GREEN) {
                g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
                g.drawString("Green wins! Restart Game?", gc.getWidth() / 2 - 140, 88);
            }
            else if (endGameWin == EndGameWin.BLUE) {
                g.drawImage(messageCloud, gc.getWidth() / 2 - 150, 80);
                g.drawString("Blue wins! Restart Game?", gc.getWidth() / 2 - 140, 88);
            }
        }

        g.setFont(fontBold);
        // Отображение очередности хода
        g.drawImage(button, gc.getWidth() / 2 - 100, gc.getHeight() - 80);
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
            } else if (motion.equals("Blue move") && blueGraph.size() != 0 && GameLogic.skillPoints[1] > 0) {
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
        } else {
            g.drawImage(button, gc.getWidth() / 2 - 100, gc.getHeight() / 2 - 25);
            if (highlightButtonYes) {
                g.drawImage(yesButton, gc.getWidth() / 2 - 79, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(yesButtonHighlighted, gc.getWidth() / 2 - 79, gc.getHeight() / 2 - 15);
            }
            if (highlightButtonNo) {
                g.drawImage(noButton, gc.getWidth() / 2 + 1, gc.getHeight() / 2 - 15);
            } else {
                g.drawImage(noButtonHighlighted, gc.getWidth() / 2 + 1, gc.getHeight() / 2 - 15);
            }
            if (restartGame == RestartGame.YES) {
                init(gc);
            } else if (restartGame == RestartGame.NO) {
                exit(0);
            }
        }

        if (highlightHome) {
            g.drawImage(homeHighlighted, 1350, 20);
        } else {
            g.drawImage(home, 1350, 20);
        }
        if (highlightRepeat) {
            g.drawImage(repeatHighlighted, 500, 20);
        } else {
            g.drawImage(repeat, 500, 20);
        }
        if (highlightHelp) {
            g.drawImage(helpHighlighted, 1350, 920);
        } else {
            g.drawImage(help, 1350, 920);
        }

        if (openHelp) {
            g.drawImage(infoMenuBig, 550, 100);
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

        if (repeatGame) {
            init(gc);
            repeatGame = false;
        }

        if (exitGame) {
            exit(0);
        }
    }
}