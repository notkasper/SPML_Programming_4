package nl.ru.ai.KasperAndDennis.reinforcement;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import nl.ru.ai.vroon.mdp.Action;
import nl.ru.ai.vroon.mdp.Field;
import nl.ru.ai.vroon.mdp.MarkovDecisionProblem;

public class QLearning {

	public static void start(MarkovDecisionProblem mdp, int epochs, double discount, double epsilon,
			double learningRate) {
		int width = mdp.getWidth();
		int height = mdp.getHeight();
		HashMap<Action, Double>[][] qValues = initializeQValues(width, height);
		ArrayList<Double> cumulativeRewards = new ArrayList<Double>();
		cumulativeRewards.add(0.0);
		for (int epoch = 0; epoch < epochs; epoch++) {
			if (epoch % 50 == 0)
				System.out.println("epoch: " + epoch);
			if (epoch % epochs / 2 == 0) {
				epsilon *= 0.5;
			}
			mdp.restart();
			setStartState(mdp);
			double cumulativeReward = 0.0;
			while (!mdp.isTerminated()) {
				int oldX = mdp.getStateXPosition();
				int oldY = mdp.getStateYPostion();
				Action action = chooseAction(mdp, epsilon, qValues);
				double reward = mdp.performAction(action);
				cumulativeReward += reward;
				double oldQValue = qValues[oldX][oldY].get(action);
				int newX = mdp.getStateXPosition();
				int newY = mdp.getStateYPostion();
				Action bestAction = getBestAction(qValues[newX][newY]);
				double bestActionValue = qValues[newX][newY].get(bestAction);
				double newQValue = oldQValue + learningRate * (reward + discount * bestActionValue - oldQValue);
				qValues[oldX][oldY].put(action, newQValue);
			}
			cumulativeReward += cumulativeRewards.get(cumulativeRewards.size() - 1);
			cumulativeRewards.add(cumulativeReward);
		}
		displayPolicy(mdp, qValues, false);
		printCumulativeRewards(cumulativeRewards);
	}

	private static Action getBestAction(HashMap<Action, Double> actionValuePairs) {
		double maxValue = Double.NEGATIVE_INFINITY;
		Action bestAction = null;
		for (Action action : actionValuePairs.keySet()) {
			Double value = actionValuePairs.get(action);
			if (value > maxValue) {
				maxValue = value;
				bestAction = action;
			}
		}
		return bestAction;
	}

	private static Action greedyActionChooser(MarkovDecisionProblem mdp, HashMap<Action, Double>[][] qValues) {
		int x = mdp.getStateXPosition();
		int y = mdp.getStateYPostion();
		HashMap<Action, Double> actionValuePairs = qValues[x][y];
		Action bestAction = getBestAction(actionValuePairs);
		return bestAction;
	}

	private static Action chooseAction(MarkovDecisionProblem mdp, double epsilon, HashMap<Action, Double>[][] qValues) {
		if (Math.random() > epsilon) {
			return greedyActionChooser(mdp, qValues);
		} else {
			Random rand = new Random();
			Action[] actions = Action.values();
			return actions[rand.nextInt(Action.values().length)];
		}
	}

	private static void setStartState(MarkovDecisionProblem mdp) {
		int width = mdp.getWidth();
		int height = mdp.getHeight();
		Random rand = new Random();
		int x, y;
		do {
			x = rand.nextInt(width);
			y = rand.nextInt(height);
			mdp.setInitialState(x, y);
		} while (isTerminal(mdp, x, y));
	}

	private static HashMap<Action, Double>[][] initializeQValues(int width, int height) {
		@SuppressWarnings("unchecked")
		HashMap<Action, Double>[][] qValues = new HashMap[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				qValues[x][y] = new HashMap<Action, Double>();
				for (Action action : Action.values()) {
					qValues[x][y].put(action, 0.0);
				}
			}
		}
		return qValues;
	}

	private static boolean isTerminal(MarkovDecisionProblem mdp, int x, int y) {
		Field field = mdp.getField(x, y);
		return field == Field.REWARD || field == Field.NEGREWARD;
	}

	private static void printCumulativeRewards(ArrayList<Double> rewards) {
		try {
			PrintWriter pw = new PrintWriter("cumulative_rewards.txt", "UTF-8");
			for (double reward : rewards) {
				pw.write(Double.toString(reward) + '\t');
			}
			pw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void displayPolicy(MarkovDecisionProblem mdp, HashMap<Action, Double>[][] array,
			boolean shouldShowValues) {
		StringBuilder sb = new StringBuilder();
		int height = array[0].length;
		int width = array.length;
		sb.append("Policy: " + '\n');
		for (int j = height - 1; j >= 0; j--) {
			sb.append("| ");
			for (int i = 0; i < width; i++) {
				Field field = mdp.getField(i, j);
				switch (field) {
				case EMPTY:
					Action bestAction = getBestAction(array[i][j]);
					double bestActionValue = Math.round(array[i][j].get(bestAction) * 100.0) / 100.0;
					sb.append(String.format("%-5s", shouldShowValues ? bestActionValue : bestAction));
					break;
				case NEGREWARD:
					sb.append(String.format("%-5s", "DEAD"));
					break;
				case REWARD:
					sb.append(String.format("%-5s", "WIN"));
					break;
				case OBSTACLE:
					sb.append(String.format("%-5s", "[]"));
					break;
				default:
					break;
				}

				sb.append(" | ");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}

}
