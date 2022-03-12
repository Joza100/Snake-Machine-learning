package snake.entities;

public class SnakePart {

	private int x;
	private int y;

	public enum Direction {
		RIGHT, DOWN, LEFT, UP
	};
	
	private Direction direction;

	public SnakePart(int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Direction getDirection() {
		return direction;
	}

}
