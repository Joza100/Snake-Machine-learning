package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class World {

	private static final float MAX_DISTANCE = 0.2f;

	private List<Connection> connections;
	private int innovationNumber;

	private List<Genome> genomes;

	public World(int genomeNumber) {
		connections = new ArrayList<>();
		innovationNumber = 1;

		genomes = new ArrayList<>();

		for (int i = 0; i < genomeNumber; i++) {
			Genome genome = new Genome();
			for (int j = 0; j < 6; j++) {
				genome.addInputGene();
			}
			for (int j = 0; j < 3; j++) {
				genome.addOutputGene();
			}
			for (int j = 0; j < 3; j++) {
				genome.mutate(this);
			}
			genomes.add(genome);
		}
	}

	public void newGeneration(Genome genome1, Genome genome2) {

		Genome offspring = Genome.offspring(genome1, genome2, innovationNumber);

		int size = genomes.size();
		genomes.clear();

		for (int i = 0; i < size; i++) {
			Genome genome = null;
			genome = new Genome(offspring);
			genome.mutate(this);
			genomes.add(genome);
		}
	}

	public void newGeneration() {
		List<List<Genome>> species = new ArrayList<>();

		for (Genome genome : genomes) {
			boolean added = false;
			for (List<Genome> list : species) {
				Genome firstGenome = list.get(0);
				if (Genome.calculateDistance(genome, firstGenome) < MAX_DISTANCE) {
					list.add(genome);
					added = true;
					break;
				}
			}
			if (!added) {
				List<Genome> list = new ArrayList<>();
				list.add(genome);
				species.add(list);
			}
		}

		genomes.clear();

		for (List<Genome> list : species) {
			if (list.size() >= 2) {
				Collections.sort(list, new Comparator<Genome>() {
					@Override
					public int compare(Genome o1, Genome o2) {
						if (o1.getFitness() > o2.getFitness()) {
							return -1;
						} else if (o2.getFitness() > o1.getFitness()) {
							return 1;
						} else {
							return 0;
						}
					}
				});

				Genome offspring = Genome.offspring(list.get(0), list.get(1), innovationNumber);
				for (int i = 0; i < list.size(); i++) {
					Genome mutatedOffspring = new Genome(offspring);
					mutatedOffspring.mutate(this);
					genomes.add(mutatedOffspring);
				}
			} else {
				Genome onlyGenome = list.get(0);
				Genome clone = new Genome(onlyGenome);
				onlyGenome.mutate(this);
				clone.mutate(this);
				genomes.add(onlyGenome);
				genomes.add(clone);
			}
		}
	}

	public int getInnovationNumber(int in, int out) {
		for (Connection connection : connections) {
			if (connection.getIn() == in && connection.getOut() == out) {
				return connection.getInnovationNumber();
			}
		}
		Connection connection = new Connection(in, out, innovationNumber++);
		connections.add(connection);
		return connection.getInnovationNumber();
	}

	public List<Genome> getGenomes() {
		return genomes;
	}

}
