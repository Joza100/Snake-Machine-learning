package neat;

public class NodeGene {
	private int index;
	private float depth;
	
	public enum Type {
		IN, OUT, HIDDEN
	}
	private Type type;
	
	public NodeGene(int index, Type type, float depth) {
		this.index = index;
		this.type = type;
		this.depth = depth;
	}
	
	public NodeGene(NodeGene nodeGene) {
		index = nodeGene.index;
		depth = nodeGene.depth;
		type = nodeGene.type;
	}

	public int getIndexNumber() {
		return index;
	}

	public Type getType() {
		return type;
	}
	
	public float getDepth () {
		return depth;
	}
}
