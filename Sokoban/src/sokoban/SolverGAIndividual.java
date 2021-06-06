package sokoban;

import java.util.LinkedList;
import Map.*;

/**
 * 	Genetic Algorithm Individual.
 * 
 * 	An individual in our genetic algorithm. This is in essence a series
 * 	of moves, which, when effected upon a map, will result in a few map
 * 	with possibly better value. Through the bettering of individuals we
 * 	hope to eventually create one capable of completely solving the map.  
 * 
 * 	@author Matthijs
 */
public class SolverGAIndividual implements Comparable<SolverGAIndividual> {

	private	char[]	genes;
	private	char[] 	directions  = 	{'U', 'D', 'L', 'R'}; 	
	private	int		fitness		=	Integer.MIN_VALUE;
	
	
	/**
	 * 	Generate a new individual with specified length.
	 * 
	 * 	Genes are initialized randomly.
	 */
	public SolverGAIndividual (int length) {
		genes  		 = new char[length];
		for (int i = 0; i < length; i++)
			genes[i] = getRandomGene();
		generateFitness();
	}
	

	/**
	 * 	Generate a new individual with specified genes.
	 */
	public SolverGAIndividual (char[] genes) {
		this.genes = genes;
		generateFitness();
	}
	
	
	/**
	 * 	Mutate this individual with a specific chance.
	 * 
	 * 	For each gene in this individuals genotype, there
	 * 	is a chance that the gene will randomly mutate into
	 * 	one of the four possible directions. 
	 */
	public void mutate (double chance) {
		for (int i = 0; i < genes.length; i++)
			if (Math.random() < chance)
				genes[i] = getRandomGene();
	}
	
	
	/**
	 * 	Return the fitness score for this individual. If this is
	 * 	set correctly (through generateFitness) it will be an 
	 * 	integer based on how far the puzzle is solved. If not set
	 * 	correctly, it will return Integer.MIN_VALUE.
	 */
	public int getFitness () {
		return fitness;
	}
	
	
	/**
	 * 	Return the genes of this individual.
	 */
	public char[] getGenes () {
		return genes;
	}
	
	
	/**
	 * 	Attempt to determine the fitness of this individual.
	 * 
	 * 	For this, we traverse the basic map of our parent and
	 * 	see where we end up. Then, through the Heuristics class,
	 *  get that maps' score.
	 */
	public void generateFitness () {
		Map map = new Map(Sokoban.getMapHandler().getMap());
		
		for (int i = 0; i < genes.length; i++) {
			char 				c			= genes[i];
			LinkedList<Move> 	moves 		= map.findMoves();
			Move				lastMove	= null;
			boolean 			possible	= false;
			for (Move move : moves) {
				lastMove = move;
				if (move.getDirection() == c) {
					map.performMove(move);
					possible = true;
				}
			}
			if (!possible && lastMove != null) {
				genes[i] = lastMove.getDirection();
				map.performMove(lastMove);
			}
			if (map.isSolved()) {
				SolverGA.solution = map;
				return;
			}
		}
		fitness = Heuristics.mapScore(map);
	}
	
	
	/**
	 *	Get a random gene value. This means randomly generate
	 *	either one of the four possible directions.
	 */
	private char getRandomGene () {
		int direction 	= (int) (Math.random() * 4);
		return directions[direction];
	}


	/**
	 * 	Compare two Individuals.
	 * 
	 * 	This is done by comparing their respective fitness values.
	 */
	@Override
	public int compareTo(SolverGAIndividual arg0) {
		return fitness - arg0.getFitness();
	}
}