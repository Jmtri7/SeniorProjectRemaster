package engine;

import java.awt.image.DataBufferInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import engine.gfx.Image;
import engine.gfx.ImageTile;
import engine.gfx.Font;
import engine.gfx.Light;
import engine.gfx.LightRequest;

public class Renderer {
	private ArrayList<LightRequest> lightRequest = new ArrayList<LightRequest>();

	private int pW, pH;
	private int[] p;

	private int[] lm;
	private int[] lb;
	private int ambientColor = -1;
	private float fadeAccumulator = 0;

	private Font font = Font.STANDARD;

	private int camX, camY;

	private final boolean DRAWLIGHT = true;

	public Renderer(GameContainer gc) {
		pW = gc.getWidth();
		pH = gc.getHeight();
		p = ((DataBufferInt) gc.getWindow().getImage().getRaster().getDataBuffer()).getData();

		lm = new int[p.length];
		lb = new int[p.length];
	}

	public void clear() {
		for(int i = 0; i < p.length; i++) {
			p[i] = 0;
			lm[i] = ambientColor;
		}
	}

	public void process() {
		if(DRAWLIGHT) {
			for(int i = 0; i < lightRequest.size(); i++) {
				LightRequest l = lightRequest.get(i);
				drawLightLines(l.light, l.locX, l.locY);
			}

		// merge lightmap and pixel map
			for(int i = 0; i < p.length; i++) {
				float r = ((lm[i] >> 16) & 0xff) / 255f;
				float g = ((lm[i] >> 8) & 0xff) / 255f;
				float b = (lm[i] & 0xff) / 255f;

				p[i] = ((int)(((p[i] >> 16) & 0xff) * r)) << 16 | ((int)(((p[i] >> 8) & 0xff) * g)) << 8 | ((int)((p[i] & 0xff) * b));
			}
		}

		lightRequest.clear();
	}

	public void setPixel(int x, int y, int value) {
		int alpha = (value >> 24) & 0xff;

		if((x < 0 || x >= pW || y < 0 || y >= pH) || alpha == 0 ) return;

		int index = x + y * pW;

		// mixes alpha colors
		if(alpha == 255) p[index] = value;
		else {
			int pixelColor = p[index];

			int newRed = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));
			int newGreen = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));
			int newBlue = ((pixelColor) & 0xff) - (int)((((pixelColor) & 0xff) - ((value) & 0xff)) * (alpha / 255f));

			p[index] = (newRed << 16 | newGreen << 8 | newBlue);
		}
	}

	public void setLightMap(int x, int y, int value) {
		if(x < 0 || x >= pW || y < 0 || y >= pH) return;

		int baseColor = lm[x + y * pW];

		int maxRed = Math.max((baseColor >> 16) & 0xff, (value >> 16) & 0xff);
		int maxGreen = Math.max((baseColor >> 8) & 0xff, (value >> 8) & 0xff);
		int maxBlue = Math.max(baseColor & 0xff, value & 0xff);

		lm[x + y * pW] = (maxRed << 16 | maxGreen << 8 | maxBlue);
	}

	public void setLightBlock(int x, int y, int value) {
		if(x < 0 || x >= pW || y < 0 || y >= pH) return;

		lb[x + y * pW] = value;
	}

	public void drawText(String text, int offX, int offY, int color) {
		offX -= camX;
		offY -= camY;

		text = text.toUpperCase();

		int offset = 0;

		for(int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i) - 32;

			for(int y = 0; y < font.getFontImage().getH(); y++) {
				for(int x = 0; x < font.getWidths()[unicode]; x++) {
					if(font.getFontImage().getP()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getW()] == 0xffffffff) {
						setPixel(offX + x + offset, offY + y, color);
					}
				}
			}

			offset += font.getWidths()[unicode];
		}
	}

	// DRAW IMAGES

	public void drawImage(Image image, int offX, int offY) {
		offX -= camX;
		offY -= camY;

		if(offX < -image.getW()) return;
		if(offY < -image.getH()) return;
		if(offX >= pW) return;
		if(offY >= pH) return;

		int newX = 0;
		int newY = 0;
		int newWidth = image.getW();
		int newHeight = image.getH();

		if(offX < 0) {newX -= offX;}
		if(offY < 0) {newY -= offY;}
		if(offX + newWidth >= pW) {newWidth = pW - offX;}
		if(offY + newHeight >= pH) {newHeight = pH - offY;}

		for(int y = newY; y < newHeight; y++) {
			for(int x = newX; x < newWidth; x++) {
				int value = image.getP()[x + y * image.getW()];

				setPixel(offX + x, offY + y, image.getP()[x + y * image.getW()]);

				int alpha = (value >> 24) & 0xff;
				if(alpha != 0)
				setLightBlock(offX + x, offY + y, image.getLightBlock());
			}
		}
	}

	public void drawImageTile(ImageTile image, int offX, int offY, int tileX, int tileY) {
		offX -= camX;
		offY -= camY;

		if(offX < -image.getTileW()) return;
		if(offY < -image.getTileH()) return;
		if(offX >= pW) return;
		if(offY >= pH) return;

		int newX = 0;
		int newY = 0;
		int newWidth = image.getTileW();
		int newHeight = image.getTileH();

		if(offX < 0) {newX -= offX;}
		if(offY < 0) {newY -= offY;}
		if(offX + newWidth >= pW) {newWidth = pW - offX;}
		if(offY + newHeight >= pH) {newHeight = pH - offY;}

		for(int y = newY; y < newHeight; y++) {
			for(int x = newX; x < newWidth; x++) {

				int index = (x + tileX * image.getTileW()) + (y + tileY * image.getTileH()) * image.getW();
				if(index < image.getP().length) {
					int value = image.getP()[index];

					setPixel(offX + x, offY + y, value);

					int alpha = (value >> 24) & 0xff;
					if(alpha != 0) setLightBlock(offX + x, offY + y, image.getLightBlock());
				}
			}
		}
	}

	public void drawRect(int offX, int offY, int width, int height, int color) {
		offX -= camX;
		offY -= camY;

		if(offX < -width) return;
		if(offY < -height) return;
		if(offX >= pW) return;
		if(offY >= pH) return;

		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;

		if(offX < 0) {newX -= offX;}
		if(offY < 0) {newY -= offY;}
		if(offX + newWidth >= pW) {newWidth = pW - offX;}
		if(offY + newHeight >= pH) {newHeight = pH - offY;}

		for(int y = newY; y <= newHeight; y++) {
			setPixel(offX, offY + y, color);
			setPixel(offX + width, offY + y, color);
		}

		for(int x = newX; x <= newWidth; x++) {
			setPixel(offX + x, offY, color);
			setPixel(offX + x, offY + height, color);
		}
	}

	public void drawFillRect(int offX, int offY, int width, int height, int color) {
		offX -= camX;
		offY -= camY;

		if(offX < -width) return;
		if(offY < -height) return;
		if(offX >= pW) return;
		if(offY >= pH) return;

		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;

		if(offX < 0) {newX -= offX;}
		if(offY < 0) {newY -= offY;}
		if(offX + newWidth >= pW) {newWidth = pW - offX;}
		if(offY + newHeight >= pH) {newHeight = pH - offY;}

		for(int y = newY; y < newHeight; y++) {
			for(int x = newX; x < newWidth; x++) {
				setPixel(offX + x, offY + y, color);

				int alpha = (color >> 24) & 0xff;
				if(alpha != 0) setLightBlock(offX + x, offY + y, Light.GLOW);
			}
		}
	}

	public void drawLight(Light l, int offX, int offY) {
		lightRequest.add(new LightRequest(l, offX, offY));
	}

	private void drawLightLines(Light l, int offX, int offY) {
		offX -= camX;
		offY -= camY;

		for(int i = 0; i <= l.getDiameter(); i++) {
			drawLightLine(l, l.getRadius(), l.getRadius(), i, 0, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), i, l.getDiameter(), offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), 0, i, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, offX, offY);
		}
	}

	private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offX, int offY) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int err2;

		//boolean block = false;
		while(true) {
			int screenX = x0 - l.getRadius() + offX;
			int screenY = y0 - l.getRadius() + offY;

			if(screenX < 0 || screenX >= pW || screenY < 0 || screenY >= pH) return;

			int lightColor = l.getLightValue(x0, y0);
			if(lightColor == 0) return;

			int lightBlock = lb[screenX + screenY * pW];

			//if (lightBlock == Light.FULL) block = true;

			//if(block == false || lightBlock == Light.GLOW)
			//	setLightMap(screenX, screenY, lightColor);

			if (lightBlock == Light.FULL) return;
			setLightMap(screenX, screenY, lightColor);

			if(x0 == x1 && y0 == y1) break;

			err2 = 2 * err;

			if(err2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}

			if(err2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}

	public int getAmbientColor() {
		return ambientColor;
	}

	public int getCamX() {
		return camX;
	}

	public int getCamY() {
		return camY;
	}

	public void setAmbientColor(int value) {
		ambientColor = value;
	}

	public void fadeAmbient(int target, float dt) {
		fadeAccumulator += 10 * dt;
		if(fadeAccumulator >= 1) {
			if(ambientColor < target) ambientColor += 0x00111111;
			else if(ambientColor > target) ambientColor -= 0x00111111;
			fadeAccumulator -= 1;
		}
	}

	public void setCamX(int value) {
		camX = value;
	}

	public void setCamY(int value) {
		camY = value;
	}
}