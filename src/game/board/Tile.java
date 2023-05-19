// by: James Trinity
package game.board;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import engine.gfx.ImageTile;
import engine.gfx.Light;

import game.entities.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.entities.Item;

public class Tile {
	private Terrain terrain = null;

	private static int tileId = 0;
	
	private Board board = null;
	private int id;

	private int size;
	private int x, y;

	private ArrayList<Entity> entities;
	// WIP differentiating items
	private ArrayList<Item> items;

	private Tile[] neighbors;

	private boolean blocked = false;

	public Tile(int x, int y, Board board) {
		this.board = board;
		this.size = board.getTileSize();

		this.x = x;
		this.y = y;

		this.entities = new ArrayList<Entity>();
		this.items = new ArrayList<Item>();

		this.neighbors = new Tile[4];
		if(y == 0) neighbors[0] = null;
		else {
			neighbors[0] = board.getTile(x, y - 1);
			neighbors[0].setNeighbor(2, this);
		}

		if(x == 0) neighbors[3] = null;
		else {
			neighbors[3] = board.getTile(x - 1, y);
			neighbors[3].setNeighbor(1, this);
		}

		id = Tile.tileId;
		Tile.tileId++;
	}

	// items

	public Item removeItem() {
		Item item;

		if(items.size() > 0) {
			item = items.get(0);
			items.remove(0);
			return item;
		}

		return null;
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	// tile attacked

	// TEST knows who hit
	public void hit(Entity attacker, int damage, String damageType) {
		for(int i = 0; i < entities.size(); i++) {
			Entity target = entities.get(i);

			if(
				target.getFaction() != null
				&& attacker.getFaction().getName().equals("player")
				&& !target.getFaction().isEnemy("player")
			) {
				target.getFaction().setEnemy("player");
			}

			target.damage(damage, damageType);
			//if(target.isDead()) i--;
		}
	}

	public void hit(int damage, String damageType) {
		for(int i = 0; i < entities.size(); i++) {
			Entity target = entities.get(i);
			target.damage(damage, damageType);
			//if(target.isDead()) i--;
		}
	}

	// entities

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	public void removeEntity(int id) {
		for(int i = 0; i < entities.size(); i++) {
			if(entities.get(i).getId() == id) entities.remove(i);
		}
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	// misc

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSize() {
		return size;
	}

	public int getId() {
		return id;
	}

	public Board getBoard() {
		return board;
	}

	public Tile getNeighbor(int direction) {
		return this.neighbors[direction];
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public void setBlocked(boolean value) {
		blocked = value;
	}

	public boolean isBlocked() {
		if(terrain != null && terrain.isBlocked()) return true;
		if(blocked == true) return true;

		boolean blocked = false;
		for(int i = 0; i < entities.size(); i++) {
			blocked = blocked || entities.get(i).isBlocking();
		}

		return blocked;
	}

	// check against flying
	public boolean isBlocked(Entity toWhom) {
		if(terrain != null && terrain.isBlocked() && !toWhom.isFloating()) return true;
		if(blocked == true) return true;

		boolean blocked = false;
		for(int i = 0; i < entities.size(); i++) {
			blocked = blocked || entities.get(i).isBlocking();
		}

		return blocked;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setNeighbor(int direction, Tile tile) {
		this.neighbors[direction] = tile;
	}

	public void setTerrain(String type) {
		terrain = Terrain.getTerrain(type);
	}

	// ENGINE

	public void update(GameContainer gc, float dt) {
		for(int i = 0; i < entities.size(); i++) entities.get(i).update(gc, dt);
	}

	public void render(GameContainer gc, Renderer r) {
		if(terrain != null) {
			float screenX = x * size;
			float screenY = y * size;

			float spriteX = screenX - terrain.getImage().getW() / 2 + size / 2;
			float spriteY = screenY - terrain.getImage().getH() + size;

			r.drawImage(terrain.getImage(), (int) spriteX, (int) spriteY);
			if(terrain.getLight() != null) r.drawLight(terrain.getLight(), this.x * size + size / 2, this.y * size + size / 2);
		}
	}
}