package nl.ru.ai.KasperAndDennis.reinforcement;

import java.util.HashMap;

import nl.ru.ai.vroon.mdp.Action;
import nl.ru.ai.vroon.mdp.Field;
import nl.ru.ai.vroon.mdp.MarkovDecisionProblem;

public class ValueIteration {

	public static void valueIteration(MarkovDecisionProblem mdp, double theta, double gamma) {
		int width = mdp.getWidth();
		int height = mdp.getHeight();

		double[][] currentVs = new double[width][height];
		double[][] nextVs = new double[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// initiate with zeroes
				currentVs[x][y] = 0;
			}
		}
		boolean converged = false;
		int counter = 0;
		while (!converged) {
			converged = true;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (isTerminalState(mdp, x, y))
						continue;
					double highestValue = Double.NEGATIVE_INFINITY;
					for (Action action : Action.values()) {
						double qValue = qFunction(mdp, currentVs, x, y, action, gamma);
						highestValue = qValue > highestValue ? qValue : highestValue;
					}
					nextVs[x][y] = highestValue;
					double diff = Math.abs(currentVs[x][y] - nextVs[x][y]);
					if (diff >= theta)
						converged = false;
				}
			}
			counter++;
			currentVs = nextVs;
			nextVs = new double[width][height];
		}
		System.out.println("COUNTER: " + counter);
		displayPolicy(mdp, currentVs, false);
	}

	private static boolean isTerminalState(MarkovDecisionProblem mdp, int x, int y) {
		Field field = mdp.getField(x, y);
		return field == Field.REWARD || field == Field.NEGREWARD || field == Field.OBSTACLE;
	}

	private static double qFunction(MarkovDecisionProblem mdp, double[][] Vs, int x, int y, Action actionToTake,
			double discount) {
		HashMap<Action, Double> actionInfo = getTransitionProbabilities(mdp, actionToTake);
		double sum = 0;
		for (Action action : actionInfo.keySet()) {
			int nextX = x + Action.getHorizontalChange(action);
			int nextY = y + Action.getVerticalChange(action);
			double probs = actionInfo.get(action);
			if (!isWithinBounds(mdp, nextX, nextY)) {
				sum += probs * (mdp.getRewardForPosition(x, y) + discount * Vs[x][y]);
			} else {
				sum += probs * (mdp.getRewardForPosition(nextX, nextY) + discount * Vs[nextX][nextY]);
			}
		}
		return sum;
	}

	private static boolean isWithinBounds(MarkovDecisionProblem mdp, int x, int y) {
		int width = mdp.getWidth();
		int height = mdp.getHeight();
		boolean withinBoundsX = x >= 0 && x < width;
		boolean withinBoundsY = y >= 0 && y < height;
		boolean withinBounds = withinBoundsX && withinBoundsY;
		return withinBounds;
	}

	private static HashMap<Action, Double> getTransitionProbabilities(MarkovDecisionProblem mdp, Action action) {
		boolean isDeterministic = mdp.isDeterministic();
		double forwardStepProb = isDeterministic ? 1 : mdp.getForwardStepProb();
		double sideStepProb = isDeterministic ? 0 : mdp.getSideStepProb();
		double backStepProb = isDeterministic ? 0 : mdp.getBackStepProb();

		HashMap<Action, Double> actions = new HashMap<Action, Double>();
		actions.put(action, forwardStepProb);
		actions.put(Action.previousAction(action), sideStepProb / 2);
		actions.put(Action.nextAction(action), sideStepProb / 2);
		actions.put(Action.backAction(action), backStepProb);
		return actions;
	}

	private static Action getBestAction(MarkovDecisionProblem mdp, int x, int y, double[][] vValues) {
		Action bestAction = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		for (Action action : Action.values()) {
			int dx = Action.getHorizontalChange(action);
			int dy = Action.getVerticalChange(action);
			int nextX = x + dx;
			int nextY = y + dy;
			System.out.println(nextX + ":" + nextY);
			if (isWithinBounds(mdp, nextX, nextY)) {
				double value = vValues[nextX][nextY];
				if (mdp.getField(nextX, nextY) == Field.REWARD) {
					bestAction = action;
					return bestAction;
				}
				if (value > bestValue) {
					bestAction = action;
					bestValue = value;
				}
			}
		}
		return bestAction;
	}

	private static double getBestValue(MarkovDecisionProblem mdp, int x, int y, double[][] vValues) {
		double bestValue = Double.NEGATIVE_INFINITY;
		for (Action action : Action.values()) {
			int dx = Action.getHorizontalChange(action);
			int dy = Action.getVerticalChange(action);
			int nextX = x + dx;
			int nextY = y + dy;
			if (isWithinBounds(mdp, nextX, nextY)) {
				double value = vValues[nextX][nextY];
				if (value > bestValue) {
					bestValue = value;
					if (mdp.getField(nextX, nextY) == Field.REWARD) {
						return bestValue;
					}
				}
			}
		}
		return bestValue;
	}

	private static void displayPolicy(MarkovDecisionProblem mdp, double[][] array, boolean shouldShowValues) {
		StringBuilder sb = new StringBuilder();
		int height = array[0].length;
		int width = array.length;
		sb.append("Policy: " + '\n');
		for (int y = height - 1; y >= 0; y--) {
			sb.append("| ");
			for (int x = 0; x < width; x++) {
				Field field = mdp.getField(x, y);
				switch (field) {
				case EMPTY:
					Action bestAction = getBestAction(mdp, x, y, array);
					double bestActionValue = Math.round(getBestValue(mdp, x, y, array) * 100.0) / 100.0;
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
