package nl.ru.ai.KasperAndDennis.reinforcement;

import nl.ru.ai.vroon.mdp.Action;
import nl.ru.ai.vroon.mdp.Field;
import nl.ru.ai.vroon.mdp.MarkovDecisionProblem;

/**
 * 
* @author Dennis den Hollander (s4776658) Kasper Karelse (s4794443)
 */
public class ValueIteration {

	private int counter = 0;
	private boolean IS_DETERMINISTIC = true;
	private final double POSREWARD;
	private final double[] TRANSITION_PROBABILITIES;
	private static final double DELTA = 1E-10;
	private final double NEGREWARD;
	private final double NOREWARD;
	private final int WIDTH, HEIGHT;
	private final MarkovDecisionProblem MDP;
	private final double GAMMA;
	protected final Action[][] POLICY;
	private double[][] currentStates;
	private double[][] nextStates;

	public ValueIteration(MarkovDecisionProblem mdp, double gamma) {
		this.GAMMA = gamma;
		this.MDP = mdp;
		this.WIDTH = mdp.getWidth();
		this.HEIGHT = mdp.getHeight();
		this.POSREWARD = mdp.getRewardVals()[0];
		this.NEGREWARD = mdp.getRewardVals()[1];
		this.NOREWARD = mdp.getRewardVals()[2];
		this.TRANSITION_PROBABILITIES = mdp.getTransitionProbs();
		this.POLICY = new Action[WIDTH][HEIGHT];
		this.nextStates = new double[WIDTH][HEIGHT];
		this.currentStates = new double[WIDTH][HEIGHT];
		valueIteration();
                System.out.println("Iterations:" + counter);
                showValues();
		getPolicy();
		showPolicy();
	}

	/**
	 * The Value Iteration algorithm
	 */
	public void valueIteration() {
		IS_DETERMINISTIC = MDP.isDeterministic();
		boolean hasConverged = false;
		while (!hasConverged) {
			counter++;
			for (int row = 0; row < WIDTH; row++) {
				for (int col = 0; col < HEIGHT; col++) {
					Field field = MDP.getField(row, col);
					if (isTerminal(field)) {
						continue;
					}
					int[] state = { row, col };
					nextStates[row][col] = getMaxQ(state);
					hasConverged = Math.abs(nextStates[row][col] - currentStates[row][col]) < DELTA;
				}
			}
			currentStates = nextStates;
			nextStates = new double[WIDTH][HEIGHT];
		}
	}

	protected boolean isTerminal(Field field) {
		return field == Field.REWARD || field == Field.NEGREWARD;
	}

	/**
	 * Finds the maximum Q value for all Actions
	 * 
	 * @param state x and y coordinate
	 * @return maximum Q value for a state
	 */
	protected double getMaxQ(int[] state) {
		double maxValue = Double.NEGATIVE_INFINITY;
		for (Action action : Action.values()) {
			double currentValue = qFunction(state, action);
			maxValue = (currentValue > maxValue) ? currentValue : maxValue;
		}
		return maxValue;
	}

	protected Action bestAction(int x, int y) {
		double maxValue = Double.NEGATIVE_INFINITY;
		Action bestAction = null;
		for (Action action : Action.values()) {
			if (invalidAction(x, y, action)) {
				double val = currentStates[x][y];
				if (val > maxValue) {
					maxValue = val;
					bestAction = action;
				}
			} else {
				int dx = action.GetDX();
				int dy = action.GetDY();
				Field field = MDP.getField(x + dx, y + dy);
				double val = (field == Field.REWARD) ? POSREWARD + 1E-6
						: ((field == Field.NEGREWARD) ? NEGREWARD - 1E-6 : currentStates[x + dx][y + dy]);
				if (val > maxValue) {
					maxValue = val;
					bestAction = action;
				}
			}
		}
		return bestAction;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = HEIGHT; row > 0; row--) {
			sb.append("| ");
			for (int column = 0; column < WIDTH; column++) {
				sb.append(String.format("%f", currentStates[column][row]));
				sb.append(" | ");
			}
			sb.append("\n");
		}
		sb.append(String.format("number of epochs: ", counter));
		return sb.toString();
	}

	/**
	 * Computes the Q-values for a state and action pair.
	 * 
	 * @param state  state composed of x and y coordinate
	 * @param action action
	 * @return Q-value
	 */
	private double qFunction(int[] state, Action action) {
		int x = state[0];
		int y = state[1];
		double[] transition = getTransitionProbabilities();
		Action[] actions = new Action[] { action, Action.nextAction(action), Action.previousAction(action),
				Action.backAction(action) };
		double sum = 0;
		for (int i = 0; i < actions.length; i++) {
			int dx = actions[i].GetDX();
			int dy = actions[i].GetDY();
			if (invalidAction(x, y, actions[i])) {
				Field field = MDP.getField(x, y);
				sum += transition[i] * (getReward(field) + GAMMA * currentStates[x][y]);
			} else {
				Field field = MDP.getField(x + dx, y + dy);
				sum += transition[i] * (getReward(field) + GAMMA * currentStates[x + dx][y + dy]);
			}
		}
		return sum;
	}

	/**
	 * Note: pNoStep is not used in this function, because the agent always moves!
	 * 
	 * @param isDeterministic
	 * @return
	 */
	private double[] getTransitionProbabilities() {
		double stepProb = TRANSITION_PROBABILITIES[0];
		double sideStepProb = TRANSITION_PROBABILITIES[1];
		double backStepProb = TRANSITION_PROBABILITIES[2];
		return IS_DETERMINISTIC ? new double[] { 1, 0, 0, 0 }
				: new double[] { stepProb, sideStepProb / 2, sideStepProb / 2, backStepProb };
	}

	/**
	 * checks if an action is valid
	 * 
	 * @param x      x coordinate
	 * @param y      y coordinate
	 * @param action action
	 * @return true if action cannot be performed
	 */
	private boolean invalidAction(int x, int y, Action action) {
		int dx = action.GetDX();
		int dy = action.GetDY();
		return (y + dy < 0 || y + dy >= HEIGHT) || (x + dx < 0 || x + dx >= WIDTH)
				|| (MDP.getField(x + dx, y + dy) == Field.OBSTACLE);
	}

	/**
	 * Gets the reward of the state.
	 *
	 * @param field
	 * @return reward
	 */
	private double getReward(Field field) {
		switch (field) {
		case REWARD:
			return POSREWARD;
		case NEGREWARD:
			return NEGREWARD;
		case EMPTY:
			return NOREWARD;
		default:
			return 0;
		}
	}

	/**
	 * Get the best action for every state
	 */
	public void getPolicy() {
		for (int column = 0; column < WIDTH; column++) {
			for (int row = 0; row < HEIGHT; row++) {
				Field state = MDP.getField(column, row);
				POLICY[column][row] = !isTerminal(state) ? bestAction(column, row) : null;
			}
		}
	}
	
	/**
	 * print the values
	 * 
	 */
	public void showValues() {
		StringBuilder string = new StringBuilder();
		for (int row = HEIGHT - 1; row >= 0; row--) {
			string.append("| ");
			for (int column = 0; column < WIDTH; column++) {
				string.append(String.format("%-20s", currentStates[column][row]));
				string.append(" | ");
			}
			string.append("\n");
		}
		System.out.println(string.toString());
	}

	/**
	 * print the policy
	 * 
	 */
	public void showPolicy() {
		StringBuilder string = new StringBuilder();
		for (int row = HEIGHT - 1; row >= 0; row--) {
			string.append("| ");
			for (int column = 0; column < WIDTH; column++) {
				string.append(String.format("%-5s", POLICY[column][row]));
				string.append(" | ");
			}
			string.append("\n");
		}
		System.out.println(string.toString());
	}
}
