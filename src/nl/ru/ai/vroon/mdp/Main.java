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
		MarkovDecisionProblem mdp = new MarkovDecisionProblem(10, 10);
		mdp.setField(5, 5, Field.REWARD);
		mdp.setField(2, 3, Field.NEGREWARD);
		mdp.setWaittime(0);
		mdp.setInitialState(0, 0);
		mdp.setShowProgress(false);
		mdp.setPosReward(1);
		mdp.setNegReward(-1);
		mdp.setDeterministic();
		ValueIteration.valueIteration(mdp, 0.002, 0.8);
		// QLearning.start(mdp, 1000000, 0.8, 0.5, 0.3);
	}

}
