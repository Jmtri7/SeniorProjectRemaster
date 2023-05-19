// by: James Trinity
package game.entities;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;

// A graphic with a position on the screen
public class Sprite {
	protected ImageTile image = null;
	protected float x, y;

	// use this instead of an image
	protected String imageId = null;

	public Sprite(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Sprite(float x, float y, ImageTile image) {
		this.image = image;
		this.x = x;
		this.y = y;
	}

	public Sprite(float x, float y, String imageId) {
		this.imageId = imageId;
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getWidth() {
		return image.getTileW();
	}

	public int getHeight() {
		return image.getTileH();
	}

	public void setX(float value) {
		x = value;
	}

	public void setY(float value) {
		y = value;
	}

	public ImageTile getGraphic() {
		return this.image;
	}

	public void update(GameContainer gc, float dt) {
		//animation.tick(dt);
	}

	public void render(GameContainer gc, Renderer r) {
		//r.drawRect((int) x, (int) y, image.getTileW(), image.getTileH(), 0xffff0000);
		//r.drawImageTile(image, (int) x, (int) y, animation.getFrameX(), animation.getFrameY());
	}
}