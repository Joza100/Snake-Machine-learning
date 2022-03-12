package snake.states;

import java.awt.Graphics;

public abstract class State {
	
	public abstract void tick(float delta);
	public abstract void render(Graphics g);
}
