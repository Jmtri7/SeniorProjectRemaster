// by: James Trinity
package game.entities;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;

import game.board.Tile;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.Arrays;

// entities with ai
public class Creature extends Entity {
	protected Animation idle = new Animation("idle", 0, Arrays.asList(0));
	protected Animation walking = new Animation("walk", 0.5f, Arrays.asList(1, 0, 2, 0));
	protected Animation attacking = new Animation("attack", 0.4f, Arrays.asList(3, 4, 5, 4, 0));

	protected Animation animation = idle;

	protected Tile destination = null;
	protected int direction = 2;
	
	protected Inventory inventory = new Inventory(this, null);

	protected ArrayList<Equipment> equipment = new ArrayList<Equipment>();

	// temporary agression towards player
	protected boolean angryAtPlayer = false;

	public Creature(Tile tile, String type) {
		super(tile);
		this.species = Species.getSpecies(type);
		if(this.species != null) {
			this.image = species.getImage();
			this.hp = species.getHp();
			this.hpMax = hp;
			this.damage = species.getDamage();
			this.light = species.getLight();
		}

		this.blocking = true;
	}

	// ITEMS

	// equipment only

	public void equip(Equipment item) {
		for(int i = 0; i < equipment.size(); i++) {

			// if the item is already equipped
			if(equipment.get(i).getId() == item.getId()) {
				equipment.get(i).setEquipped(false);
				equipment.remove(i);
				return;
			}

			// another item is already equipped in that slot
			if(equipment.get(i).getSlot().equals(item.getSlot())) {
				equipment.get(i).setEquipped(false);
				equipment.remove(i);
			}
		}

		item.setEquipped(true);
		equipment.add(item);
	}

	// all items

	public void takeItem() {
		if(!isDead()) {
			Item item = this.tile.removeItem();
			if(item != null) {
				inventory.addItem(item);
			}
		}
	}

	public void useItem() {
		if(!isDead() && inventory.getSelectedItem() != null) {
			inventory.useSelectedItem();
		}
	}

	public void dropItem() {
		if(!isDead()) {
			for(int i = 0; i < equipment.size(); i++) {
				if(equipment.get(i).getId() == inventory.getSelectedItem().getId()) {
					equipment.remove(i);
				}
			}

			inventory.dropSelectedItem();
		} 
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isAngryAtPlayer() {
		return angryAtPlayer;
	}

	public void setAngryAtPlayer(boolean value) {
		angryAtPlayer = value;
	}

	// ACTIONS

	public void damage(int damage, String damageType) {
		if(!this.isDead()) {

			// include equipment modifiers
			float armorModifier = 0;
			for(int i = 0; i < equipment.size(); i++) {
				if(equipment.get(i).isEquipped()) {
					armorModifier += equipment.get(i).getArmorModifier();
				}
			}

			damage = (int) (damage * (1 - armorModifier));

			this.hp -= damage;

			if(this.hp <= 0) die();
			else if(this.species.getInjurySound() != null && damage > 0) this.species.getInjurySound().play();
		}
	}

	public void die() {
		isDead = true;
		//tile.removeEntity(id);
		//tile = null;

		this.blocking = false;

		if(this.species.getDeathSound() != null) this.species.getDeathSound().play();

		while(inventory.getItems().size() > 0) inventory.dropSelectedItem();
	}

	public void attack() {
		if(!walking.isActive() && !attacking.isActive() && !isDead()) {

			Tile destination = tile.getNeighbor(direction);
			if(destination != null) {
				this.destination = destination;

				int damage = this.damage;

				// include equipment modifiers
				for(int i = 0; i < equipment.size(); i++) {
					if(equipment.get(i).isEquipped()) {
						damage += equipment.get(i).getDamageModifier();
					}
				}

				this.destination.hit(damage, "unarmed");

				attacking.start();
				animation = attacking;
			}

			if(this.species.getAttackSound() != null) this.species.getAttackSound().play();
		}
	}

	public void turn(int direction) {
		if(!walking.isActive() && !isDead()) this.direction = direction;
	}

	public void walk(int direction) {
		if(!walking.isActive() && !attacking.isActive() && !isDead()) {
			this.direction = direction;

			// re-align sprite image
			int tileSize = tile.getBoard().getTileSize();
			x = tile.getX() * tileSize;
			y = tile.getY() * tileSize;

			Tile destination = tile.getNeighbor(direction);
			if(destination != null && !destination.isBlocked(this)) {
				this.destination = destination;
				destination.setBlocked(true);

				walking.start();
				animation = walking;

				//if(this.species.getWalkingSound() != null) this.species.getWalkingSound().play();
			}
		}
	}

	public void update(GameContainer gc, float dt) {

		if(walking.isActive()) {
			if(
				destination != null 
				//&& walking.getTimer() / walking.getDuration() <= 0.5
				&& walking.getTimer() / walking.getDuration() <= 1
			) {
				tile.removeEntity(id);
				destination.addEntity(this);
				this.setTile(destination);
				destination.setBlocked(false);
				destination = null;
			}
			
			// move sprite image
			float moveDistance = this.tile.getSize() * dt / walking.getDuration();
			switch(direction) {
				case 0: y -= moveDistance; break;
				case 1: x += moveDistance; break;
				case 2: y += moveDistance; break;
				case 3: x -= moveDistance; break;
			}

			walking.tick(dt);

		}
		else if(attacking.isActive()) attacking.tick(dt);
		else if(idle.isActive()) idle.tick(dt);
		if(!animation.isActive()) {

			// re-align sprite image
			int tileSize = tile.getBoard().getTileSize();
			x = tile.getX() * tileSize;
			y = tile.getY() * tileSize;

			idle.start();
			animation = idle;
		}
	}

	public void render(GameContainer gc, Renderer r) {
		if(tile != null) {
			
			int tileSize = tile.getBoard().getTileSize();
			int tileX = tile.getX() * tileSize;
			int tileY = tile.getY() * tileSize;
	
			if(this.species != null && !isDead()) {

				float spriteX = x - image.getTileW() / 2 + tileSize / 2;
				float spriteY = y - image.getTileH() + tileSize;

				//r.drawFillRect(tileX, tileY, tileSize, tileSize, 0xffff0000);
				r.drawImageTile(image, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
				//r.drawRect((int) spriteX, (int) spriteY, image.getTileW(), image.getTileH(), 0xff00ff00);

				// draw equipment
				for(int i = 0; i < equipment.size(); i++) {
					r.drawImageTile(equipment.get(i).getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
				}

				if(light != null) r.drawLight(light, (int) (x + tileSize / 2), (int) (y));

				if(hp < hpMax) {
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize, 2, 0xffff0000);
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize * hp / hpMax, 2, 0xff00ff00);
				}
			} //else r.drawFillRect(tileX, tileY, tileSize, tileSize, 0xffff0000);
			
		}
	}

	public float getWalkSpeed() {
		return walking.getDuration();
	}

	public void setWalkSpeed(float value) {
		walking.setDuration(value);
	}

	public void setAttackSpeed(float value) {
		attacking.setDuration(value);
	}

	public int getDirection() {
		return direction;
	}

	public Equipment getWeapon() {
		return null;
	}
}