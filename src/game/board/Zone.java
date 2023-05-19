// by: James Trinity
package game.board;

import engine.GameContainer;
import engine.audio.SoundClip;

// should belong to a board and represent a rectangular portion of it.
// updated when board is updated and changes music if entered
public class Zone {

	private int ambientColor;
	private SoundClip music;

	private int x, y, width, height;

	public Zone(int x, int y, int width, int height, int ambientColor, SoundClip music) {
		this.ambientColor = ambientColor;
		this.music = music;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean contains(int x, int y) {
		if(x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height) return true;
		else return false;
	}

	public void update(GameContainer gc, float dt) {
	}

	public int getAmbientColor() {
		return ambientColor;
	}

	public SoundClip getMusic() {
		return music;
	}
}