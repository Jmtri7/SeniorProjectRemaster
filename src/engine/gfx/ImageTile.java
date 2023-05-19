package engine.gfx;

public class ImageTile extends Image {
	private int tileW, tileH;

	public ImageTile(String path, int tileW, int tileH) {
		super(path);

		this.tileW = tileW;
		this.tileH = tileH;
	}

	public Image getTileImage(int tileX, int tileY) {
		int[] p = new int[tileW * tileH];

		for(int y = 0; y < tileH; y++) {
			for(int x = 0; x < tileW; x++) {
				int pixelIndex = (x + tileX * tileW) + (y + tileY * tileH) * this.getW();
				if(pixelIndex >= this.getP().length) {
					p[x + y * tileW] = 0xff000000;
				} else {
					p[x + y * tileW] = this.getP()[pixelIndex];
				}
			}
		}

		return new Image(p, tileW, tileH);
	}

	public int getTileW() {
		return tileW;
	}

	public int getTileH() {
		return tileH;
	}
}