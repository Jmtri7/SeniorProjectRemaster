// by: James Trinity
package game;

import java.util.HashMap;
import java.util.Map;

import engine.gfx.ImageTile;

public class ImageLoader {
	private static HashMap<String, ImageTile> map = new HashMap<String, ImageTile>();

	public static void loadImage(String id, String path, int tileW, int tileH) {
		map.put(id, new ImageTile(path, tileW, tileH));
	}

	public static ImageTile getImage(String id) {
		ImageTile image = map.get(id);

		if(image == null) System.out.println("Image (" + id + ") isn't loaded.");

		return image;
	}

	// attempts to load from map first
	public static ImageTile safeLoad(String id, String path, int tileW, int tileH) {
		ImageTile image = map.get(id);

		if(image == null) {
			System.out.println("Image (" + id + ") wasn't pre loaded.");

			map.put(id, new ImageTile(path, tileW, tileH));
		}

		return map.get(id);
	}
}