// by: James Trinity
package game.entities;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import engine.gfx.Light;

import game.board.Tile;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.Arrays;

public class Humanoid extends Creature {
	protected ImageTile head;
	protected ImageTile torso;
	protected ImageTile rightArm;
	protected ImageTile leftArm;
	protected ImageTile legs;

	protected ImageTile underwear;

	protected Equipment hair;

	protected Equipment hat;
	protected Equipment shirt;
	protected Equipment pants;
	protected Equipment gloves;
	protected Equipment shoes;

	protected Equipment weapon;

	public Humanoid(Tile tile, String type) {
		super(tile, type);

		head = new ImageTile("/res/creature/" + type + "/head.png", 60, 60);
		torso = new ImageTile("/res/creature/" + type + "/torso.png", 60, 60);
		rightArm = new ImageTile("/res/creature/" + type + "/rightArm.png", 60, 60);
		leftArm = new ImageTile("/res/creature/" + type + "/leftArm.png", 60, 60);
		legs = new ImageTile("/res/creature/" + type + "/legs.png", 60, 60);

		underwear = new ImageTile("/res/creature/" + type + "/underwear.png", 60, 60);
	}

	public void equip(Equipment item) {
		if(item.getSlot().equals("hair")) {
			// wearing a hat
			if(hair != null) {
				// wearing that hat
				if(item.getId() == hair.getId()) {
					hair.setEquipped(false);
					hair = null;
					return;
				}
				// wearing a different hat
				else {
					hair.setEquipped(false);
					hair = item;
					hair.setEquipped(true);
					return;
				}
			}
			// not wearing any hat
			else {
				hair = item;
				hair.setEquipped(true);
			}

			return;
		} else if(item.getSlot().equals("hat")) {
			// wearing a hat
			if(hat != null) {
				// wearing that hat
				if(item.getId() == hat.getId()) {
					hat.setEquipped(false);
					hat = null;
					return;
				}
				// wearing a different hat
				else {
					hat.setEquipped(false);
					hat = item;
					hat.setEquipped(true);
					return;
				}
			}
			// not wearing any hat
			else {
				hat = item;
				hat.setEquipped(true);
			}

			return;
		} else if(item.getSlot().equals("shirt")) {
			if(shirt != null) {
				if(item.getId() == shirt.getId()) {
					shirt.setEquipped(false);
					shirt = null;
					return;
				}
				else {
					shirt.setEquipped(false);
					shirt = item;
					shirt.setEquipped(true);
					return;
				}
			}
			else {
				shirt = item;
				shirt.setEquipped(true);
			}

			return;
		} else if(item.getSlot().equals("pants")) {
			if(pants != null) {
				if(item.getId() == pants.getId()) {
					pants.setEquipped(false);
					pants = null;
					return;
				}
				else {
					pants.setEquipped(false);
					pants = item;
					pants.setEquipped(true);
					return;
				}
			}
			else {
				pants = item;
				pants.setEquipped(true);
			}

			return;
		} else if(item.getSlot().equals("shoes")) {
			if(shoes != null) {
				if(item.getId() == shoes.getId()) {
					shoes.setEquipped(false);
					shoes = null;
					return;
				}
				else {
					shoes.setEquipped(false);
					shoes = item;
					shoes.setEquipped(true);
					return;
				}
			}
			else {
				shoes = item;
				shoes.setEquipped(true);
			}

			return;
		} else if(item.getSlot().equals("weapon")) {
			if(weapon != null) {
				if(item.getId() == weapon.getId()) {
					weapon.setEquipped(false);
					weapon = null;
					return;
				}
				else {
					weapon.setEquipped(false);
					weapon = item;
					weapon.setEquipped(true);
					return;
				}
			}
			else {
				weapon = item;
				weapon.setEquipped(true);
			}

			return;
		}
	}

	public void dropItem() {
		Item item = inventory.getSelectedItem();

		if(item != null) {
			if(item.getSlot().equals("hair")) {
				if(hair != null) {
					if(item.getId() == hair.getId()) {
						hair.setEquipped(false);
						hair = null;
					}
				}
			} else if(item.getSlot().equals("hat")) {
				if(hat != null) {
					if(item.getId() == hat.getId()) {
						hat.setEquipped(false);
						hat = null;
					}
				}
			} else if(item.getSlot().equals("shirt")) {
				if(shirt != null) {
					if(item.getId() == shirt.getId()) {
						shirt.setEquipped(false);
						shirt = null;
					}
				}
			} else if(item.getSlot().equals("pants")) {
				if(pants != null) {
					if(item.getId() == pants.getId()) {
						pants.setEquipped(false);
						pants = null;
					}
				}
			} else if(item.getSlot().equals("shoes")) {
				if(shoes != null) {
					if(item.getId() == shoes.getId()) {
						shoes.setEquipped(false);
						shoes = null;
					}
				}
			} else if(item.getSlot().equals("weapon")) {
				if(weapon != null) {
					if(item.getId() == weapon.getId()) {
						weapon.setEquipped(false);
						weapon = null;
					}
				}
			}
		}
		

		inventory.dropSelectedItem();
	}

	public void damage(int damage, String damageType) {
		if(!invincible && !this.isDead()) {

			// include equipment modifiers
			float armorModifier = 0;
			if(hat != null) armorModifier += hat.getArmorModifier();
			if(shirt != null) armorModifier += shirt.getArmorModifier();
			if(pants != null) armorModifier += pants.getArmorModifier();
			if(gloves != null) armorModifier += gloves.getArmorModifier();
			if(shoes != null) armorModifier += shoes.getArmorModifier();
			if(weapon != null) armorModifier += weapon.getArmorModifier();

			damage = (int) (damage * (1 - armorModifier));

			this.hp -= damage;

			if(this.hp <= 0) die();
			else if(this.species.getInjurySound() != null && damage > 0) this.species.getInjurySound().play();
		}
	}

	public void attack() {
		if(!walking.isActive() && !attacking.isActive() && !isDead()) {

			Tile destination = tile.getNeighbor(direction);
			if(destination != null) {
				this.destination = destination;

				int damage = this.damage;

				// include equipment modifiers
				if(hat != null) damage += hat.getDamageModifier();
				if(shirt != null) damage += shirt.getDamageModifier();
				if(pants != null) damage += pants.getDamageModifier();
				if(gloves != null) damage += gloves.getDamageModifier();
				if(shoes != null) damage += shoes.getDamageModifier();

				if(weapon != null) {
					damage += weapon.getDamageModifier();

					if(weapon.getTag().equals("bow")) {
						Projectile arrow = new Projectile(this, destination, "arrow", direction);
						//arrow.setFaction(this.getFaction().getName());
					}
					else this.destination.hit(this, damage, weapon.getDamageType());
				} else {
					this.destination.hit(this, damage, "unarmed");
				}

				attacking.start();
				animation = attacking;
			}

			if(this.species.getAttackSound() != null) this.species.getAttackSound().play();
		}
	}

	public void render(GameContainer gc, Renderer r) {
		if(tile != null) {
			
			int tileSize = tile.getBoard().getTileSize();
			int tileX = tile.getX() * tileSize;
			int tileY = tile.getY() * tileSize;

			//r.drawFillRect(tileX, tileY, tileSize, tileSize, 0xffff0000);
	
			if(this.species != null && !isDead()) {

				float spriteX = x - image.getTileW() / 2 + tileSize / 2;
				float spriteY = y - image.getTileH();

				switch(direction) {
					case 0:
						if(weapon != null) r.drawImageTile(weapon.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(rightArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(legs, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(shoes != null) r.drawImageTile(shoes.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(torso, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants == null && underwear != null) r.drawImageTile(underwear, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants != null) r.drawImageTile(pants.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);	
						if(shirt != null) r.drawImageTile(shirt.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(leftArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
					break;
					case 1:
						r.drawImageTile(leftArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(legs, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(shoes != null) r.drawImageTile(shoes.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(torso, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants == null && underwear != null) r.drawImageTile(underwear, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants != null) r.drawImageTile(pants.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);	
						if(shirt != null) r.drawImageTile(shirt.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(rightArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(weapon != null) r.drawImageTile(weapon.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
					break;
					case 2:
						r.drawImageTile(rightArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(legs, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(shoes != null) r.drawImageTile(shoes.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(torso, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants == null && underwear != null) r.drawImageTile(underwear, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants != null) r.drawImageTile(pants.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);	
						if(shirt != null) r.drawImageTile(shirt.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(leftArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(weapon != null) r.drawImageTile(weapon.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
					break;
					case 3:
						if(weapon != null) r.drawImageTile(weapon.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(rightArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(legs, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(shoes != null) r.drawImageTile(shoes.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(torso, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						if(pants == null && underwear != null) r.drawImageTile(underwear, (int) spriteX, (int) spriteY, animation.getFrame(), direction);	
						if(pants != null) r.drawImageTile(pants.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);	
						if(shirt != null) r.drawImageTile(shirt.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
						r.drawImageTile(leftArm, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
					break;
				}

				// draw head
				r.drawImageTile(head, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
				if(hair != null) r.drawImageTile(hair.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);
				// draw hat
				if(hat != null) r.drawImageTile(hat.getGraphic(), (int) spriteX, (int) spriteY, animation.getFrame(), direction);

				if(light != null) r.drawLight(light, (int) (x + tileSize / 2), (int) (y));

				if(hp < hpMax) {
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize, 2, 0xffff0000);
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize * hp / hpMax, 2, 0xff00ff00);
				}
			}
		}
	}

	public Equipment getWeapon() {
		return weapon;
	}
}