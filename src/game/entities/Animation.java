// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class Animation extends State {
	private static HashMap<String, Animation> map = new HashMap<String, Animation>();

	private ArrayList<Integer> frames;
	private float frameTime;

	public Animation(String name, float duration, List<Integer> frames) {
		super(duration);

		this.frames = new ArrayList<>(frames);
		this.frameTime = duration / frames.size();

		Animation.map.put(name, this);
	}

	public void setDuration(float value) {
		this.duration = value;
		this.frameTime = duration / frames.size();
	}

	public static Animation getAnimation(String name) {
		return map.get(name);
	}

	public void start() {
		// why is this getting called so many times?
		//System.out.println("start");
		active = true;
		timer = duration;
	}

	public int getFrame() {
		int frameIndex = (int) (((duration - timer) / frameTime) % frames.size());

		return frames.get(frameIndex);
	}
}