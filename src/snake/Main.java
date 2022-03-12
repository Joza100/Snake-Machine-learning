package snake;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import snake.display.Input;
import snake.display.Window;
import snake.states.GameState;
import snake.states.StateManager;

public class Main extends Canvas {

	private static final long serialVersionUID = -6286281667174192574L;

	public static final int WIDTH = 896, HEIGHT = 504;
	private Window window;

	private long lastTime;
	private StateManager stateManager;

	public Main() {
		window = new Window("Snake Machine Learning", WIDTH, HEIGHT);
		window.add(this);
		window.addKeyListener(new Input());
		setFocusable(false);
		update();
	}

	private void update() {
		stateManager = new StateManager();
		stateManager.setState(new GameState());
		lastTime = System.nanoTime();
		while (true) {
			tick();
			render();
		}
	}

	private void tick() {
		float delta = (System.nanoTime() - lastTime) / 1000000000f;
		lastTime = System.nanoTime();
		stateManager.tick(delta);
	}

	private void render() {
		if (getBufferStrategy() == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = getBufferStrategy().getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		stateManager.render(g);

		g.dispose();
		getBufferStrategy().show();
	}

	public static void main(String[] args) {
		new Main();
	}
}
