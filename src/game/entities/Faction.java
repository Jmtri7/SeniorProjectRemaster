// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class Faction {
	private static HashMap<String, Faction> map = new HashMap<String, Faction>();

	private String name;
	private ArrayList<String> enemies;

	public Faction(String name, List<String> enemies) {
		this.name = name;
		if(enemies != null) this.enemies = new ArrayList<>(enemies);
		else this.enemies = new ArrayList<>();
		map.put(name, this);
	}

	public String getName() {
		return name;
	}

	public void setEnemy(String enemyName) {
		this.enemies.add(enemyName);
	}

	public boolean isEnemy(String enemyName) {
		// check the list of enemies
		for(int i = 0; i < enemies.size(); i++)
			if(enemies.get(i).equals(enemyName)) return true;

		return false;
	}

	public static Faction get(String name) {
		return map.get(name);
	}
}