package nl.ru.ai.vroon.mdp;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import nl.ru.ai.KasperAndDennis.reinforcement.QLearning;
import nl.ru.ai.KasperAndDennis.reinforcement.ValueIteration;

/**
 * Main class that runs the Markov Decision Problem.
 *
 * @author Dennis den Hollander (s4776658) Kasper Karelse (s4794443)
 */
public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Which Markov Decision Problem do you want to solve? (1-3): ");
		String command1 = scanner.nextLine();

		System.out.print("Do you want to perform value iteration (1) or Q-learning (2)?: ");
		String command2 = scanner.nextLine();

		switch (command1) {
		case "1":
			MarkovDecisionProblem mdp1 = new MarkovDecisionProblem();
			mdp1.setWaittime(0);
			mdp1.setShowProgress(false);
			switch (command2) {
			case "1":
				mdp1.setInitialState(0, 0);
				new ValueIteration(mdp1, 0.9);
				break;
			case "2":
				mdp1.setDeterministic();
				new QLearning(mdp1, 300, 0.8, 0.2, 0.2);
				break;
			default:
				System.out.println("Your type of reinforcement learning has not been found, please try again.");
				break;
			}
			break;
		case "2":
			MarkovDecisionProblem mdp2 = new MarkovDecisionProblem(10, 10);
			mdp2.setWaittime(0);
			mdp2.setShowProgress(false);
			mdp2.setField(5, 5, Field.REWARD);
			mdp2.setField(1, 1, Field.OBSTACLE);
			mdp2.setField(1, 2, Field.OBSTACLE);
			mdp2.setField(3, 4, Field.OBSTACLE);
			mdp2.setField(5, 6, Field.OBSTACLE);
			mdp2.setField(7, 1, Field.OBSTACLE);
			switch (command2) {
			case "1":
				new ValueIteration(mdp2, 0.9);
				break;
			case "2":
				mdp2.setDeterministic();
				new QLearning(mdp2, 1000, 0.8, 0.2, 0.2);
				break;
			default:
				System.out.println("Your type of reinforcement learning has not been found, please try again.");
				break;
			}
			break;
		case "3":
			MarkovDecisionProblem mdp3 = new MarkovDecisionProblem(10, 10);
			mdp3.setField(5, 5, Field.REWARD);
			mdp3.setField(2, 3, Field.NEGREWARD);
			mdp3.setWaittime(0);
			mdp3.setShowProgress(false);
			switch (command2) {
			case "1":
				new ValueIteration(mdp3, 0.9);
				break;
			case "2":
				mdp3.setDeterministic();
				new QLearning(mdp3, 2000, 0.9, 0.2, 0.2);
				break;
			default:
				break;
			}
			break;
		default:
			System.out.println("Invalid number, please try again ...");
		}
		System.exit(0);
	}

	public static void write(ArrayList<Double> list) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			for (Double d : list) {
				writer.write(Double.toString(d) + "\t");
			}
			writer.close();
		} catch (IOException ioe) {
			System.out.println("Something went wrong: " + ioe);
		}
	}
}
