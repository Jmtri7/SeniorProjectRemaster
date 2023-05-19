// by: James Trinity
package game.entities;

// A timer
public class State {
	protected float duration;
	protected float timer;
	protected boolean active;

	public State(float duration) {
		this.duration = duration;

		timer = 0;
		active = false;
	}

	public void tick(float dt) {
		timer -= dt;
		if(timer <= 0) {
			active = false;
		}
	}

	public void start() {
		active = true;
		timer = duration;
	}

	public float getDuration() {
		return duration;
	}

	public float getTimer() {
		return timer;
	}

	public void setDuration(float value) {
		duration = value;
	}

	public boolean isActive() {
		return active;
	}
}