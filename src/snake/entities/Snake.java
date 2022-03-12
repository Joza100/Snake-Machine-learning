package snake.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import snake.display.Input;
import snake.entities.SnakePart.Direction;
import snake.states.GameState;

public class Snake extends Entity {

	private static final float MOVE_TIME = 0.01f;

	private LinkedList<SnakePart> parts;

	private float timer;

	private Direction direction;

	private boolean dead;

	public Snake() {
		direction = Direction.RIGHT;

		parts = new LinkedList<>();

		for (int x = 0; x < 5; x++) {
			parts.add(new SnakePart((int) GameState.WORLD_WIDTH / 2 - x, (int) GameState.WORLD_HEIGHT / 2, direction));
		}

		dead = false;

		timer = 0;
	}

	@Override
	public void tick(float delta) {
		if (onSnake()) {
			dead = true;
		}

		SnakePart firstPart = parts.getFirst();

		if (firstPart.getX() >= GameState.WORLD_WIDTH || firstPart.getX() < 0
				|| firstPart.getY() >= GameState.WORLD_HEIGHT || firstPart.getY() < 0) {
			dead = true;
		}

		if (Input.getKey(KeyEvent.VK_W) && firstPart.getDirection() != Direction.DOWN) {
			direction = Direction.UP;
		} else if (Input.getKey(KeyEvent.VK_S) && firstPart.getDirection() != Direction.UP) {
			direction = Direction.DOWN;
		} else if (Input.getKey(KeyEvent.VK_A) && firstPart.getDirection() != Direction.RIGHT) {
			direction = Direction.LEFT;
		} else if (Input.getKey(KeyEvent.VK_D) && firstPart.getDirection() != Direction.LEFT) {
			direction = Direction.RIGHT;
		}

		timer += delta;

		if (timer >= MOVE_TIME) {
			timer = 0;
			switch (direction) {
			case LEFT:
				parts.add(0, new SnakePart(firstPart.getX() - 1, firstPart.getY(), direction));
				break;
			case DOWN:
				parts.add(0, new SnakePart(firstPart.getX(), firstPart.getY() + 1, direction));
				break;
			case RIGHT:
				parts.add(0, new SnakePart(firstPart.getX() + 1, firstPart.getY(), direction));
				break;
			case UP:
				parts.add(0, new SnakePart(firstPart.getX(), firstPart.getY() - 1, direction));
				break;
			}
			parts.removeLast();
		}
	}

	public void changeDirection(Direction changeDirection) {

		List<Direction> values = new ArrayList<>(Arrays.asList(Direction.values()));

		switch (changeDirection) {
		case LEFT:
			direction = values.get((values.indexOf(direction) - 1) == -1 ? 3 : values.indexOf(direction) - 1);
			break;
		case RIGHT:
			direction = values.get((values.indexOf(direction) + 1) == 4 ? 0 : values.indexOf(direction) + 1);
			break;
		default:
			break;
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for (SnakePart part : parts) {
			g2d.setColor(Color.WHITE);
			g2d.fillRect((int) (part.getX() * GameState.RECT_SIZE), (int) (part.getY() * GameState.RECT_SIZE),
					GameState.RECT_SIZE, GameState.RECT_SIZE);
			g2d.setColor(Color.GRAY);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawRect((int) (part.getX() * GameState.RECT_SIZE), (int) (part.getY() * GameState.RECT_SIZE),
					GameState.RECT_SIZE, GameState.RECT_SIZE);
		}
	}

	public void addNewPart() {
		SnakePart lastPart = parts.getLast();
		switch (lastPart.getDirection()) {
		case LEFT:
			parts.add(new SnakePart(lastPart.getX() + 1, lastPart.getY(), direction));
			break;
		case DOWN:
			parts.add(new SnakePart(lastPart.getX(), lastPart.getY() - 1, direction));
			break;
		case RIGHT:
			parts.add(new SnakePart(lastPart.getX() - 1, lastPart.getY(), direction));
			break;
		case UP:
			parts.add(new SnakePart(lastPart.getX(), lastPart.getY() + 1, direction));
			break;
		}
	}

	public float[] makeInputs(Food food) {
		SnakePart first = parts.getFirst();

		float up = 1;
		float down = 1;
		float left = 1;
		float right = 1;

		int upFirst;
		int downFirst;
		int leftFirst;
		int rightFirst;

		float upSnake = 1;
		float downSnake = 1;
		float leftSnake = 1;
		float rightSnake = 1;

		for (SnakePart part : parts) {
			if (part == first) {
				continue;
			}
			if (part.getY() == first.getY()) {
				if (part.getX() > first.getX()) {
					rightSnake = (part.getX() - first.getX()) / GameState.WORLD_WIDTH;
				} else {
					leftSnake = (-part.getX() + first.getX()) / GameState.WORLD_WIDTH;
				}
			}
			if (part.getX() == first.getX()) {
				if (part.getY() > first.getY()) {
					downSnake = (part.getY() - first.getY()) / GameState.WORLD_HEIGHT;
				} else {
					upSnake = (-part.getY() + first.getY()) / GameState.WORLD_HEIGHT;
				}
			}
		}

		float upEdge = 1;
		float downEdge = 1;
		float leftEdge = 1;
		float rightEdge = 1;

		upEdge = (first.getY() - 0) / GameState.WORLD_HEIGHT;
		downEdge = ((GameState.WORLD_HEIGHT - 1) - first.getY()) / GameState.WORLD_HEIGHT;
		leftEdge = (first.getX() - 0) / GameState.WORLD_WIDTH;
		rightEdge = ((GameState.WORLD_WIDTH - 1) - first.getX()) / GameState.WORLD_WIDTH;

		float upFood = 1;
		float downFood = 1;
		float leftFood = 1;
		float rightFood = 1;

		if (food.getY() == first.getY()) {
			if (food.getX() > first.getX()) {
				rightFood = (food.getX() - first.getX()) / GameState.WORLD_WIDTH;
			} else {
				leftFood = (-food.getX() + first.getX()) / GameState.WORLD_WIDTH;
			}
		}
		if (food.getX() == first.getX()) {
			if (food.getY() > first.getY()) {
				downFood = (food.getY() - first.getY()) / GameState.WORLD_HEIGHT;
			} else {
				upFood = (-food.getY() + first.getY()) / GameState.WORLD_HEIGHT;
			}
		}

		up = Math.min(upFood, Math.min(upSnake, upEdge));
		down = Math.min(downFood, Math.min(downSnake, downEdge));
		left = Math.min(leftFood, Math.min(leftSnake, leftEdge));
		right = Math.min(rightFood, Math.min(rightSnake, rightEdge));

		if (up == upSnake) {
			upFirst = -1;
		} else if (up == upEdge) {
			upFirst = 0;
		} else {
			upFirst = 1;
		}

		if (down == downSnake) {
			downFirst = -1;
		} else if (down == downEdge) {
			downFirst = 0;
		} else {
			downFirst = 1;
		}

		if (left == leftSnake) {
			leftFirst = -1;
		} else if (left == leftEdge) {
			leftFirst = 0;
		} else {
			leftFirst = 1;
		}

		if (right == rightSnake) {
			rightFirst = -1;
		} else if (right == rightEdge) {
			rightFirst = 0;
		} else {
			rightFirst = 1;
		}

		switch (direction) {
		case RIGHT:
			return new float[] { right, down, up, rightFirst, downFirst, upFirst };
		case DOWN:
			return new float[] { down, left, right, downFirst, leftFirst, rightFirst };
		case LEFT:
			return new float[] { left, up, down, leftFirst, upFirst, downFirst };
		case UP:
			return new float[] { up, right, left, upFirst, rightFirst, leftFirst };
		default:
			return new float[] {};
		}
		
		
	}

	public boolean onSnake() {
		SnakePart firstPart = parts.getFirst();
		for (SnakePart part : parts) {
			if (part == firstPart) {
				continue;
			}
			if (firstPart.getX() == part.getX() && firstPart.getY() == part.getY()) {
				return true;
			}
		}
		return false;
	}

	public LinkedList<SnakePart> getParts() {
		return parts;
	}

	public void die() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}

}
