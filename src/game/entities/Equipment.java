// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;

import game.board.Tile;

// an item that a humanoid can wear
public class Equipment extends Item {
	private static HashMap<String, Equipment> map = new HashMap<String, Equipment>();

	protected String slot = null;

	protected int damageModifier;
	protected float armorModifier;

	protected String damageType;

	// copy constructor
	public Equipment(Tile tile, String name) {
		super(tile, map.get(name).getGraphic(), name);

		Equipment prototype = map.get(name);

		this.type = prototype.getType();
		this.slot = prototype.getSlot();

		// additive damage
		this.damageModifier = prototype.getDamageModifier();
		// percent damage reduction
		this.armorModifier = prototype.getArmorModifier();

		this.damageType = prototype.getDamageType();
	}

	// prototype constructor
	public Equipment(ImageTile image, String name, String slot, int damageModifier, float armorModifier, String damageType) {
		super(image, name);

		this.type = "equipment";
		this.slot = slot;

		// additive damage
		this.damageModifier = damageModifier;
		// percent damage reduction
		this.armorModifier = armorModifier;

		this.damageType = damageType;
	}

	public int getDamageModifier() {
		return damageModifier;
	}

	public float getArmorModifier() {
		return armorModifier;
	}

	public String getDamageType() {
		return damageType;
	}

	public void use(Creature user) {
		user.equip(this);
	}

	public String getSlot() {
		return slot;
	}

	public void render(GameContainer gc, Renderer r) {
		int tileSize = tile.getBoard().getTileSize();
		int tileX = tile.getX() * tileSize;
		int tileY = tile.getY() * tileSize;
		float spriteX = x - image.getTileW() / 2 + tileSize / 2;
		float spriteY = y - image.getTileH() + tileSize;
		r.drawImageTile(image, (int) spriteX, (int) spriteY, 0, 4);
	}

	// creates a new type of equipment
	public static void create(ImageTile image, String name, String slot, int damageModifier, float armorModifier, String damageType) {
		Equipment item = new Equipment(image,  name,  slot,  damageModifier,  armorModifier,  damageType);

		map.put(name, item);
	}
}