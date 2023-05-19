// by: James Trinity
package game.entities;

import engine.GameContainer;
import engine.Renderer;
import engine.audio.SoundClip;
import engine.gfx.ImageTile;
import engine.gfx.Light;

import game.AudioLoader;

import game.board.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.KeyEvent;

// A sprite on the game board
public class Entity extends Sprite {
	protected Species species;
	protected String faction;

	protected String tag = "none";

	protected static int entityId = 0;
	protected int id;

	protected Tile tile = null;

	// stats
	protected boolean blocking;
	protected int damage;
	protected Light light;
	protected int hp, hpMax;
	protected boolean isDead;
	protected boolean respawns;
	protected float respawnTime, respawnTimer;
	protected boolean invincible;
	protected boolean isFloating;

	protected SoundClip damageNoise;
	protected SoundClip deathNoise;

	protected String weakness;

	public Entity(Tile tile) {
		super(tile.getX() * tile.getSize(), tile.getY() * tile.getSize());
		this.tile = tile;
		this.tile.addEntity(this);

		this.blocking = false;
		this.hpMax = 16;
		this.hp = this.hpMax;
		this.isDead = false;
		this.respawns = false;
		this.invincible = false;
		this.respawnTime = 0;
		this.respawnTimer = this.respawnTime;
		this.isFloating = false;

		this.weakness = null;

		this.id = Entity.entityId;
		Entity.entityId++;
	}

	public Entity(Tile tile, ImageTile image) {
		super(tile.getX() * tile.getSize(), tile.getY() * tile.getSize(), image);
		this.tile = tile;
		this.tile.addEntity(this);

		this.blocking = false;
		this.hpMax = 16;
		this.hp = this.hpMax;
		this.isDead = false;
		this.respawns = false;
		this.invincible = false;
		this.respawnTime = 0;
		this.respawnTimer = this.respawnTime;
		this.isFloating = false;

		this.weakness = null;

		this.id = Entity.entityId;
		Entity.entityId++;
	}

	// entity prototype
	public Entity(ImageTile image) {
		super(0, 0, image);

		this.blocking = false;
		this.hpMax = 10;
		this.hp = this.hpMax;
		this.isDead = false;
		this.respawns = false;
		this.invincible = false;
		this.respawnTime = 0;
		this.respawnTimer = this.respawnTime;

		this.weakness = null;
	}

	// UNTESTED
	// entity relying on image ID
	public Entity(Tile tile, String imageId) {
		super(tile.getX() * tile.getSize(), tile.getY() * tile.getSize(), imageId);
		this.tile = tile;
		this.tile.addEntity(this);

		this.blocking = false;
		this.hpMax = 16;
		this.hp = this.hpMax;
		this.isDead = false;
		this.respawns = false;
		this.invincible = false;
		this.respawnTime = 0;
		this.respawnTimer = this.respawnTime;
		this.isFloating = false;

		this.weakness = null;

		this.id = Entity.entityId;
		Entity.entityId++;
	}

	public void update(GameContainer gc, float dt) {
		if(respawns == true && isDead) {
			respawnTimer -= dt;
			if(respawnTimer <= 0) {
				isDead = false;
				hp = hpMax;
			}
		}
	}

	public void render(GameContainer gc, Renderer r) {
	}

	public boolean isProjectile() {
		return false;
	}

	public void damage(int damage, String damageType) {
		if(!invincible && !isDead) {
			this.hp -= damage;
			if(this.hp <= 0) die();
			else if(this.damageNoise != null && damage > 0) this.damageNoise.play();
		}
	}

	public void die() {
		isDead = true;
		respawnTimer = respawnTime;
		if(this.deathNoise != null) this.deathNoise.play();
	}

	public int getId() {
		return id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String newTag) {
		this.tag = newTag;
	}

	public Tile getTile() {
		return tile;
	}

	public int getHp() {
		return hp;
	}

	public boolean isDead() {
		return isDead;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public boolean isFloating() {
		return isFloating;
	}

	public void setFloating(boolean value) {
		isFloating = value;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public void setBlocking(boolean value) {
		this.blocking = value;
	}

	public void setInvincible(boolean value) {
		this.invincible = value;
	}

	public void setRespawns(boolean value) {
		this.respawns = value;
	}

	public void setRespawnTime(float value) {
		this.respawnTime = value;
	}

	public void setDamageNoise(String name) {
		this.damageNoise = AudioLoader.safeLoad(name, "/res/sounds/" + name + ".wav");
	}

	public void setDeathNoise(String name) {
		this.deathNoise = AudioLoader.safeLoad(name, "/res/sounds/" + name + ".wav");
	}

	public void setFaction(String factionName) {
		this.faction = factionName;
	}

	public void setWeakness(String type) {
		this.weakness = type;
	}

	public String getWeakness() {
		return this.weakness;
	}

	public Faction getFaction() {
		return Faction.get(faction);
	}

	public Species getSpecies() {
		return species;
	}

	public Light getLight() {
		return light;
	}
}