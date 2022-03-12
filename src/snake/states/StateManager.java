package snake.states;

import java.awt.Graphics;

public class StateManager {

	private State state;
	
	public void setState(State state) {
		this.state = state;
	}
	
	public void tick (float delta) {
		state.tick(delta);
	}
	
	public void render (Graphics g) {
		state.render(g);
	}
}
