package sokoban;

import Map.Map;

/**
 * 	Sokoban solver based on a Genetic Algorithm.
 * 
 * 	We create a population of "individuals": each is a
 * 	series of (initially) random moves. These individuals
 * 	obtain a 'fitness' depending on how well these moves serve
 * 	to solve the map. Through artificial evolution, we hope
 * 	to eventually find an individual that can solve the
 * 	map. 
 * 
 * 	@author Matthijs
 */
class SolverGA extends Solver {

	
	protected static Map	solution	=	null;
	
	@Override
	protected Map solve(Map map) {
		SolverGAPopulation population = new SolverGAPopulation();
		
		while (solution == null && !isFinished())
			population.evolve();
		
		return solution;
	}
}