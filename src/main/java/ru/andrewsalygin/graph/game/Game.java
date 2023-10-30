package ru.andrewsalygin.graph.game;

import org.newdawn.slick.*;

import org.newdawn.slick.font.effects.ColorEffect;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.utils.*;
import ru.andrewsalygin.graph.game.visualgraph.VisualConnection;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.*;

import static java.lang.System.exit;
import static ru.andrewsalygin.graph.game.GameLogic.endGameWin;
import static ru.andrewsalygin.graph.game.GameLogic.highlightedNode;
import static ru.andrewsalygin.graph.game.utils.Menu.*;
import static ru.andrewsalygin.graph.game.utils.Button.*;
import static ru.andrewsalygin.graph.game.utils.Flag.*;
import static ru.andrewsalygin.graph.game.utils.UI.unit;

public class Game extends BasicGame {
    final static int MIN_VALUE_REGENERATION_HEALTH_NODE = 1;
    final static int MAX_VALUE_REGENERATION_HEALTH_NODE = 25;
    private final Color HIGHLIGHT_NODE_COLOR = Color.yellow;
    private static final Random random = new Random();
    private static GameLogic gameLogic;

    public Game() {
        super("Infection Graph");
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.setShowFPS(false); // Скрыть отображение FPS

        UI.backgroundImage = new Image("/src/main/resources/backgrounds/background4.png");

        // Размеры таблицы
        int tableScale = 25;

        UI.rows = 15;
        UI.cols = 15;
        UI.cellSize = gc.getHeight() / tableScale;
        UI.nodeRadius = UI.cellSize / 3;
        UI.gridWidth = UI.cols * UI.cellSize;
        UI.gridHeight = UI.rows * UI.cellSize;

        float screenResolutionX = (float) gc.getWidth() / 1920;
        float screenResolutionY = (float) gc.getWidth() / 1920;

        // Единица измерения для правильного отображения на любых разрешениях экрана
        unit = new MeasureUnit(0, 0, 0, 0, 0, 0, 0, 0, screenResolutionX, screenResolutionY);

        unit.setSize1(16);
        unit.setSize2(10);
        UI.font = new UnicodeFont("src/main/resources/fonts/better-vcr.ttf", unit.getSize1(), false, false);
        UI.fontMessage = new UnicodeFont("src/main/resources/fonts/better-vcr.ttf", unit.getSize2(), false,
                false);
        UI.fontBold = new UnicodeFont("src/main/resources/fonts/better-vcr.ttf", unit.getSize1(), true, false);

        // Устанавливаем эффекты для шрифта
        UI.font.getEffects().add(new ColorEffect(java.awt.Color.black));
        UI.fontMessage.getEffects().add(new ColorEffect(java.awt.Color.black));
        UI.fontBold.getEffects().add(new ColorEffect(java.awt.Color.black));

        // Установка эффекта для сглаживания текста
        UI.font.getEffects().add(new ColorEffect(java.awt.Color.black));
        UI.fontMessage.getEffects().add(new ColorEffect(java.awt.Color.black));
        UI.fontBold.getEffects().add(new ColorEffect(java.awt.Color.black));

        // Инициализируем шрифт
        UI.font.addAsciiGlyphs();
        UI.font.loadGlyphs();
        UI.fontMessage.addAsciiGlyphs();
        UI.fontMessage.loadGlyphs();
        UI.fontBold.addAsciiGlyphs();
        UI.fontBold.loadGlyphs();

        // Рассчитываем координаты начала отрисовки, чтобы разместить поле по центру экрана
        gameLogic = new GameLogic();
        gameLogic.startGame(gc);
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Input input = gc.getInput();

        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();

        // Нахождение вершины, на которую навели мышкой
        gameLogic.getSession().getFlags().put(HIGHLIGHT_NODE, false);
        highlightedNode = null;
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : gameLogic.getSession().getVisualGraph().getGraph().entrySet()) {
            VisualNode tmpNode = (VisualNode) entry.getKey();
            if (tmpNode.contains(mouseX, mouseY)) {
                highlightedNode = tmpNode;
                gameLogic.getSession().getFlags().put(HIGHLIGHT_NODE, true);
                break;
            }
        }

        if (endGameWin == EndGameWin.NONE) {
            if (gameLogic.getSession().getMotion() == Motion.Green) { // Подсветка кнопок зелёных (меню слева)
                unit.setY1(255);
                unit.setX2(200);
                unit.setY2(25);
                if (Math.abs(mouseX - (float) gc.getWidth() / 14 - unit.getX2() / 2) <= unit.getX2() / 2
                        && Math.abs(mouseY - unit.getY1()) <= unit.getY2() && gameLogic.getSession().getGreenGraph().size() != 0) {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_BUTTON, true);
                } else {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_BUTTON, false);
                }
                if (gameLogic.getSession().getSkillPoints()[0] > 0) {
                    unit.setX1(438);
                    unit.setX2(8);
                    unit.setY1(123);
                    unit.setY2(8);
                    if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                            && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_POWER_INCREASE_BUTTON, true);
                    } else {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_POWER_INCREASE_BUTTON, false);
                    }
                    unit.setY1(153);
                    if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                            && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, true);
                    } else {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, false);
                    }
                    unit.setY1(183);
                    if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                            && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, true);
                    } else {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, false);
                    }
                }
            } else if (gameLogic.getSession().getMotion() == Motion.Blue) { // Подсветка кнопок синих (меню справа)
                unit.setX1(350);
                unit.setX2(150);
                unit.setY1(230);
                unit.setY2(280);
                if (mouseX >= gc.getWidth() - unit.getX1() && mouseX <= gc.getWidth() - unit.getX2() &&
                        mouseY >= unit.getY1() && mouseY <= unit.getY2() && gameLogic.getSession().getBlueGraph().size() != 0) {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_BUTTON, true);
                } else {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_BUTTON, false);
                }
                if (gameLogic.getSession().getSkillPoints()[1] > 0) {
                    unit.setX1(60);
                    unit.setX2(44);
                    unit.setY1(123);
                    unit.setY2(8);
                    if (mouseX >= gc.getWidth() - unit.getX1() && mouseX <= gc.getWidth() - unit.getX2()
                            && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_POWER_INCREASE_BUTTON, true);
                    } else {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_POWER_INCREASE_BUTTON, false);
                    }
                    unit.setY1(153);
                    if (mouseX >= gc.getWidth() - unit.getX1() && mouseX <= gc.getWidth() - unit.getX2()
                            && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, true);
                    } else {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_PROTECTION_INCREASE_BUTTON, false);
                    }
                    unit.setY1(183);
                    if (mouseX >= gc.getWidth() - unit.getX1() && mouseX <= gc.getWidth() - unit.getX2()
                            && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, true);
                    } else {
                        gameLogic.getSession().getFlags().put(HIGHLIGHT_REPLICATION_INCREASE_BUTTON, false);
                    }
                }
            }

            // Подсветка кнопок, отвечающих за количество передаваемого вируса
            if (gameLogic.getSession().getFlags().get(SELECTED_NODE_TO_MOVE_VIRUS)) {
                unit.setX1(115);
                unit.setX2(40);
                unit.setY1(15);
                unit.setY2(15);
                if (mouseX >= (float) gc.getWidth() / 2 - unit.getX1()
                        && mouseX <= (float) gc.getWidth() / 2 - unit.getX2()
                        && mouseY >= (float) gc.getHeight() / 2 - unit.getY1()
                        && mouseY <= (float) gc.getHeight() / 2 + unit.getY2()) {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_ALL_VIRUS, true);
                } else {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_ALL_VIRUS, false);
                }
                unit.setX1(38);
                unit.setX2(37);
                if (mouseX >= (float) gc.getWidth() / 2 - unit.getX1()
                        && mouseX <= (float) gc.getWidth() / 2 + unit.getX2()
                        && mouseY >= (float) gc.getHeight() / 2 - unit.getY1()
                        && mouseY <= (float) gc.getHeight() / 2 + unit.getY2()) {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_HALF_VIRUS, true);
                } else {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_HALF_VIRUS, false);
                }
                unit.setX1(39);
                unit.setX2(114);
                if (mouseX >= (float) gc.getWidth() / 2 + unit.getX1()
                        && mouseX <= (float) gc.getWidth() / 2 + unit.getX2()
                        && mouseY >= (float) gc.getHeight() / 2 - unit.getY1()
                        && mouseY <= (float) gc.getHeight() / 2 + unit.getY2()) {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_QUARTER_VIRUS, true);
                } else {
                    gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_QUARTER_VIRUS, false);
                }
            } else {
                gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_ALL_VIRUS, false);
                gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_HALF_VIRUS, false);
                gameLogic.getSession().getFlags().put(HIGHLIGHT_SEND_QUARTER_VIRUS, false);
            }
        } else { // Игра закончена, вывод кнопок перезапуска игры
            unit.setX1(79);
            unit.setX2(4);
            unit.setY1(15);
            unit.setY2(15);
            if (mouseX >= (float) gc.getWidth() / 2 - unit.getX1()
                    && mouseX <= (float) gc.getWidth() / 2 - unit.getX2()
                    && mouseY >= (float) gc.getHeight() / 2 - unit.getY1()
                    && mouseY <= (float) gc.getHeight() / 2 + unit.getY2()) {
                gameLogic.getSession().getFlags().put(HIGHLIGHT_YES_BUTTON, true);
            } else {
                gameLogic.getSession().getFlags().put(HIGHLIGHT_YES_BUTTON, false);
            }
            unit.setX1(1);
            unit.setX2(76);
            if (mouseX >= (float) gc.getWidth() / 2 + unit.getX1()
                    && mouseX <= (float) gc.getWidth() / 2 + unit.getX2()
                    && mouseY >= (float) gc.getHeight() / 2 - unit.getY1()
                    && mouseY <= (float) gc.getHeight() / 2 + unit.getY2()) {
                gameLogic.getSession().getFlags().put(HIGHLIGHT_NO_BUTTON, true);
            } else {
                gameLogic.getSession().getFlags().put(HIGHLIGHT_NO_BUTTON, false);
            }
        }

        // Определение победителя в конце игры
        if (gameLogic.getSession().getDay() != 1) {
            if (gameLogic.getSession().getBlueGraph().size() == 0 && gameLogic.getSession().getGreenGraph().size() == 0) {
                endGameWin = EndGameWin.RED;
            } else if (gameLogic.getSession().getBlueGraph().size() == 0) {
                endGameWin = EndGameWin.GREEN;
            } else if (gameLogic.getSession().getGreenGraph().size() == 0) {
                endGameWin = EndGameWin.BLUE;
            }
        }

        // Определение подсветки кнопок меню на основе позиции мыши
        unit.setX1(1375);
        unit.setX2(25);
        unit.setY1(45);
        unit.setY2(25);
        if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_HOME_BUTTON, true);
        } else {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_HOME_BUTTON, false);
        }
        unit.setX1(545);
        if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                && Math.abs(mouseY - unit.getY1()) <= unit.getY2()) {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_REPEAT_BUTTON, true);
        } else {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_REPEAT_BUTTON, false);
        }
        unit.setX1(1375);
        unit.setY1(80);
        unit.setY2(30);
        if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                && mouseY >= gc.getHeight() - unit.getY1()
                && mouseY <= gc.getHeight() - unit.getY2()) {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_HELP_BUTTON, true);
        } else {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_HELP_BUTTON, false);
        }

        unit.setX1(545);
        unit.setY1(80);
        unit.setY2(30);
        if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                && mouseY >= gc.getHeight() - unit.getY1()
                && mouseY <= gc.getHeight() - unit.getY2()) {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_SAVE_BUTTON, true);
        } else {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_SAVE_BUTTON, false);
        }

        unit.setX1(605);
        unit.setY1(80);
        unit.setY2(30);
        if (Math.abs(mouseX - unit.getX1()) <= unit.getX2()
                && mouseY >= gc.getHeight() - unit.getY1()
                && mouseY <= gc.getHeight() - unit.getY2()) {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_OPEN_BUTTON, true);
        } else {
            gameLogic.getSession().getFlags().put(HIGHLIGHT_OPEN_BUTTON, false);
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        // События обрабатываемые, если игра не закончилась
        if (endGameWin == EndGameWin.NONE) {
            // Завершение хода
            if (button == Input.MOUSE_LEFT_BUTTON && gameLogic.getSession().getFlags().get(HIGHLIGHT_BUTTON)) {
                // Меняем цвет
                if (gameLogic.getSession().getMotion() == Motion.Green) {
                    gameLogic.getSession().setMotion(Motion.Blue);
                } else { // Когда синий завершает ход, начинается новый день
                    gameLogic.getSession().setMotion(Motion.Green);
                    gameLogic.getSession().setDay(gameLogic.getSession().getDay() + 1);
                    List<VisualNode> nodesToDelete = new ArrayList<>();
                    // Красные вершины активируют иммунитет и пытаются убить зелёный вирус
                    for (Map.Entry<VisualNode, HashMap<Node, Connection>> entry : gameLogic.getSession().getGreenGraph().entrySet()) {
                        int protectionHealthNode =
                                random.nextInt(MAX_VALUE_REGENERATION_HEALTH_NODE
                                        - MIN_VALUE_REGENERATION_HEALTH_NODE + 1) + MIN_VALUE_REGENERATION_HEALTH_NODE;
                        int prev_hp = entry.getKey().getHp();
                        int new_hp = prev_hp + gameLogic.getSession().getReplications()[0] + gameLogic.getSession().getProtections()[0] - protectionHealthNode;
                        if (new_hp > 0 && new_hp <= 300) {
                            entry.getKey().setHp(new_hp);
                        } else if (new_hp <= 0) {
                            nodesToDelete.add(entry.getKey());
                            entry.getKey().setHp(Math.abs(new_hp));
                            entry.getKey().setEllipseColor(new Color(163, 0, 0));
                            gameLogic.getSession().getRedGraph().put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (VisualNode node : nodesToDelete) {
                        gameLogic.getSession().getGreenGraph().remove(node);
                    }

                    // Красные вершины активируют иммунитет и пытаются убить синий вирус
                    nodesToDelete = new ArrayList<>();
                    for (Map.Entry<VisualNode, HashMap<Node, Connection>> entry : gameLogic.getSession().getBlueGraph().entrySet()) {
                        int protectionHealthNode =
                                random.nextInt(MAX_VALUE_REGENERATION_HEALTH_NODE
                                        - MIN_VALUE_REGENERATION_HEALTH_NODE + 1) + MIN_VALUE_REGENERATION_HEALTH_NODE;
                        int prev_hp = entry.getKey().getHp();
                        int new_hp = prev_hp + gameLogic.getSession().getReplications()[1] + gameLogic.getSession().getProtections()[1] - protectionHealthNode;
                        if (new_hp > 0 && new_hp <= 300) {
                            entry.getKey().setHp(new_hp);
                        } else if (new_hp <= 0) {
                            nodesToDelete.add(entry.getKey());
                            entry.getKey().setHp(Math.abs(new_hp));
                            entry.getKey().setEllipseColor(new Color(163, 0, 0));
                            gameLogic.getSession().getRedGraph().put(entry.getKey(), entry.getValue());
                        }
                    }
                    for (VisualNode node : nodesToDelete) {
                        gameLogic.getSession().getBlueGraph().remove(node);
                    }

                    // Регенерация хп у здоровых вершин
                    for (Map.Entry<VisualNode, HashMap<Node, Connection>> entry : gameLogic.getSession().getRedGraph().entrySet()) {
                        int replicationHealthNode =
                                random.nextInt(MAX_VALUE_REGENERATION_HEALTH_NODE
                                        - MIN_VALUE_REGENERATION_HEALTH_NODE + 1) + MIN_VALUE_REGENERATION_HEALTH_NODE;
                        VisualNode node = (VisualNode) entry.getKey();
                        int prev_hp = node.getHp();
                        int new_hp = prev_hp + replicationHealthNode;
                        if (new_hp <= random.nextInt(50 - 10 + 1) + 10) {
                            node.setHp(new_hp);
                        }
                    }
                }
            } else if (gameLogic.getSession().getDay() == 1 && button == Input.MOUSE_LEFT_BUTTON && gameLogic.getSession().getFlags().get(HIGHLIGHT_NODE)) {
                if (gameLogic.getSession().getMotion() == Motion.Green && gameLogic.getSession().getGreenGraph().size() == 0) {
                    // Нельзя на первом дне заразить вершину с чужим вирусом
                    if (gameLogic.getSession().getBlueGraph().containsKey(highlightedNode)) {
                        gameLogic.getSession().getFlags().put(ERROR_INIT_VIRUS, true);
                    } else {
                        highlightedNode.setEllipseColor(Color.green);
                        gameLogic.getSession().getGreenGraph().put(highlightedNode, gameLogic.getSession().getVisualGraph().getGraph().get(highlightedNode));
                        gameLogic.getSession().getRedGraph().remove(highlightedNode);

                        int[] tmp = gameLogic.getSession().getSkillPoints();
                        tmp[0]++;
                        gameLogic.getSession().setSkillPoints(tmp);

                        highlightedNode.setHp(100);
                        highlightedNode.setSkillPoint(false);
                        gameLogic.getSession().getFlags().put(ERROR_INIT_VIRUS, false);
                    }
                } else if (gameLogic.getSession().getMotion() == Motion.Blue && gameLogic.getSession().getBlueGraph().size() == 0) {
                    if (gameLogic.getSession().getGreenGraph().containsKey(highlightedNode)) {
                        gameLogic.getSession().getFlags().put(ERROR_INIT_VIRUS, true);
                    } else {
                        highlightedNode.setEllipseColor(Color.blue);
                        gameLogic.getSession().getBlueGraph().put(highlightedNode, gameLogic.getSession().getVisualGraph().getGraph().get(highlightedNode));
                        gameLogic.getSession().getRedGraph().remove(highlightedNode);

                        int[] tmp = gameLogic.getSession().getSkillPoints();
                        tmp[1]++;
                        gameLogic.getSession().setSkillPoints(tmp);

                        highlightedNode.setHp(100);
                        highlightedNode.setSkillPoint(false);
                        gameLogic.getSession().getFlags().put(ERROR_INIT_VIRUS, false);
                    }
                }
                // Процесс перекидывания вируса с вершин на вершины
                // Важно: else if должны быть именно в таком порядке
            } else if (button == Input.MOUSE_LEFT_BUTTON && (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_ALL_VIRUS)
                    || gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_HALF_VIRUS) || gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_QUARTER_VIRUS))) {
                gameLogic.getSession().getFlags().put(MOVE_VIRUS_MODE, true);
            } else if (gameLogic.getSession().getFlags().get(HIGHLIGHT_NODE) && gameLogic.getSession().getFlags().get(SELECTED_NODE_FROM_MOVE_VIRUS)
                    && button == Input.MOUSE_LEFT_BUTTON) {
                gameLogic.getSession().getFlags().put(SELECTED_NODE_TO_MOVE_VIRUS, true);
                GameLogic.endVirusMove = highlightedNode;
            } else if (button == Input.MOUSE_LEFT_BUTTON && gameLogic.getSession().getFlags().get(HIGHLIGHT_NODE)) {
                gameLogic.getSession().getFlags().put(SELECTED_NODE_FROM_MOVE_VIRUS, true);
                GameLogic.startVirusMove = highlightedNode;
                // Прокачка навыков
            } else if (button == Input.MOUSE_LEFT_BUTTON && gameLogic.getSession().getFlags().get(HIGHLIGHT_POWER_INCREASE_BUTTON)) {
                if (gameLogic.getSession().getMotion() == Motion.Green && gameLogic.getSession().getSkillPoints()[0] > 0) {
                    int[] tmp = gameLogic.getSession().getPowers();
                    tmp[0] += gameLogic.getSession().getPowerDelta();
                    gameLogic.getSession().setPowers(tmp);

                    tmp = gameLogic.getSession().getSkillPoints();
                    tmp[0]--;
                    gameLogic.getSession().setSkillPoints(tmp);
                } else if (gameLogic.getSession().getSkillPoints()[1] > 0) {
                    int[] tmp = gameLogic.getSession().getPowers();
                    tmp[1] += gameLogic.getSession().getPowerDelta();
                    gameLogic.getSession().setPowers(tmp);

                    tmp = gameLogic.getSession().getSkillPoints();
                    tmp[1]--;
                    gameLogic.getSession().setSkillPoints(tmp);
                }
            } else if (button == Input.MOUSE_LEFT_BUTTON && gameLogic.getSession().getFlags().get(HIGHLIGHT_PROTECTION_INCREASE_BUTTON)) {
                if (gameLogic.getSession().getMotion() == Motion.Green && gameLogic.getSession().getSkillPoints()[0] > 0) {
                    int[] tmp = gameLogic.getSession().getProtections();
                    tmp[0] += gameLogic.getSession().getProtectionDelta();
                    gameLogic.getSession().setProtections(tmp);

                    tmp = gameLogic.getSession().getSkillPoints();
                    tmp[0]--;
                    gameLogic.getSession().setSkillPoints(tmp);
                } else if (gameLogic.getSession().getSkillPoints()[1] > 0) {
                    int[] tmp = gameLogic.getSession().getProtections();
                    tmp[1] += gameLogic.getSession().getProtectionDelta();
                    gameLogic.getSession().setProtections(tmp);

                    tmp = gameLogic.getSession().getSkillPoints();
                    tmp[1]--;
                    gameLogic.getSession().setSkillPoints(tmp);
                }
            } else if (button == Input.MOUSE_LEFT_BUTTON && gameLogic.getSession().getFlags().get(HIGHLIGHT_REPLICATION_INCREASE_BUTTON)) {
                if (gameLogic.getSession().getMotion() == Motion.Green && gameLogic.getSession().getSkillPoints()[0] > 0) {
                    int[] tmp = gameLogic.getSession().getReplications();
                    tmp[0] += gameLogic.getSession().getReplicationDelta();
                    gameLogic.getSession().setReplications(tmp);

                    tmp = gameLogic.getSession().getSkillPoints();
                    tmp[0]--;
                    gameLogic.getSession().setSkillPoints(tmp);
                } else if (gameLogic.getSession().getSkillPoints()[1] > 0) {
                    int[] tmp = gameLogic.getSession().getReplications();
                    tmp[1] += gameLogic.getSession().getReplicationDelta();
                    gameLogic.getSession().setReplications(tmp);

                    tmp = gameLogic.getSession().getSkillPoints();
                    tmp[1]--;
                    gameLogic.getSession().setSkillPoints(tmp);
                }
            }
        } else { // Если наступил конец игры, то обрабатываем нужно ли запустить её заново
            if (button == Input.MOUSE_LEFT_BUTTON) {
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_YES_BUTTON)) {
                    GameLogic.restartGame = RestartGame.YES;
                } else if (gameLogic.getSession().getFlags().get(HIGHLIGHT_NO_BUTTON)) {
                    GameLogic.restartGame = RestartGame.NO;
                }
            }
        }

        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_HELP_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            if (gameLogic.getSession().getFlags().get(OPEN_HELP_MENU)) {
                gameLogic.getSession().getFlags().put(OPEN_HELP_MENU, false);
            } else {
                gameLogic.getSession().getFlags().put(OPEN_HELP_MENU, true);
            }
        }
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SAVE_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            gameLogic.getSession().getFlags().put(SAVE_GAME, true);
        }
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_OPEN_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            gameLogic.getSession().getFlags().put(OPEN_GAME, true);
        }
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_REPEAT_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            gameLogic.getSession().getFlags().put(REPEAT_GAME, true);
        }
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_HOME_BUTTON) && button == Input.MOUSE_LEFT_BUTTON) {
            gameLogic.getSession().getFlags().put(EXIT_GAME, true);
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        g.setColor(new Color(76, 96, 133));
        g.fillRect(0, 0, gc.getWidth(), gc.getHeight());

        unit.setX1(150);
        g.drawImage(UI.backgroundImage, (float) gc.getWidth() / 3 - unit.getX1(), 0,
                (float) (gc.getWidth() * 2) / 3 + unit.getX1(),
                gc.getHeight(), 0, 0, UI.backgroundImage.getWidth(), UI.backgroundImage.getHeight());

        g.setColor(Color.white);

        // Рисуем горизонтальные линии
        for (int row = 0; row <= UI.rows; row++) {
            int lineY = UI.yLeftCorner + row * UI.cellSize;
            g.drawLine(UI.xLeftCorner, lineY, UI.xLeftCorner + UI.gridWidth, lineY);
        }

        // Рисуем вертикальные линии
        for (int col = 0; col <= UI.cols; col++) {
            int lineX = UI.xLeftCorner + col * UI.cellSize;
            g.drawLine(lineX, UI.yLeftCorner, lineX, UI.yLeftCorner + UI.gridHeight);
        }

        // Отрисовка вершин
        g.setLineWidth(3.0f);
        unit.setY1(35);
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : gameLogic.getSession().getVisualGraph().getGraph().entrySet()) {
            VisualNode visualNode = (VisualNode) entry.getKey();
            if (visualNode.equals(highlightedNode)) {
                g.setColor(HIGHLIGHT_NODE_COLOR); // Цвет подсветки
                g.drawString(String.valueOf(visualNode.getHp()), visualNode.getEllipse().getCenterX(),
                        visualNode.getEllipse().getCenterY() - unit.getY1());
            } else {
                g.setColor(visualNode.getEllipseColor());
                g.drawString(String.valueOf(visualNode.getHp()), visualNode.getEllipse().getCenterX(),
                        visualNode.getEllipse().getCenterY() - unit.getY1());
            }
            g.fill(visualNode.getEllipse());
            g.setColor(Color.black);
            g.drawOval(visualNode.getEllipse().getCenterX() - UI.nodeRadius,
                    visualNode.getEllipse().getCenterY() - UI.nodeRadius, UI.cellSize / 1.5f,
                    UI.cellSize / 1.5f);
        }

        // Отрисовка рёбер в компонентах
        unit.setX1(5);
        unit.setY1(5);
        unit.setWidth(10);
        unit.setHeight(10);
        int startX;
        int startY;
        int endX;
        int endY;
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : gameLogic.getSession().getVisualGraph().getGraph().entrySet()) {
            for (Map.Entry<Node, Connection> localEntry : entry.getValue().entrySet()) {
                VisualConnection vc = (VisualConnection) localEntry.getValue();
                g.setColor(vc.getColor());
                startX = (int) vc.getSrcNode().getEllipse().getCenterX();
                startY = (int) vc.getSrcNode().getEllipse().getCenterY();
                endX = (int) vc.getDestNode().getEllipse().getCenterX();
                endY = (int) vc.getDestNode().getEllipse().getCenterY();
                g.fillOval(startX - unit.getX1(), startY - unit.getY1(),
                        unit.getWidth(), unit.getHeight()); // Точка на начале отрезка
                g.fillOval(endX - unit.getX1(), endY - unit.getY1(),
                        unit.getWidth(), unit.getHeight()); // Точка на конце отрезка
                g.drawLine(startX, startY, endX, endY);
            }
        }

        // Отрисовка UI
        unit.setX1(470);
        unit.setY1(10);
        unit.setX2(20);
        unit.setY2(215);
        g.drawImage(BASIC_INFO_MENU.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(), 0, 0,
                BASIC_INFO_MENU.getImage().getWidth(), BASIC_INFO_MENU.getImage().getHeight());
        g.drawImage(BASIC_INFO_MENU.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                BASIC_INFO_MENU.getImage().getWidth(), BASIC_INFO_MENU.getImage().getHeight());
        unit.setX1(20);
        unit.setY1(150);
        unit.setX2(470);
        unit.setY2(35);
        g.drawImage(INFO_MENU.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                gc.getHeight() - unit.getY2(), 0, 0, INFO_MENU.getImage().getWidth(), INFO_MENU.getImage().getHeight());
        unit.setX1(470);
        unit.setX2(20);
        g.drawImage(INFO_MENU.getImage(), gc.getWidth() - unit.getX1(), gc.getHeight() - unit.getY1(),
                gc.getWidth() - unit.getX2(), gc.getHeight() - unit.getY2(), 0, 0, INFO_MENU.getImage().getWidth(),
                INFO_MENU.getImage().getHeight());
        g.setFont(UI.fontBold);

        // Дни
        unit.setX1(100);
        unit.setY1(20);
        unit.setY2(70);
        g.drawImage(REGULAR_BUTTON_HOVERED.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                (float) gc.getWidth() / 2 + unit.getX1(),
                unit.getY2(), 0, 0, REGULAR_BUTTON_HOVERED.getImage().getWidth(),
                REGULAR_BUTTON_HOVERED.getImage().getHeight());
        unit.setX1(35);
        unit.setY1(37);
        g.drawString("Day " + gameLogic.getSession().getDay(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());

        // Сообщение
        g.setFont(UI.fontMessage);
        if (gameLogic.getSession().getDay() == 1) {
            unit.setX1(150);
            unit.setY1(80);
            unit.setY2(105);
            g.drawImage(MESSAGE_CLOUD.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                    (float) gc.getWidth() / 2 + unit.getX1(), unit.getY2(),
                    0, 0, MESSAGE_CLOUD.getImage().getWidth(), MESSAGE_CLOUD.getImage().getHeight());
            unit.setX1(140);
            unit.setY1(88);
            if (gameLogic.getSession().getFlags().get(ERROR_INIT_VIRUS)) {
                g.drawString("Impossible to infect in day 1", (float) gc.getWidth() / 2 - unit.getX1(),
                        unit.getY1());
            } else {
                if (gameLogic.getSession().getMotion() == Motion.Green) {
                    switch (gameLogic.getSession().getGreenGraph().size()) {
                        case 0 -> g.drawString("Select the initial node to infect",
                                (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
                        case 1 -> g.drawString("Finish the move", (float) gc.getWidth() / 2 - unit.getX1(),
                                unit.getY1());
                    }
                } else {
                    switch (gameLogic.getSession().getBlueGraph().size()) {
                        case 0 -> g.drawString("Select the initial node to infect",
                                (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
                        case 1 -> g.drawString("Finish the move", (float) gc.getWidth() / 2 - unit.getX1(),
                                unit.getY1());
                    }
                }
            }
        } else if (gameLogic.getSession().getFlags().get(SELECTED_NODE_FROM_MOVE_VIRUS) && !gameLogic.getSession().getFlags().get(SELECTED_NODE_TO_MOVE_VIRUS)) {
            unit.setX1(150);
            unit.setY1(80);
            unit.setY2(105);
            g.drawImage(MESSAGE_CLOUD.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                    (float) gc.getWidth() / 2 + unit.getX1(), unit.getY2(),
                    0, 0, MESSAGE_CLOUD.getImage().getWidth(), MESSAGE_CLOUD.getImage().getHeight());
            unit.setX1(140);
            unit.setY1(88);
            g.drawString("Select second node to transfer virus", (float) gc.getWidth() / 2 - unit.getX1(),
                    unit.getY1());
            GameLogic.errorMotion = MotionError.OK;
        } else if (GameLogic.errorMotion != null && GameLogic.errorMotion != MotionError.OK) {
            unit.setX1(150);
            unit.setY1(80);
            unit.setY2(105);
            g.drawImage(MESSAGE_CLOUD.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                    (float) gc.getWidth() / 2 + unit.getX1(), unit.getY2(),
                    0, 0, MESSAGE_CLOUD.getImage().getWidth(), MESSAGE_CLOUD.getImage().getHeight());
            unit.setX1(140);
            unit.setY1(88);
            switch (GameLogic.errorMotion) {
                case NOT_YOUR_MOTION ->
                        g.drawString(gameLogic.getSession().getMotion() + " now!", (float) gc.getWidth() / 2 - unit.getX1(),
                                unit.getY1());
                case RED_NODE_SELECTED -> g.drawString("Red node cannot be selected!",
                        (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
                case NOT_ADJACENT -> g.drawString("Nodes are not adjacent!",
                        (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
                case SAME_NODE -> g.drawString("This is the same node!",
                        (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
                case MAX_VALUE_OF_VIRUS -> g.drawString("The virus is already maximum!",
                        (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
            }
        } else if (gameLogic.getSession().getFlags().get(MOVE_VIRUS_MODE)) { // Отправка вируса в другую вершину
            if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_ALL_VIRUS)) {
                GameLogic.errorMotion = gameLogic.moveVirus(GameLogic.startVirusMove, GameLogic.endVirusMove, MoveVirusPart.ALL);
                gameLogic.getSession().getFlags().put(MOVE_VIRUS_DONE, true);
            } else if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_HALF_VIRUS)) {
                GameLogic.errorMotion = gameLogic.moveVirus(GameLogic.startVirusMove, GameLogic.endVirusMove, MoveVirusPart.HALF);
                gameLogic.getSession().getFlags().put(MOVE_VIRUS_DONE, true);
            } else if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_QUARTER_VIRUS)) {
                GameLogic.errorMotion = gameLogic.moveVirus(GameLogic.startVirusMove, GameLogic.endVirusMove, MoveVirusPart.QUARTER);
                gameLogic.getSession().getFlags().put(MOVE_VIRUS_DONE, true);
            }

            // Пересылка вируса закончилась
            if (gameLogic.getSession().getFlags().get(MOVE_VIRUS_DONE)) {
                gameLogic.getSession().getFlags().put(SELECTED_NODE_FROM_MOVE_VIRUS, false);
                gameLogic.getSession().getFlags().put(SELECTED_NODE_TO_MOVE_VIRUS, false);
                gameLogic.getSession().getFlags().put(MOVE_VIRUS_MODE, false);
                gameLogic.getSession().getFlags().put(MOVE_VIRUS_DONE, false);
            }
        } else if (gameLogic.getSession().getFlags().get(SELECTED_NODE_TO_MOVE_VIRUS)) { // Показывает меню для выбора кол-ва пересылки вируса
            unit.setX1(150);
            unit.setY1(25);
            g.drawImage(LONG_MENU.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                    (float) gc.getHeight() / 2 - unit.getY1(), (float) gc.getWidth() / 2 + unit.getX1(),
                    (float) gc.getHeight() / 2 + unit.getY1(), 0, 0, LONG_MENU.getImage().getWidth(),
                    LONG_MENU.getImage().getHeight());
            unit.setX1(115);
            unit.setY1(15);
            unit.setX2(75);
            if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_ALL_VIRUS)) {
                g.drawImage(BUTTON_ALL_VIRUS_MOVE_HOVERED.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        BUTTON_ALL_VIRUS_MOVE_HOVERED.getImage().getWidth(),
                        BUTTON_ALL_VIRUS_MOVE_HOVERED.getImage().getHeight());
            } else {
                g.drawImage(BUTTON_ALL_VIRUS_MOVE.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        BUTTON_ALL_VIRUS_MOVE.getImage().getWidth(),
                        BUTTON_ALL_VIRUS_MOVE.getImage().getHeight());
            }
            unit.setX1(38);
            if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_HALF_VIRUS)) {
                g.drawImage(BUTTON_HALF_VIRUS_MOVE_HOVERED.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        BUTTON_HALF_VIRUS_MOVE_HOVERED.getImage().getWidth(),
                        BUTTON_HALF_VIRUS_MOVE_HOVERED.getImage().getHeight());
            } else {
                g.drawImage(BUTTON_HALF_VIRUS_MOVE.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        BUTTON_HALF_VIRUS_MOVE.getImage().getWidth(),
                        BUTTON_HALF_VIRUS_MOVE.getImage().getHeight());
            }
            unit.setX1(39);
            if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SEND_QUARTER_VIRUS)) {
                g.drawImage(BUTTON_QUARTER_VIRUS_MOVE_HOVERED.getImage(), (float) gc.getWidth() / 2 + unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 + unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        BUTTON_QUARTER_VIRUS_MOVE_HOVERED.getImage().getWidth(),
                        BUTTON_QUARTER_VIRUS_MOVE_HOVERED.getImage().getHeight());
            } else {
                g.drawImage(BUTTON_QUARTER_VIRUS_MOVE.getImage(), (float) gc.getWidth() / 2 + unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 + unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        BUTTON_QUARTER_VIRUS_MOVE.getImage().getWidth(),
                        BUTTON_QUARTER_VIRUS_MOVE.getImage().getHeight());
            }
        }

        g.setFont(UI.fontBold);
        // Отрисовка кнопки завершения хода
        if (endGameWin == EndGameWin.NONE) {
            if (gameLogic.getSession().getMotion() == Motion.Green && gameLogic.getSession().getGreenGraph().size() != 0) {
                unit.setY1(230);
                unit.setX2(200);
                unit.setY2(280);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_BUTTON)) {
                    g.drawImage(REGULAR_BUTTON_HOVERED.getImage(), (float) gc.getWidth() / 14,
                            unit.getY1(), (float) gc.getWidth() / 14 + unit.getX2(), unit.getY2(), 0, 0,
                            REGULAR_BUTTON_HOVERED.getImage().getWidth(), REGULAR_BUTTON_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(REGULAR_BUTTON.getImage(), (float) gc.getWidth() / 14,
                            unit.getY1(), (float) gc.getWidth() / 14 + unit.getX2(), unit.getY2(), 0, 0,
                            REGULAR_BUTTON.getImage().getWidth(), REGULAR_BUTTON.getImage().getHeight());
                }
                unit.setX1(31);
                unit.setY1(247);
                g.drawString("Finish move", (float) gc.getWidth() / 14 + unit.getX1(), unit.getY1());
            } else if (gameLogic.getSession().getMotion() == Motion.Blue && gameLogic.getSession().getBlueGraph().size() != 0) {
                unit.setX1(350);
                unit.setX2(200);
                unit.setY1(230);
                unit.setY2(280);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_BUTTON)) {
                    g.drawImage(REGULAR_BUTTON_HOVERED.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX1() + unit.getX2(), unit.getY2(), 0, 0,
                            REGULAR_BUTTON.getImage().getWidth(), REGULAR_BUTTON.getImage().getHeight());
                } else {
                    g.drawImage(REGULAR_BUTTON.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX1() + unit.getX2(), unit.getY2(), 0, 0,
                            REGULAR_BUTTON.getImage().getWidth(), REGULAR_BUTTON.getImage().getHeight());
                }
                unit.setX2(318);
                unit.setY2(247);
                g.drawString("Finish move", gc.getWidth() - unit.getX2(), unit.getY2());
            }
        } else { // Отрисовка сообщения о победе
            unit.setX1(150);
            unit.setY1(80);
            unit.setY2(105);
            g.setFont(UI.fontMessage);
            if (endGameWin == EndGameWin.RED) {
                g.drawImage(MESSAGE_CLOUD.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                        (float) gc.getWidth() / 2 + unit.getX1(), unit.getY2(),
                        0, 0, MESSAGE_CLOUD.getImage().getWidth(), MESSAGE_CLOUD.getImage().getHeight());
                unit.setX1(140);
                unit.setY1(88);
                g.drawString("Red wins! Restart Game?", (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
            } else if (endGameWin == EndGameWin.GREEN) {
                g.drawImage(MESSAGE_CLOUD.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                        (float) gc.getWidth() / 2 + unit.getX1(), unit.getY2(),
                        0, 0, MESSAGE_CLOUD.getImage().getWidth(), MESSAGE_CLOUD.getImage().getHeight());
                unit.setX1(140);
                unit.setY1(88);
                g.drawString("Green wins! Restart Game?", (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
            } else if (endGameWin == EndGameWin.BLUE) {
                g.drawImage(MESSAGE_CLOUD.getImage(), (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1(),
                        (float) gc.getWidth() / 2 + unit.getX1(), unit.getY2(),
                        0, 0, MESSAGE_CLOUD.getImage().getWidth(), MESSAGE_CLOUD.getImage().getHeight());
                unit.setX1(140);
                unit.setY1(88);
                g.drawString("Blue wins! Restart Game?", (float) gc.getWidth() / 2 - unit.getX1(), unit.getY1());
            }
        }

        g.setFont(UI.fontBold);
        unit.setX1(100);
        unit.setY1(80);
        unit.setX2(200);
        unit.setY2(30);
        // Отображение очередности хода
        g.drawImage(REGULAR_BUTTON.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                gc.getHeight() - unit.getY1(), (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                gc.getHeight() - unit.getY2(), 0, 0,
                REGULAR_BUTTON.getImage().getWidth(), REGULAR_BUTTON.getImage().getHeight());
        unit.setX1(65);
        unit.setY1(63);
        g.drawString(gameLogic.getSession().getMotion().getStringMotion(), (float) gc.getWidth() / 2 - unit.getX1(),
                gc.getHeight() - unit.getY1());

        // Основная информация
        unit.setX1(40);
        unit.setY1(30);
        g.drawString("Green virus", unit.getX1(), unit.getY1());

        g.setFont(UI.font);
        unit.setY1(60);
        g.drawString("Number of infected vertices: " + gameLogic.getSession().getVirusNodes()[0], unit.getX1(), unit.getY1());
        unit.setY1(90);
        g.drawString("Skill points: " + gameLogic.getSession().getSkillPoints()[0], unit.getX1(), unit.getY1());
        unit.setY1(120);
        g.drawString("Power: " + gameLogic.getSession().getPowers()[0], unit.getX1(), unit.getY1());
        unit.setY1(150);
        g.drawString("Protection: " + gameLogic.getSession().getProtections()[0], unit.getX1(), unit.getY1());
        unit.setY1(180);
        g.drawString("Replication: " + gameLogic.getSession().getReplications()[0], unit.getX1(), unit.getY1());

        unit.setY1(130);
        g.drawString("Next power: " + (gameLogic.getSession().getPowers()[0] + gameLogic.getSession().getPowerDelta()), unit.getX1(), gc.getHeight() - unit.getY1());
        unit.setY1(100);
        g.drawString("Next protection: " + (gameLogic.getSession().getProtections()[0] + gameLogic.getSession().getProtectionDelta()), unit.getX1(),
                gc.getHeight() - unit.getY1());
        unit.setY1(70);
        g.drawString("Next replication: " + (gameLogic.getSession().getReplications()[0] + gameLogic.getSession().getReplicationDelta()), unit.getX1(),
                gc.getHeight() - unit.getY1());

        g.setFont(UI.fontBold);
        unit.setX1(450);
        unit.setY1(30);
        g.drawString("Blue virus", gc.getWidth() - unit.getX1(), unit.getY1());

        g.setFont(UI.font);
        unit.setY1(60);
        g.drawString("Number of infected vertices: " + gameLogic.getSession().getVirusNodes()[1], gc.getWidth() - unit.getX1(),
                unit.getY1());
        unit.setY1(90);
        g.drawString("Skill points: " + gameLogic.getSession().getSkillPoints()[1], gc.getWidth() - unit.getX1(), unit.getY1());
        unit.setY1(120);
        g.drawString("Power: " + gameLogic.getSession().getPowers()[1], gc.getWidth() - unit.getX1(), unit.getY1());
        unit.setY1(150);
        g.drawString("Protection: " + gameLogic.getSession().getProtections()[1], gc.getWidth() - unit.getX1(), unit.getY1());
        unit.setY1(180);
        g.drawString("Replication: " + gameLogic.getSession().getReplications()[1], gc.getWidth() - unit.getX1(), unit.getY1());

        unit.setY1(130);
        g.drawString("Next power: " + (gameLogic.getSession().getPowers()[1] + gameLogic.getSession().getPowerDelta()), gc.getWidth() - unit.getX1(),
                gc.getHeight() - unit.getY1());
        unit.setY1(100);
        g.drawString("Next protection: " + (gameLogic.getSession().getProtections()[1] + gameLogic.getSession().getProtectionDelta()), gc.getWidth() - unit.getX1(),
                gc.getHeight() - unit.getY1());
        unit.setY1(70);
        g.drawString("Next replication: " + (gameLogic.getSession().getReplications()[1] + gameLogic.getSession().getReplicationDelta()), gc.getWidth() - unit.getX1(),
                gc.getHeight() - unit.getY1());

        // Отрисовка кнопок улучшений навыков (плюсики)
        if (endGameWin == EndGameWin.NONE) {
            if (gameLogic.getSession().getMotion() == Motion.Green && gameLogic.getSession().getGreenGraph().size() != 0 && gameLogic.getSession().getSkillPoints()[0] > 0) {
                unit.setX1(430);
                unit.setY1(119);
                unit.setX2(446);
                unit.setY2(135);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_POWER_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(),
                            0, 0, PLUS_HOVERED.getImage().getWidth(), PLUS_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(PLUS.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(),
                            0, 0, PLUS.getImage().getWidth(), PLUS.getImage().getHeight());
                }
                unit.setY1(149);
                unit.setY2(165);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_PROTECTION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(),
                            0, 0, PLUS_HOVERED.getImage().getWidth(), PLUS_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(PLUS.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(),
                            0, 0, PLUS.getImage().getWidth(), PLUS.getImage().getHeight());
                }
                unit.setY1(179);
                unit.setY2(195);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_REPLICATION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(),
                            0, 0, PLUS_HOVERED.getImage().getWidth(), PLUS_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(PLUS.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(),
                            0, 0, PLUS.getImage().getWidth(), PLUS.getImage().getHeight());
                }
            } else if (gameLogic.getSession().getMotion() == Motion.Blue && gameLogic.getSession().getBlueGraph().size() != 0 && gameLogic.getSession().getSkillPoints()[1] > 0) {
                unit.setX1(60);
                unit.setY1(119);
                unit.setX2(44);
                unit.setY2(135);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_POWER_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                            PLUS_HOVERED.getImage().getWidth(), PLUS_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(PLUS.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                            PLUS.getImage().getWidth(), PLUS.getImage().getHeight());
                }
                unit.setY1(149);
                unit.setY2(165);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_PROTECTION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                            PLUS_HOVERED.getImage().getWidth(), PLUS_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(PLUS.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                            PLUS.getImage().getWidth(), PLUS.getImage().getHeight());
                }
                unit.setY1(179);
                unit.setY2(195);
                if (gameLogic.getSession().getFlags().get(HIGHLIGHT_REPLICATION_INCREASE_BUTTON)) {
                    g.drawImage(PLUS_HOVERED.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                            PLUS_HOVERED.getImage().getWidth(), PLUS_HOVERED.getImage().getHeight());
                } else {
                    g.drawImage(PLUS.getImage(), gc.getWidth() - unit.getX1(), unit.getY1(),
                            gc.getWidth() - unit.getX2(), unit.getY2(), 0, 0,
                            PLUS.getImage().getWidth(), PLUS.getImage().getHeight());
                }
            }
        } else { // Отрисовка меню завершения игры
            unit.setX1(100);
            unit.setY1(25);
            g.drawImage(REGULAR_BUTTON.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                    (float) gc.getHeight() / 2 - unit.getY1(), (float) gc.getWidth() / 2 + unit.getX1(),
                    (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                    REGULAR_BUTTON.getImage().getWidth(), REGULAR_BUTTON.getImage().getHeight());
            unit.setX1(79);
            unit.setY1(15);
            unit.setX2(75);
            if (gameLogic.getSession().getFlags().get(HIGHLIGHT_YES_BUTTON)) {
                g.drawImage(YES_BUTTON.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        YES_BUTTON.getImage().getWidth(), YES_BUTTON.getImage().getHeight());
            } else {
                g.drawImage(YES_BUTTON_HOVERED.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        YES_BUTTON_HOVERED.getImage().getWidth(), YES_BUTTON_HOVERED.getImage().getHeight());
            }
            unit.setX1(1);
            if (gameLogic.getSession().getFlags().get(HIGHLIGHT_NO_BUTTON)) {
                g.drawImage(NO_BUTTON.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        NO_BUTTON.getImage().getWidth(), NO_BUTTON.getImage().getHeight());
            } else {
                g.drawImage(NO_BUTTON_HOVERED.getImage(), (float) gc.getWidth() / 2 - unit.getX1(),
                        (float) gc.getHeight() / 2 - unit.getY1(),
                        (float) gc.getWidth() / 2 - unit.getX1() + unit.getX2(),
                        (float) gc.getHeight() / 2 + unit.getY1(), 0, 0,
                        NO_BUTTON_HOVERED.getImage().getWidth(), NO_BUTTON_HOVERED.getImage().getHeight());
            }
            if (GameLogic.restartGame == RestartGame.YES) {
                init(gc);
            } else if (GameLogic.restartGame == RestartGame.NO) {
                exit(0);
            }
        }

        // Отрисовка основных кнопок меню
        unit.setX1(1350);
        unit.setY1(20);
        unit.setX2(1400);
        unit.setY2(70);
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_HOME_BUTTON)) {
            g.drawImage(HOME_BUTTON_HOVERED.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(), 0,
                    0, HOME_BUTTON_HOVERED.getImage().getWidth(), HOME_BUTTON_HOVERED.getImage().getHeight());
        } else {
            g.drawImage(HOME_BUTTON.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(), 0, 0,
                    HOME_BUTTON.getImage().getWidth(), HOME_BUTTON.getImage().getHeight());
        }
        unit.setX1(520);
        unit.setX2(570);
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_REPEAT_BUTTON)) {
            g.drawImage(REPEAT_BUTTON_HOVERED.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(), 0,
                    0, REPEAT_BUTTON_HOVERED.getImage().getWidth(), REPEAT_BUTTON_HOVERED.getImage().getHeight());
        } else {
            g.drawImage(REPEAT_BUTTON.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(), 0,
                    0, REPEAT_BUTTON.getImage().getWidth(), REPEAT_BUTTON.getImage().getHeight());
        }
        unit.setX1(1350);
        unit.setX2(1400);
        unit.setY1(80);
        unit.setY2(30);
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_HELP_BUTTON)) {
            g.drawImage(HELP_BUTTON_HOVERED.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                    gc.getHeight() - unit.getY2(), 0, 0, HELP_BUTTON_HOVERED.getImage().getWidth(),
                    HELP_BUTTON_HOVERED.getImage().getHeight());
        } else {
            g.drawImage(HELP_BUTTON.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                    gc.getHeight() - unit.getY2(), 0, 0, HELP_BUTTON.getImage().getWidth(),
                    HELP_BUTTON.getImage().getHeight());
        }

        // Сохранение
        unit.setX1(520);
        unit.setX2(570);
        unit.setY1(80);
        unit.setY2(30);
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_SAVE_BUTTON)) {
            g.drawImage(SAVE_BUTTON_HOVERED.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                    gc.getHeight() - unit.getY2(), 0, 0, SAVE_BUTTON_HOVERED.getImage().getWidth(),
                    SAVE_BUTTON_HOVERED.getImage().getHeight());
        } else {
            g.drawImage(SAVE_BUTTON.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                    gc.getHeight() - unit.getY2(), 0, 0, SAVE_BUTTON.getImage().getWidth(),
                    SAVE_BUTTON.getImage().getHeight());
        }

        // Открытие
        unit.setX1(580);
        unit.setX2(630);
        unit.setY1(80);
        unit.setY2(30);
        if (gameLogic.getSession().getFlags().get(HIGHLIGHT_OPEN_BUTTON)) {
            g.drawImage(OPEN_SAVING_BUTTON_HOVERED.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                    gc.getHeight() - unit.getY2(), 0, 0, OPEN_SAVING_BUTTON_HOVERED.getImage().getWidth(),
                    OPEN_SAVING_BUTTON_HOVERED.getImage().getHeight());
        } else {
            g.drawImage(OPEN_SAVING_BUTTON.getImage(), unit.getX1(), gc.getHeight() - unit.getY1(), unit.getX2(),
                    gc.getHeight() - unit.getY2(), 0, 0, OPEN_SAVING_BUTTON.getImage().getWidth(),
                    OPEN_SAVING_BUTTON.getImage().getHeight());
        }


        // Открытие правил
        unit.setX1(550);
        unit.setX2(1350);
        unit.setY1(100);
        unit.setY2(900);
        if (gameLogic.getSession().getFlags().get(OPEN_HELP_MENU)) {
            g.drawImage(RULES_MENU.getImage(), unit.getX1(), unit.getY1(), unit.getX2(), unit.getY2(), 0, 0,
                    RULES_MENU.getImage().getWidth(), RULES_MENU.getImage().getHeight());
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
            unit.setX1(580);
            unit.setY1(130);
            g.drawString(rules, unit.getX1(), unit.getY1());
        }

        if (gameLogic.getSession().getFlags().get(SAVE_GAME)) {
            gameLogic.getSession().getFlags().put(SAVE_GAME, false);
            GameSerialization.saveGameToFile(gameLogic.getSession());
        }

        if (gameLogic.getSession().getFlags().get(OPEN_GAME)) {
            gameLogic.getSession().getFlags().put(OPEN_GAME, false);
            gameLogic.setSession(GameSerialization.openGameFromFile(gc, gameLogic.getSession()));
        }

        if (gameLogic.getSession().getFlags().get(REPEAT_GAME)) {
            gameLogic.getSession().getFlags().put(REPEAT_GAME, false);
            init(gc);
        }

        if (gameLogic.getSession().getFlags().get(EXIT_GAME)) {
            exit(0);
        }
    }
}