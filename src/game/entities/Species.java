// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import engine.gfx.ImageTile;
import engine.gfx.Light;
import engine.audio.SoundClip;

public class Species {
	private static HashMap<String, Species> map = new HashMap<String, Species>();

	private String type;
	private int hp, damage;
	private ImageTile image;
	private Light light;
	private SoundClip injurySound, deathSound, attackSound, walkingSound;

	public Species(
		String type,
		int hp, int damage,
		ImageTile image,
		Light light,
		SoundClip injurySound,
		SoundClip deathSound,
		SoundClip attackSound,
		SoundClip walkingSound
	) {
		this.type = type;
		this.hp = hp;
		this.damage = damage;
		this.image = image;
		this.image.setLightBlock(Light.GLOW);
		this.light = light;
		this.injurySound = injurySound;
		this.deathSound = deathSound;
		this.attackSound = attackSound;
		this.walkingSound = walkingSound;

		Species.map.put(type, this);
	}

	public static Species getSpecies(String type) {
		return map.get(type);
	}

	public String getType() {
		return type;
	}

	public int getHp() {
		return hp;
	}

	public int getDamage() {
		return damage;
	}

	public ImageTile getImage() {
		return image;
	}

	public Light getLight() {
		return light;
	}

	public SoundClip getInjurySound() {
		return injurySound;
	}

	public SoundClip getDeathSound() {
		return deathSound;
	}

	public SoundClip getAttackSound() {
		return attackSound;
	}

	public SoundClip getWalkingSound() {
		return walkingSound;
	}
}