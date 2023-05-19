// by: James Trinity
package game;

import java.awt.event.KeyEvent;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Renderer;

import game.entities.Entity;

public class Camera {
	private Entity target = null;

	public Camera(Entity target) {
		this.target = target;
	}

	public void update(GameContainer gc, float dt) {
		
	}

	public void render(GameContainer gc, Renderer r) {
		if(target != null) {
			float cameraX = target.getX() - gc.getWidth() / 2;
			float cameraY = target.getY() - (gc.getHeight() - 100) / 2;
			r.setCamX((int) cameraX);
			r.setCamY((int) cameraY);
		}
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
}