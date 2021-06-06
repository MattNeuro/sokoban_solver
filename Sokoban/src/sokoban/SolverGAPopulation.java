package sokoban;

import java.util.Collections;
import java.util.LinkedList;

public class SolverGAPopulation extends LinkedList<SolverGAIndividual> {

	
	private static final long 	serialVersionUID 	= 	-7188488839223397767L;
	private	static 		 int	size				=	100;
	
	
	/**
	 * 	Create a new Genetic Algorithm population.
	 * 
	 * 	Populations can be acted upon: individuals can mutate,
	 * 	fight and combine to create a new population.
	 */
	public SolverGAPopulation () {
		init();
		Collections.sort(this);
	}
	
	
	public void evolve () {
		fight();
		reproduce();
		mutate();
		Collections.sort(this);
	}
	
	
	/**
	 * 	Make every even individual fight against his next odd neighbor. This
	 * 	means numbers 1 and 2 fight, 3 and 4, etc. 
	 * 
	 * 	Chance of survival is directly related their respective fitness scores;
	 * 	an individual with fitness (almost) as high as his neighbor will have 
	 * 	(almost) the same chance of survival, whereas individuals with a much
	 * 	lower relative fitness will have almost no chance of survive, make your time.
	 * 
	 * 	ALL YOUR BASE ARE BELONG TO US
	 */
	private void fight () {
		SolverGAIndividual	a, b;
		double				fraction;
		
		for (int i = 0; i < (this.size() - 1); i+= 2) {
			a 			= this.get(i);
			b 			= this.get(i + 1);
			fraction	= (double) a.getFitness() / (double) b.getFitness();
			if (fraction > 1)
				fraction = 1 / fraction;

			if (Math.random() > (fraction / 2))
				this.remove(a);
			else
				this.remove(b);
		}
	}
	
	
	/**
	 * 	Two individuals can reproduce to create a new individual based
	 * 	on both their gene-types.
	 */
	private void reproduce () {
		char[] genesA, genesB, genesNew;
		LinkedList<SolverGAIndividual> children = new LinkedList<SolverGAIndividual>();
		
		for (int i = 0; i < (this.size() - 1); i+= 2) {
			genesA 		= this.get(i).getGenes();
			genesB 		= this.get(i + 1).getGenes();
			genesNew	= new char[genesA.length];
			
			for (int j = 0; j < genesA.length / 2; j++)
				genesNew[j] = genesA[j];
			for (int j = genesA.length / 2; j < genesA.length; j++)
				genesNew[j] = genesB[j];
			children.push(new SolverGAIndividual(genesNew));
		}
		for (SolverGAIndividual child : children)
			this.push(child);
	}
	
	
	/**
	 *	Mutate all individuals in our population with a probability directly
	 *	related to their relative fitness. This means healthy individuals will
	 *	barely mutate, while really bad individuals will mutate wildly.
	 */
	private void mutate () {
		SolverGAIndividual individual;
		for (int i = 0; i < size(); i++) {
			individual = get(i);
			if (i < size() - 6)
				individual.mutate(1 - (i / (double) size()));
			individual.generateFitness();
		}
	}
	
	
	/**
	 *	Initialize our population. This will generate #size number of
	 *	individuals.
	 */
	private void init () {
		for (int i = 0; i < size; i++)
			this.add(new SolverGAIndividual(150));
	}
}
