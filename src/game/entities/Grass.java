// by: James Trinity
package game.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.KeyEvent;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import engine.gfx.Light;

import game.ImageLoader;

import game.board.Tile;

public class Grass extends Entity {
	private Animation sway = new Animation("sway", 0.6f, Arrays.asList(0, 1));

	public Grass(Tile tile, String imageId) {
		super(tile, imageId);
		setTag(imageId);
	}

	public Grass(Tile tile, String imageId, int glowColor, int glowRadius) {
		super(tile, imageId);
		setTag(imageId);
		light = new Light(glowRadius, glowColor);
	}

	public void update(GameContainer gc, float dt) {
		if(sway.isActive()) sway.tick(dt);
		else sway.start();
	}

	public void render(GameContainer gc, Renderer r) {
		int tileSize = tile.getBoard().getTileSize();
		int tileX = tile.getX() * tileSize;
		int tileY = tile.getY() * tileSize;

		ImageTile image = ImageLoader.getImage(imageId);

		float spriteX;
		float spriteY;
		spriteX = x - image.getTileW() / 2 + tileSize / 2;
		spriteY = y - image.getTileH() + tileSize;
		r.drawImageTile(image, (int) spriteX, (int) spriteY, sway.getFrame(), 0);

		if(light != null){
			light = new Light(sway.getFrame() * 10 + 100, light.getColor());
			r.drawLight(light, (int) (x + tileSize / 2), (int) (y));
		}
	}
}