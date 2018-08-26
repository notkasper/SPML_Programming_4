package nl.ru.ai.vroon.mdp;

import nl.ru.ai.KasperAndDennis.reinforcement.ValueIteration;
import nl.ru.ai.KasperAndDennis.reinforcement.QLearning;

/**
 * This main is for testing purposes (and to show you how to use the MDP class).
 * 
 * @author Kasper Karelse & Dennis Hollander
 *
 */
public class Main {

	/**
	 * @param args, not used
	 */
	public static void main(String[] args) {
		MarkovDecisionProblem mdp = new MarkovDecisionProblem();
		mdp.setWaittime(0);
		mdp.setInitialState(0, 0);
		mdp.setShowProgress(true);
		mdp.setPosReward(1);
		mdp.setNegReward(-1);
		mdp.setDeterministic();
		double[][] values = ValueIteration.valueIteration(mdp, 0.002, 0.8);
		displayPolicy(values);
		QLearning.start(mdp, 300, 0.8, 0.5, 0.3);
	}

	public static void displayPolicy(double[][] array) {
		StringBuilder sb = new StringBuilder();
		int height = array[0].length;
		int width = array.length;
		for (int j = height - 1; j >= 0; j--) {
			sb.append("| ");
			for (int i = 0; i < width; i++) {
				sb.append(String.format("%-5s", array[i][j]));
				sb.append(" | ");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}

}
