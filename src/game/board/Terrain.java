// by: James Trinity
package game.board;

import java.util.HashMap;
import java.util.Map;

import engine.gfx.Image;
import engine.gfx.Light;

public class Terrain {
	private static HashMap<String, Terrain> map = new HashMap<String, Terrain>();

	private String type;
	private Image image;
	private Light light;
	private boolean blocked;

	public Terrain(String type, Image image, Integer lightColor, boolean blocked) {
		this.type = type;
		this.image = image;
		if(lightColor != null) this.light = new Light(image.getW() * 2, lightColor);
		this.blocked = blocked;

		Terrain.map.put(type, this);
	}

	public static Terrain getTerrain(String type) {
		return map.get(type);
	}

	public String getType() {
		return type;
	}

	public Image getImage() {
		return image;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public Light getLight() {
		return light;
	}
}