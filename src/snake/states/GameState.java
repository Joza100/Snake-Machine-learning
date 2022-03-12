package snake.states;

import java.awt.Graphics;

import neat.SnakeAI;
import neat.World;
import snake.Main;

public class GameState extends State {

	public static final int RECT_SIZE = 14;
	public static final float WORLD_WIDTH = Main.WIDTH / RECT_SIZE, WORLD_HEIGHT = Main.HEIGHT / RECT_SIZE;

	private World world;
	private SnakeAI[] snakes;

	private int genNumber;

	public GameState() {
		world = new World(120);
		snakes = new SnakeAI[120];

		for (int i = 0; i < 120; i++) {
			snakes[i] = new SnakeAI(world.getGenomes().get(i));
		}
		genNumber = 1;

	}

	@Override
	public void tick(float delta) {

		boolean over = true;

		for (int i = 0; i < snakes.length; i++) {
			snakes[i].tick(delta);

			if (!snakes[i].getSnake().isDead()) {
				over = false;
			}
		}

		if (over) {
			genNumber++;
			System.out.println(genNumber);
			SnakeAI max = snakes[0];
			SnakeAI secondMax = snakes[0];

			for (SnakeAI snake : snakes) {
				if (snake.calculateFitness() > max.calculateFitness()) {
					secondMax = max;
					max = snake;
				} else if (snake.calculateFitness() > secondMax.calculateFitness()
						&& snake.calculateFitness() < max.calculateFitness()) {
					secondMax = snake;
				}
			}
			
			world.newGeneration(max.getGenome(), secondMax.getGenome());
			snakes = new SnakeAI[world.getGenomes().size()];
			for (int i = 0; i < world.getGenomes().size(); i++) {
				snakes[i] = new SnakeAI(world.getGenomes().get(i));
			}
		}

	}

	@Override
	public void render(Graphics g) {

		for (int i = 0; i < snakes.length; i++) {
			snakes[i].render(g);
		}
	}

}
