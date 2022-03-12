package snake.entities;

import java.awt.Graphics;

public abstract class Entity {
	
	public abstract void tick(float delta);
	public abstract void render(Graphics g);
}
