// by: James Trinity
package game.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.KeyEvent;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;

import game.board.Tile;

public class Plant extends Entity {
	private static ImageTile image = new ImageTile("/res/plants/flowers.png", 20, 40);

	private int type;

	public Plant(Tile tile) {
		super(tile, Plant.image);
		this.tag = "plant";
		this.hp = 5;

		if((3 * Math.random()) < 1) this.type = (int) (3 * Math.random());
		else this.type = 2;

		this.respawns = true;
		this.respawnTime = 20f;
		this.respawnTimer = this.respawnTime;
	}

	public void render(GameContainer gc, Renderer r) {
		if(!isDead) {
			int tileSize = tile.getBoard().getTileSize();
			int tileX = tile.getX() * tileSize;
			int tileY = tile.getY() * tileSize;
			float spriteX = x - image.getTileW() / 2 + tileSize / 2;
			float spriteY = y - image.getTileH() + tileSize;
			r.drawImageTile(image, (int) spriteX, (int) spriteY, this.type, 0);
		}
	}
}