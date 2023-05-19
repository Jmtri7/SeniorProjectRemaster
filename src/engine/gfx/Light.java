package engine.gfx;

public class Light {
	public static final int NONE = 0;
	public static final int FULL = 1;
	public static final int GLOW = 2;
	public static final int CEILING = 3;
	public static final int WALL = 4;

	private int radius, diameter;
	private int color;

	private int[] lm;

	public Light(int radius, int color) {
		this.radius = radius;
		this.diameter = radius * 2;
		this.color = color;

		lm = new int[diameter * diameter];

		for(int x = 0; x < diameter; x++) {
			for(int y = 0; y < diameter; y++) {
				double distance = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));

				if(distance < radius) {
					double power = 1 - distance / radius;
					lm[x + y * diameter] = ((int)(((color >> 16) & 0xff) * power)) << 16 | ((int)(((color >> 8) & 0xff) * power)) << 8 | ((int)((color & 0xff) * power));
				} else {
					lm[x + y * diameter] = 0;
				}
			}
		}
	}

	public int getLightValue(int x, int y) {
		if(x < 0 || x >= diameter || y < 0 || y >= diameter) return 0;

		return lm[x + y * diameter];
	}

	public int getColor() {
		return color;
	}

	public int getRadius() {
		return radius;
	}

	public int getDiameter() {
		return diameter;
	}

	public int[] getLm() {
		return lm;
	}
}