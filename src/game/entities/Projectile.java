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

// entity that keeps moving until it hits something
public class Projectile extends Entity {
	private static HashMap<String, Projectile> map = new HashMap<String, Projectile>();

	protected Species species;
	protected int damage;
	protected Light light;

	protected Animation idle = new Animation("idle", 0, Arrays.asList(0));
	protected Animation walking = new Animation("walk", 0.1f, Arrays.asList(0));
	protected Animation attacking = new Animation("attack", 0f, Arrays.asList(0));

	protected Animation animation = idle;

	protected Tile destination = null;
	protected int direction = 2;

	protected boolean diesOnImpact = true;

	protected Entity owner = null;

	// copy constructor
	// who dunit
	public Projectile(Entity owner, Tile tile, String type, int direction) {
		super(tile, map.get(type).getGraphic());

		this.owner = owner;
		
		this.hp = 1;
		this.hpMax = hp;
		this.damage = map.get(type).getDamage();

		this.direction = direction;
		this.blocking = false;
		this.isFloating = true;
	}

	// copy constructor
	public Projectile(Tile tile, String type, int direction) {
		super(tile, map.get(type).getGraphic());
		
		this.hp = 1;
		this.hpMax = hp;
		this.damage = map.get(type).getDamage();

		this.direction = direction;
		this.blocking = false;
		this.isFloating = true;
	}
	
	// create prototype
	public Projectile(ImageTile image, String type, int damage) {
		super(image);
		
		this.hp = 1;
		this.hpMax = hp;
		this.damage = damage;

		this.blocking = false;
		this.isFloating = true;

		map.put(type, this);
	}

	public boolean isProjectile() {
		return true;
	}

	// actions

	public void damage(int damage, String damageType) {
		if(!this.isDead()) {
			this.hp -= damage;
			if(this.hp <= 0) die();
			//else if(this.species.getInjurySound() != null && damage > 0) this.species.getInjurySound().play();
		}
	}

	public void die() {
		isDead = true;
		this.blocking = false;
		//if(this.species.getDeathSound() != null) this.species.getDeathSound().play();
	}

	public void attack() {
		if(!walking.isActive() && !attacking.isActive() && !isDead()) {
			Tile destination = tile.getNeighbor(direction);
			if(destination != null) {
				this.destination = destination;
				if(this.owner == null) {
					this.destination.hit(damage, "piercing");
					this.tile.hit(damage, "piercing");
				} else {
					this.destination.hit(this.owner, damage, "piercing");
					this.tile.hit(this.owner, damage, "piercing");
				}
				
				attacking.start();
				animation = attacking;
			}
			//if(this.species.getAttackSound() != null) this.species.getAttackSound().play();
		}
	}

	public void turn(int direction) {
		if(!walking.isActive() && !isDead()) this.direction = direction;
	}

	// arrow sometimes passes through target
	public void walk(int direction) {
		if(!walking.isActive() && !attacking.isActive() && !isDead()) {
			this.direction = direction;
			// re-align sprite image
			int tileSize = tile.getBoard().getTileSize();
			x = tile.getX() * tileSize;
			y = tile.getY() * tileSize;
			Tile destination = tile.getNeighbor(direction);
			if(destination != null && (destination.isBlocked(this) || tile.isBlocked(this))) attack();
			else if(destination != null && !destination.isBlocked(this)) {
				this.destination = destination;
				walking.start();
				animation = walking;
			}
			else if(destination == null) die();
		}
	}

	public void update(GameContainer gc, float dt) {
		walk(direction);
		if(walking.isActive()) {
			if(
				destination != null 
				&& walking.getTimer() / walking.getDuration() <= 1
			) {
				tile.removeEntity(id);
				destination.addEntity(this);
				this.setTile(destination);
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
		else if(attacking.isActive()) {
			attacking.tick(dt);
			if(
				diesOnImpact == true 
				&& !attacking.isActive()
			) die();
		}
		else if(idle.isActive()) idle.tick(dt);
		if(!animation.isActive()) {
			// re-align sprite image
			int tileSize = tile.getBoard().getTileSize();
			x = tile.getX() * tileSize;
			y = tile.getY() * tileSize;
			// start idling
			idle.start();
			animation = idle;
		}
	}

	public void render(GameContainer gc, Renderer r) {
		if(tile != null) {
			// get the tile location
			int tileSize = tile.getBoard().getTileSize();
			int tileX = tile.getX() * tileSize;
			int tileY = tile.getY() * tileSize;
			// draw the projectile
			if(!isDead()) {
				// calculate sprite location
				float spriteX = x - image.getTileW() / 2 + tileSize / 2;
				float spriteY = y - image.getTileH() + tileSize;
				// draw sprite
				r.drawImageTile(image, (int) spriteX, (int) spriteY, animation.getFrame(), direction);
				// draw light
				if(light != null) r.drawLight(light, (int) (x + tileSize / 2), (int) (y));
				// draw hp bar
				if(hp < hpMax) {
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize, 2, 0xffff0000);
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize * hp / hpMax, 2, 0xff00ff00);
				}
			}
			
		}
	}

	public void setWalkSpeed(float value) {
		walking.setDuration(value);
	}

	public Species getSpecies() {
		return species;
	}

	public int getDamage() {
		return damage;
	}
}