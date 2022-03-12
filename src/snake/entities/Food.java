package snake.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import snake.states.GameState;

public class Food extends Entity {

	private int x;
	private int y;

	private Snake snake;

	public Food(Snake snake) {
		this.snake = snake;
		newPosition();
	}
	
	private void newPosition () {
		Random random = new Random();
		do {
			x = random.nextInt((int) GameState.WORLD_WIDTH);
			y = random.nextInt((int) GameState.WORLD_HEIGHT);
		} while (onSnake());
	}

	public boolean onSnake() {
		for (SnakePart part : snake.getParts()) {
			if (x == part.getX() && y == part.getY()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void tick(float delta) {
		if (onSnake()) {
			snake.addNewPart();
			newPosition();
		}
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(255, 0, 0));
		g2d.fillRect((int) (x * GameState.RECT_SIZE), (int) (y * GameState.RECT_SIZE), GameState.RECT_SIZE,
				GameState.RECT_SIZE);
		g2d.setColor(new Color(170, 0, 0));
		g2d.setStroke(new BasicStroke(3));
		g2d.drawRect((int) (x * GameState.RECT_SIZE), (int) (y * GameState.RECT_SIZE), GameState.RECT_SIZE,
				GameState.RECT_SIZE);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
