// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;

import game.board.Tile;

// items are entities that can be picked up and added to inventories
public class Item extends Entity {
	private static HashMap<String, Item> map = new HashMap<String, Item>();

	protected String type = null;

	protected boolean consumable;
	protected boolean equipped;
	protected boolean equipment;

	public Item(Tile tile, ImageTile image, String name) {
		super(tile, image);
		tile.removeEntity(this.id);
		tile.addItem(this);

		this.tag = name;
		this.invincible = true;
		this.consumable = false;
		this.equipment = false;
		this.equipped = false;
	}

	// item prototype
	public Item(ImageTile image, String name) {
		super(image);

		this.tag = name;
		this.invincible = true;
		this.consumable = false;
		this.equipment = false;
		this.equipped = false;
	}

	public void use(Creature user) {

	}

	public boolean isEquipped() {
		return equipped;
	}

	public void setEquipped(boolean value) {
		equipped = value;
	}

	public boolean isEquipment() {
		return equipment;
	}

	public boolean isConsumable() {
		return consumable;
	}

	public String getType() {
		return type;
	}

	public String getSlot() {
		return null;
	}

	public static Item get(String name) {
		return map.get(name);
	}

	public void update(GameContainer gc, float dt) {
		int tileSize = tile.getBoard().getTileSize();
		x = tile.getX() * tileSize;
		y = tile.getY() * tileSize;
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