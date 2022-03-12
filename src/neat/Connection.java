package neat;

public class Connection {

	private int in;
	private int out;
	
	private int innovationNumber;

	public Connection(int in, int out, int innovationNumber) {
		super();
		this.in = in;
		this.out = out;
		this.innovationNumber = innovationNumber;
	}

	public int getIn() {
		return in;
	}

	public int getOut() {
		return out;
	}

	public int getInnovationNumber() {
		return innovationNumber;
	}
}
