package neat;

public class ConnectGene {

	private int in;
	private int out;
	private float weight;
	private boolean enabled;
	private int innovationNumber;
	
	public ConnectGene(int in, int out, float weight, int innovationNumber) {
		this.in = in;
		this.out = out;
		this.weight = weight;
		enabled = true;
		this.innovationNumber = innovationNumber;
	}
	
	public ConnectGene (ConnectGene connectGene) {
		in = connectGene.in;
		out = connectGene.out;
		weight = connectGene.weight;
		enabled = connectGene.enabled;
		innovationNumber = connectGene.innovationNumber;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getIn() {
		return in;
	}

	public int getOut() {
		return out;
	}

	public float getWeight() {
		return weight;
	}

	public int getInnovationNumber() {
		return innovationNumber;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
}
