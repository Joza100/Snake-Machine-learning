package neat;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import snake.entities.Food;
import snake.entities.Snake;
import snake.entities.SnakePart.Direction;

public class SnakeAI {

	private Snake snake;
	private Food food;
	private Genome genome;

	private float lifeTimer;
	private float timer;

	private Set<Point> points;
	
	int lastSize;

	public SnakeAI(Genome genome) {
		snake = new Snake();
		food = new Food(snake);
		this.genome = genome;

		points = new HashSet<>();
		
		timer = 0;
		lifeTimer = 0;
		lastSize = 0;
	}

	public void tick(float delta) {
		lifeTimer += delta;
		timer += delta;
		if (snake.isDead()) {
			return;
		}
		
		if (timer >= 0.01) {
			timer = 0;
			float[] inputs = snake.makeInputs(food);

			float[] outputs = new float[3];
			for (int i = 0; i < 3; i++) {
				outputs[i] = genome.evaluation(inputs, i + 1);
			}

			int biggestIndex = 0;
			float biggestOutput = outputs[0];

			for (int i = 0; i < 3; i++) {
				if (outputs[i] > biggestOutput) {
					biggestOutput = outputs[i];
					biggestIndex = i;
				}
			}
			if (biggestIndex == 1) {
				snake.changeDirection(Direction.RIGHT);
			} else if (biggestIndex == 2) {
				snake.changeDirection(Direction.LEFT);
			}
			points.add(new Point(snake.getParts().getFirst().getX(), snake.getParts().getFirst().getY()));
		}

		snake.tick(delta);
		food.tick(delta);

		int currentSize = snake.getParts().size();
		
		if (currentSize != lastSize) {
			lifeTimer = 0;
		}
		lastSize = currentSize;
		
		if (lifeTimer >= 5) {
			snake.die();
		}
	}

	public void render(Graphics g) {
		if (snake.isDead()) {
			return;
		}
		snake.render(g);
		food.render(g);
	}

	public float calculateFitness() {
		float fitness = points.size() * 5 + (snake.getParts().size() - 5) * 50;
		genome.setFitness(fitness);
		return fitness;
	}

	public Snake getSnake() {
		return snake;
	}
	
	public Genome getGenome () {
		return genome;
	}
}
