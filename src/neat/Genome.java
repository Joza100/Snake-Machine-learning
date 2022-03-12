package neat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import neat.NodeGene.Type;

public class Genome {

	private static final float ADD_CONNECTION = 0.5f;
	private static final float ADD_NODE = 0.15f;
	private static final float SHIFT_WEIGHT = 0.3f;
	private static final float CHANGE_WEIGHT = 0.2f;
	private static final float ENABLE_DISABLE_NODE = 0.1f;
	
	private List<NodeGene> nodeGenes;
	private List<ConnectGene> connectGenes;

	private int nodeIndex;

	private float fitness;

	public Genome() {
		nodeGenes = new ArrayList<>();
		connectGenes = new ArrayList<>();

		nodeIndex = 1;
	}

	public Genome(Genome genome) {
		nodeGenes = new ArrayList<>();
		connectGenes = new ArrayList<>();

		for (ConnectGene connectGene : genome.connectGenes) {
			connectGenes.add(new ConnectGene(connectGene));
		}
		for (NodeGene nodeGene : genome.nodeGenes) {
			nodeGenes.add(new NodeGene(nodeGene));
		}
		nodeIndex = genome.nodeIndex;
	}

	public void addInputGene() {
		nodeGenes.add(new NodeGene(nodeIndex++, Type.IN, 0));
	}

	public void addOutputGene() {
		nodeGenes.add(new NodeGene(nodeIndex++, Type.OUT, 0));
	}

	public float evaluation(float[] inputs, int outNumber) {
		int outCounter = 1;
		for (NodeGene nodeGene : nodeGenes) {
			if (nodeGene.getType() == Type.OUT) {
				if (outNumber == outCounter) {
					return evaluation(inputs, nodeGene);
				} else {
					outCounter++;
				}
			}
		}
		return -2;
	}

	private float evaluation(float[] inputs, NodeGene outGene) {
		if (outGene.getType() == Type.IN) {
			return inputs[outGene.getIndexNumber() - 1];
		}

		float value = 0;
		List<NodeGene> inNodes = getInNodes(outGene);

		for (NodeGene nodeGene : inNodes) {
			try {
				value += evaluation(inputs, nodeGene) * findConnectionGene(nodeGene, outGene).getWeight();
			} catch (StackOverflowError e) {
				printGenes();
			}
		}

		return MathUtil.sigmoid(value);
	}

	private ConnectGene findConnectionGene(NodeGene inGene, NodeGene outGene) {
		for (ConnectGene connectGene : connectGenes) {
			if (connectGene.getIn() == inGene.getIndexNumber() && connectGene.getOut() == outGene.getIndexNumber()) {
				return connectGene;
			}
		}
		return null;
	}

	private List<NodeGene> getInNodes(NodeGene toFind) {
		List<NodeGene> inGenes = new ArrayList<>();
		for (ConnectGene connectGene : connectGenes) {
			if (connectGene.getOut() == toFind.getIndexNumber()) {
				for (NodeGene nodeGene : nodeGenes) {
					if (connectGene.getIn() == nodeGene.getIndexNumber()) {
						inGenes.add(nodeGene);
					}
				}
			}
		}
		return inGenes;
	}

	public void mutate(World world) {
		Random random = new Random();

		float rnd = random.nextFloat();

		if (connectGenes.isEmpty() || rnd <= ADD_CONNECTION) {
			boolean invalidConnection;
			int in = 0;
			int out;

			do {
				invalidConnection = false;
				do {
					in = random.nextInt(nodeGenes.size()) + 1;
				} while (getNodeByIndex(in).getType() == Type.OUT);

				do {
					out = random.nextInt(nodeGenes.size()) + 1;
				} while (getNodeByIndex(out).getType() == Type.IN);

				for (ConnectGene connectGene : connectGenes) {
					if ((connectGene.getIn() == in && connectGene.getOut() == out) || out == in) {
						invalidConnection = true;
					}
				}
				if (!invalidConnection && (getNodeByIndex(out).getType() == Type.HIDDEN
						&& getNodeByIndex(in).getDepth() >= getNodeByIndex(out).getDepth())) {
					invalidConnection = true;
				}
			} while (invalidConnection);

			connectGenes.add(
					new ConnectGene(in, out, random.nextFloat() * (1 - -1) + -1, world.getInnovationNumber(in, out)));
		} 
		rnd = random.nextFloat();
		if (rnd <= ENABLE_DISABLE_NODE) {
			ConnectGene connectGene = connectGenes.get(random.nextInt(connectGenes.size()));
			connectGene.setEnabled(!connectGene.isEnabled());
		}
		
		rnd = random.nextFloat();
		if (rnd <= SHIFT_WEIGHT) {
			ConnectGene connectGene = connectGenes.get(random.nextInt(connectGenes.size()));
			connectGene.setWeight((float) (connectGene.getWeight() + random.nextFloat() * (0.1 - -0.1) + -0.1));

		} 
		rnd = random.nextFloat();
		if (rnd <= CHANGE_WEIGHT) {
			ConnectGene connectGene = connectGenes.get(random.nextInt(connectGenes.size()));
			connectGene.setWeight(random.nextFloat() * (1 - -1) + -1);
		}
		rnd = random.nextFloat();
		if (rnd <= ADD_NODE) {
			ConnectGene connectGene = connectGenes.get(random.nextInt(connectGenes.size()));
			connectGene.setEnabled(false);
			NodeGene newGene;
			if (getNodeByIndex(connectGene.getOut()).getType() == Type.OUT) {
				newGene = new NodeGene(nodeIndex++, Type.HIDDEN, getNodeByIndex(connectGene.getIn()).getDepth() + 1);
			} else {
				newGene = new NodeGene(nodeIndex++, Type.HIDDEN, (getNodeByIndex(connectGene.getIn()).getDepth()
						+ getNodeByIndex(connectGene.getOut()).getDepth()) / 2);
			}
			nodeGenes.add(newGene);
			connectGenes.add(new ConnectGene(connectGene.getIn(), newGene.getIndexNumber(), connectGene.getWeight(),
					world.getInnovationNumber(connectGene.getIn(), newGene.getIndexNumber())));
			connectGenes.add(new ConnectGene(newGene.getIndexNumber(), connectGene.getOut(), 1,
					world.getInnovationNumber(newGene.getIndexNumber(), connectGene.getOut())));
		}
	}

	public static Genome offspring(Genome genome1, Genome genome2, int highestInnovation) {
		Random random = new Random();

		Genome offspring = new Genome();
		if (genome1.nodeGenes.size() > genome2.nodeGenes.size()) {
			for (NodeGene nodeGene : genome1.nodeGenes) {
				offspring.nodeGenes.add(new NodeGene(nodeGene));
			}
			offspring.nodeIndex = genome1.nodeIndex;
		} else {
			for (NodeGene nodeGene : genome2.nodeGenes) {
				offspring.nodeGenes.add(new NodeGene(nodeGene));
			}
			offspring.nodeIndex = genome2.nodeIndex;
		}

		for (int innovationNumber = 1; innovationNumber < highestInnovation; innovationNumber++) {

			ConnectGene connectGene1 = null;
			ConnectGene connectGene2 = null;

			for (ConnectGene connectGene : genome1.connectGenes) {
				if (connectGene.getInnovationNumber() == innovationNumber) {
					connectGene1 = connectGene;
					break;
				}
			}
			for (ConnectGene connectGene : genome2.connectGenes) {
				if (connectGene.getInnovationNumber() == innovationNumber) {
					connectGene2 = connectGene;
					break;
				}
			}

			if (connectGene1 != null && connectGene2 != null) {
				float rnd = random.nextFloat();
				if (rnd < 0.5) {
					offspring.connectGenes.add(connectGene1);
				} else {
					offspring.connectGenes.add(connectGene2);
				}
			} else {
				if (connectGene1 != null && genome1.getFitness() > genome2.getFitness()) {
					offspring.connectGenes.add(connectGene1);
				} else if (connectGene2 != null && genome2.getFitness() > genome2.getFitness()) {
					offspring.connectGenes.add(connectGene2);
				}
			}
		}
		return offspring;
	}

	public static float calculateDistance(Genome genome1, Genome genome2) {
		int disjointGenes = 0;

		for (ConnectGene connectGene1 : genome1.connectGenes) {
			boolean disjoint = true;
			for (ConnectGene connectGene2 : genome2.connectGenes) {
				if (connectGene1.getInnovationNumber() == connectGene2.getInnovationNumber()) {
					disjoint = false;
					break;
				}
			}
			if (disjoint) {
				disjointGenes++;
			}
		}

		float totalWeightDifference = 0;
		int weightNumber = 0;

		for (ConnectGene connectGene1 : genome1.connectGenes) {
			for (ConnectGene connectGene2 : genome2.connectGenes) {
				if (connectGene1.getInnovationNumber() == connectGene2.getInnovationNumber()) {
					weightNumber++;
					totalWeightDifference += Math.abs(connectGene1.getWeight() - connectGene2.getWeight());
				}
			}
		}

		float distance = 0;

		if (genome1.connectGenes.size() > genome2.connectGenes.size()) {
			distance += disjointGenes / (float) genome1.connectGenes.size();
		} else {
			distance += disjointGenes / (float) genome2.connectGenes.size();
		}

		if (weightNumber != 0) {
			distance += totalWeightDifference / 2 / (float) weightNumber;
		}

		return distance;
	}

	public void printGenes() {
		System.out.println("Node Genes: ");
		for (NodeGene nodeGene : nodeGenes) {
			System.out.println("Type: " + nodeGene.getType() + " Index Number: " + nodeGene.getIndexNumber());
		}

		System.out.println("Connection Genes: ");
		for (ConnectGene connectGene : connectGenes) {
			System.out.println("In: " + connectGene.getIn() + " Out: " + connectGene.getOut() + " Weight: "
					+ connectGene.getWeight() + " Innovation Number: " + connectGene.getInnovationNumber()
					+ " Enabled: " + connectGene.isEnabled());
		}
	}

	private NodeGene getNodeByIndex(int indexNumber) {
		for (NodeGene nodeGene : nodeGenes) {
			if (nodeGene.getIndexNumber() == indexNumber) {
				return nodeGene;
			}
		}
		return null;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

}
