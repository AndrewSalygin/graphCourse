package ru.andrewsalygin.graph.game;

import lombok.Getter;
import lombok.Setter;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.utils.*;
import ru.andrewsalygin.graph.game.visualgraph.VisualGraph;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ru.andrewsalygin.graph.game.Game.*;
import static ru.andrewsalygin.graph.game.utils.MotionError.*;

/**
 * @author Andrew Salygin
 */
@Getter
@Setter
public class GameLogic {
    private static final int MAX_VIRUS_HP = 300;
    private static final Random random = new Random();
    public static MotionError errorMotion;
    public static VisualNode highlightedNode;
    public static VisualNode startVirusMove;
    public static VisualNode endVirusMove;
    public static RestartGame restartGame = RestartGame.NONE;
    public static EndGameWin endGameWin = EndGameWin.NONE;
    private Session session;

    public void startGame(GameContainer gc) {
        HashMap<Flag, Boolean> flags = new HashMap<>();
        for (Flag flag : Flag.values()) {
            flags.put(flag, false);
        }

        UI.xLeftCorner = (gc.getWidth() - UI.gridWidth) / 2;
        UI.yLeftCorner = (gc.getHeight() - UI.gridHeight) / 2;

        VisualGraph visualGraph = new VisualGraph();

        // Распределяем компоненты случайным образом по игровому полю
        visualGraph.randomizeComponentPlacements(UI.xLeftCorner, UI.yLeftCorner);
        // Соедините компоненты связности между собой, добавив рёбра
        visualGraph.connectComponents();

        // Больше отдельные компоненты не нужны
        visualGraph.setComponents(null);

        // Инициализация здоровых вершин
        HashMap<VisualNode, HashMap<Node, Connection>> redGraph = new HashMap<>();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : visualGraph.getGraph().entrySet()) {
            VisualNode node = (VisualNode) entry.getKey();
            if (node.getEllipseColor().equals(Color.red) ||
                    node.getEllipseColor().equals(new Color(163, 0, 0))) {
                redGraph.put(node, entry.getValue());
            }
        }

        restartGame = RestartGame.NONE;
        endGameWin = EndGameWin.NONE;

        session = new Session(new int[]{0, 0}, new int[]{0, 0}, new int[]{10, 10}, new int[]{10, 10},
                new int[]{10, 10}, 5, 5, 5, Motion.Green, 1, flags,
                new HashMap<>(),
                new HashMap<>(),
                redGraph, visualGraph);
    }

    public MotionError moveVirus(VisualNode startVirusMove, VisualNode endVirusMove, MoveVirusPart virusPart) {
        HashMap<Node, HashMap<Node, Connection>> graph = session.getVisualGraph().getGraph();
        Motion motion = session.getMotion();
        int[] powers = session.getPowers();
        int[] protections = session.getProtections();
        int[] skillPoints = session.getSkillPoints();

        HashMap<VisualNode, HashMap<Node, Connection>> redGraph = session.getRedGraph();
        HashMap<VisualNode, HashMap<Node, Connection>> greenGraph = session.getGreenGraph();
        HashMap<VisualNode, HashMap<Node, Connection>> blueGraph = session.getBlueGraph();


        int valueToMove = 0;
        int hpToAdd;
        int virusAttack;
        int virusProtectionOther;
        int healthNodeProtection;
        int firstValue;
        int secondValue;
        int protectionHealthNode;

        // Пересылка вируса в ту же вершину невозможна
        if (startVirusMove.equals(endVirusMove)) {
            return SAME_NODE;
        }

        // Определяем количество пересылаемого вируса
        if (virusPart == MoveVirusPart.ALL) {
            valueToMove = startVirusMove.getHp();
        }
        else if (virusPart == MoveVirusPart.HALF) {
            valueToMove = startVirusMove.getHp() / 2;
        } else if (virusPart == MoveVirusPart.QUARTER) {
            valueToMove = startVirusMove.getHp() / 4;
        }

        // Если конечная вершина смежная
        if (graph.get(startVirusMove).containsKey(endVirusMove)) {
            if (motion == Motion.Green) {
                // Конечная вершина тот же вирус
                if (startVirusMove.getEllipseColor().equals(Color.green)) {
                    if (endVirusMove.getEllipseColor().equals(Color.green)) {
                        if (endVirusMove.getHp() < MAX_VIRUS_HP) {
                            // Количество переданного будет меньше максимального
                            if (endVirusMove.getHp() + valueToMove <= MAX_VIRUS_HP) {
                                endVirusMove.setHp(endVirusMove.getHp() + valueToMove);
                                startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            }
                            else { // Больше максимального
                                hpToAdd = MAX_VIRUS_HP - endVirusMove.getHp();
                                endVirusMove.setHp(MAX_VIRUS_HP);
                                startVirusMove.setHp(startVirusMove.getHp() - hpToAdd);
                            }
                        } else { // Уже максимум
                            return MAX_VALUE_OF_VIRUS;
                        }
                        // Конечная вершина другой вирус
                    } else if (endVirusMove.getEllipseColor().equals(Color.blue)) {
                        virusAttack = valueToMove * powers[0];
                        virusProtectionOther = endVirusMove.getHp() * protections[1];
                        firstValue = (virusAttack - virusProtectionOther) / powers[0];
                        secondValue = (virusProtectionOther - virusAttack) / protections[1];

                        // Случай, когда вирусы взаимоуничтожают друг друга
                        if (virusAttack == virusProtectionOther) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(0);
                            endVirusMove.setEllipseColor(new Color(163, 0, 0));
                            redGraph.put(endVirusMove, blueGraph.get(endVirusMove));
                            blueGraph.remove(endVirusMove);
                        }
                        // Зелёный сильнее
                        else if (firstValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(firstValue);
                            endVirusMove.setEllipseColor(Color.green);
                            greenGraph.put(endVirusMove, blueGraph.get(endVirusMove));
                            blueGraph.remove(endVirusMove);
                        }
                        // Синий сильнее
                        else if (secondValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(secondValue);
                        }

                        // Конечная вершина здоровая
                    } else if (endVirusMove.getEllipseColor().equals(Color.red) ||
                            endVirusMove.getEllipseColor().equals(new Color(163, 0, 0))) {
                        virusAttack = valueToMove * powers[0];
                        protectionHealthNode = random.nextInt(MAX_VALUE_REGENERATION_HEALTH_NODE - MIN_VALUE_REGENERATION_HEALTH_NODE + 1) + MIN_VALUE_REGENERATION_HEALTH_NODE;
                        healthNodeProtection = endVirusMove.getHp() * protectionHealthNode;
                        firstValue = (virusAttack - healthNodeProtection) / powers[0];
                        secondValue = (healthNodeProtection - virusAttack) / protectionHealthNode;

                        // Аналогично ситуациям выше
                        if (virusAttack == healthNodeProtection) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(0);
                        }
                        else if (firstValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(firstValue);
                            endVirusMove.setEllipseColor(Color.green);
                            if (endVirusMove.isSkillPoint()) {
                                skillPoints[0]++;
                                endVirusMove.setSkillPoint(false);
                            }
                            greenGraph.put(endVirusMove, redGraph.get(endVirusMove));
                            redGraph.remove(endVirusMove);
                        }
                        else if (secondValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(secondValue);
                        }
                    }

                    // В качестве начальной выбрана здоровая вершина, что недопустимо
                } else if (startVirusMove.getEllipseColor().equals(Color.red) ||
                        startVirusMove.getEllipseColor().equals(new Color(163, 0, 0))) {
                    return RED_NODE_SELECTED;
                }
                else { // Текущий ход синих
                    return NOT_YOUR_MOTION;
                }
            }
            else if (motion == Motion.Blue) { // Аналогично ситуациям выше, только для синих
                 if (startVirusMove.getEllipseColor().equals(Color.blue)) {
                     if (endVirusMove.getEllipseColor().equals(Color.blue)) {
                         if (endVirusMove.getHp() < MAX_VIRUS_HP) {
                             if (endVirusMove.getHp() + valueToMove <= MAX_VIRUS_HP) {
                                 endVirusMove.setHp(endVirusMove.getHp() + valueToMove);
                                 startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             }
                             else {
                                 hpToAdd = MAX_VIRUS_HP - endVirusMove.getHp();
                                 endVirusMove.setHp(MAX_VIRUS_HP);
                                 startVirusMove.setHp(startVirusMove.getHp() - hpToAdd);
                             }
                         } else {
                             return MAX_VALUE_OF_VIRUS;
                         }
                     } else if (endVirusMove.getEllipseColor().equals(Color.green)) {
                         virusAttack = valueToMove * powers[0];
                         virusProtectionOther = endVirusMove.getHp() * protections[1];
                         firstValue = (virusAttack - virusProtectionOther) / powers[0];
                         secondValue = (virusProtectionOther - virusAttack) / protections[1];

                         if (virusAttack == virusProtectionOther) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(0);
                             endVirusMove.setEllipseColor(new Color(163, 0, 0));
                             redGraph.put(endVirusMove, blueGraph.get(endVirusMove));
                             blueGraph.remove(endVirusMove);
                         }
                         else if (firstValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(firstValue);
                             endVirusMove.setEllipseColor(Color.blue);
                             blueGraph.put(endVirusMove, greenGraph.get(endVirusMove));
                             greenGraph.remove(endVirusMove);
                         }
                         else if (secondValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(secondValue);
                         }
                     }
                     else if (endVirusMove.getEllipseColor().equals(Color.red) ||
                             endVirusMove.getEllipseColor().equals(new Color(163, 0, 0))) {
                         virusAttack = valueToMove * powers[1];
                         protectionHealthNode = random.nextInt(MAX_VALUE_REGENERATION_HEALTH_NODE - MIN_VALUE_REGENERATION_HEALTH_NODE + 1) + MIN_VALUE_REGENERATION_HEALTH_NODE;
                         healthNodeProtection = endVirusMove.getHp() * protectionHealthNode;
                         firstValue = (virusAttack - healthNodeProtection) / powers[1];
                         secondValue = (healthNodeProtection - virusAttack) / protectionHealthNode;

                         if (virusAttack == healthNodeProtection) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(0);
                         }
                         else if (firstValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(firstValue);
                             endVirusMove.setEllipseColor(Color.blue);
                             if (endVirusMove.isSkillPoint()) {
                                 skillPoints[1]++;
                                 endVirusMove.setSkillPoint(false);
                             }
                             blueGraph.put(endVirusMove, redGraph.get(endVirusMove));
                             redGraph.remove(endVirusMove);
                         }
                         else if (secondValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(secondValue);
                         }
                     }
                 }
                 else if (startVirusMove.getEllipseColor().equals(Color.red) ||
                         startVirusMove.getEllipseColor().equals(new Color(163, 0, 0))) {
                     return RED_NODE_SELECTED;
                 }
                 else {
                     return NOT_YOUR_MOTION;
                 }
            }
        } else {
            return NOT_ADJACENT;
        }
        return OK;
    }
}
