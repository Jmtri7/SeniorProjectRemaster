// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;

import game.board.Tile;

// items that are destroyed after use
public class Consumable extends Item {
	public Consumable(Tile tile, ImageTile image, String tag) {
		super(tile, image, tag);

		this.type = "consumable";
		this.consumable = true;
	}

	public void use(Creature user) {
		if(tag.equals("potion")) {
			user.damage(-50, "none");
		} else if(tag.equals("scrollFireNova")) {
			new Projectile(user.getTile().getNeighbor(0), "firebolt", 0);
			new Projectile(user.getTile().getNeighbor(1), "firebolt", 1);
			new Projectile(user.getTile().getNeighbor(2), "firebolt", 2);
			new Projectile(user.getTile().getNeighbor(3), "firebolt", 3);
		}
	}

	public void render(GameContainer gc, Renderer r) {
		int tileSize = tile.getBoard().getTileSize();
		int tileX = tile.getX() * tileSize;
		int tileY = tile.getY() * tileSize;
		float spriteX = x - image.getTileW() / 2 + tileSize / 2;
		float spriteY = y - image.getTileH() + tileSize;
		r.drawImageTile(image, (int) spriteX, (int) spriteY, 0, 0);
	}
}