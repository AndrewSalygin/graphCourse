package ru.andrewsalygin.graph.game;

import org.newdawn.slick.Color;
import ru.andrewsalygin.graph.core.Connection;
import ru.andrewsalygin.graph.core.Node;
import ru.andrewsalygin.graph.game.utils.Motion;
import ru.andrewsalygin.graph.game.utils.MotionError;
import ru.andrewsalygin.graph.game.utils.MoveVirusPart;
import ru.andrewsalygin.graph.game.visualgraph.VisualNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ru.andrewsalygin.graph.game.Game.*;
import static ru.andrewsalygin.graph.game.utils.MotionError.*;

/**
 * @author Andrew Salygin
 */
public class GameLogic {
    private static final int MAX_VIRUS_HP = 300;
    private static final Random random = new Random();
    static int[] virusNodes;
    static int[] skillPoints;
    static int[] powers;
    static int[] protections;
    static int[] replications;
    static int protectionHealthNode;
    static int replicationHealthNode;
    static int powerDelta;
    static int protectionDelta;
    static int replicationDelta;
    static Motion motion;
    static int day;

    public static void startGame() {
        virusNodes = new int[]{0, 0};
        skillPoints = new int[]{0, 0};
        powers = new int[]{10, 10};
        protections = new int[]{10, 10};
        replications = new int[]{10, 10};
        powerDelta = 5;
        protectionDelta = 5;
        replicationDelta = 5;
        motion = Motion.Green;
        day = 1;

        // Инициализация здоровых вершин
        Game.redGraph = new HashMap<>();
        for (Map.Entry<Node, HashMap<Node, Connection>> entry : Game.visualGraph.getGraph().entrySet()) {
            VisualNode node = (VisualNode) entry.getKey();
            if (node.getEllipseColor() == Color.red ||
                    node.getEllipseColor().equals(new Color(163, 0, 0))) {
                Game.redGraph.put(node, entry.getValue());
            }
        }
    }

    public static MotionError moveVirus(VisualNode startVirusMove, VisualNode endVirusMove, MoveVirusPart virusPart) {
        HashMap<Node, HashMap<Node, Connection>> graph = Game.visualGraph.getGraph();
        int valueToMove = 0;
        int hpToAdd;
        int virusAttack;
        int virusProtectionOther;
        int healthNodeProtection;
        int firstValue;
        int secondValue;

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
                if (startVirusMove.getEllipseColor() == Color.green) {
                    if (endVirusMove.getEllipseColor() == Color.green) {
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
                    } else if (endVirusMove.getEllipseColor() == Color.blue) {
                        virusAttack = valueToMove * powers[0];
                        virusProtectionOther = endVirusMove.getHp() * protections[1];
                        firstValue = (virusAttack - virusProtectionOther) / powers[0];
                        secondValue = (virusProtectionOther - virusAttack) / protections[1];

                        // Случай, когда вирусы взаимоуничтожают друг друга
                        if (virusAttack == virusProtectionOther) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(0);
                            endVirusMove.setEllipseColor(new Color(163, 0, 0));
                            Game.redGraph.put(endVirusMove, Game.blueGraph.get(endVirusMove));
                            Game.blueGraph.remove(endVirusMove);
                        }
                        // Зелёный сильнее
                        else if (firstValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(firstValue);
                            endVirusMove.setEllipseColor(Color.green);
                            Game.greenGraph.put(endVirusMove, Game.blueGraph.get(endVirusMove));
                            Game.blueGraph.remove(endVirusMove);
                        }
                        // Синий сильнее
                        else if (secondValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(secondValue);
                        }

                        // Конечная вершина здоровая
                    } else if (endVirusMove.getEllipseColor() == Color.red ||
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
                            Game.greenGraph.put(endVirusMove, Game.redGraph.get(endVirusMove));
                            Game.redGraph.remove(endVirusMove);
                        }
                        else if (secondValue > 0) {
                            startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                            endVirusMove.setHp(secondValue);
                        }
                    }

                    // В качестве начальной выбрана здоровая вершина, что недопустимо
                } else if (startVirusMove.getEllipseColor() == Color.red ||
                        startVirusMove.getEllipseColor().equals(new Color(163, 0, 0))) {
                    return RED_NODE_SELECTED;
                }
                else { // Текущий ход синих
                    return NOT_YOUR_MOTION;
                }
            }
            else if (motion == Motion.Blue) { // Аналогично ситуациям выше, только для синих
                 if (startVirusMove.getEllipseColor() == Color.blue) {
                     if (endVirusMove.getEllipseColor() == Color.blue) {
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
                     } else if (endVirusMove.getEllipseColor() == Color.green) {
                         virusAttack = valueToMove * powers[0];
                         virusProtectionOther = endVirusMove.getHp() * protections[1];
                         firstValue = (virusAttack - virusProtectionOther) / powers[0];
                         secondValue = (virusProtectionOther - virusAttack) / protections[1];

                         if (virusAttack == virusProtectionOther) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(0);
                             endVirusMove.setEllipseColor(new Color(163, 0, 0));
                             Game.redGraph.put(endVirusMove, Game.blueGraph.get(endVirusMove));
                             Game.blueGraph.remove(endVirusMove);
                         }
                         else if (firstValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(firstValue);
                             endVirusMove.setEllipseColor(Color.blue);
                             Game.blueGraph.put(endVirusMove, Game.greenGraph.get(endVirusMove));
                             Game.greenGraph.remove(endVirusMove);
                         }
                         else if (secondValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(secondValue);
                         }
                     }
                     else if (endVirusMove.getEllipseColor() == Color.red ||
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
                             Game.blueGraph.put(endVirusMove, Game.redGraph.get(endVirusMove));
                             Game.redGraph.remove(endVirusMove);
                         }
                         else if (secondValue > 0) {
                             startVirusMove.setHp(startVirusMove.getHp() - valueToMove);
                             endVirusMove.setHp(secondValue);
                         }
                     }
                 }
                 else if (startVirusMove.getEllipseColor() == Color.red ||
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