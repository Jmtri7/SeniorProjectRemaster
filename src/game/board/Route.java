// by: James Trinity
package game.board;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class Route {
	private static HashMap<String, Route> map = new HashMap<String, Route>();

	private String name;
	private ArrayList<Tile> sites;

	public Route(String name, List<Tile> sites) {
		this.name = name;
		if(sites != null) this.sites = new ArrayList<>(sites);
		else this.sites = new ArrayList<>();
		map.put(name, this);
	}

	public Tile getSite(int index) {
		return sites.get(index);
	}

	public boolean isEnd(int index) {
		if(index == 0 || index == sites.size() - 1) {
			return true;
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public static ArrayList<Tile> get(String name) {
		return map.get(name).sites;
	}
}