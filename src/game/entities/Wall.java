// by: James Trinity
package game.entities;

import java.util.HashMap;
import java.util.Map;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.ImageTile;
import engine.gfx.Light;

import game.board.Tile;

public class Wall extends Entity {
	public static HashMap<String, ImageTile> imageMap = new HashMap<String, ImageTile>();
	private String type = null;

	private boolean shifting = false;

	public Wall(Tile tile, String type) {
		super(tile);

		this.type = type;
		this.blocking = true;

		this.tag = "obstacle";
	}

	public void render(GameContainer gc, Renderer r) {
		if(tile != null && type != null && !type.equals("none")) {

			int tileSize = tile.getBoard().getTileSize();

			float screenX = tile.getX() * tileSize;
			float screenY = tile.getY() * tileSize;
			//r.drawRect((int) screenX, (int) screenY, tileSize, tileSize, 0xffffff00);

			float spriteX = x - imageMap.get(type).getTileW() / 2 + tileSize / 2;
			float spriteY = y - imageMap.get(type).getTileH() + tileSize;

			if(!isDead) {
					if(shifting == true) {
						if(true || tile.getBoard().getAmbientColor() <= 0xff555555) {
							r.drawImageTile(imageMap.get(type), (int) spriteX, (int) spriteY, 0, 0);
							blocking = false;
						}
						else if(tile.getBoard().getAmbientColor() > 0xff555555) {
							r.drawImageTile(imageMap.get(type), (int) spriteX, (int) spriteY, 1, 0);
							blocking = true;
						}
					}
					else r.drawImageTile(imageMap.get(type), (int) spriteX, (int) spriteY, 0, 0);

				if(hp < hpMax) {
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize, 2, 0xffff0000);
					r.drawFillRect((int) x, (int) spriteY - 2, tileSize * hp / hpMax, 2, 0xff00ff00);
				}
			} else {
				if(type != null && imageMap.get(type) != null) {
					r.drawImageTile(imageMap.get(type), (int) spriteX, (int) spriteY, 0, 1);
				}
			}

			//r.drawRect((int) spriteX, (int) spriteY, image.getTileW(), image.getTileH(), 0xffff0000);
		}
	}

	public void damage(int damage, String damageType) {
		if(!invincible && !isDead) {
			if(damageType.equals(weakness)) {
				this.hp -= damage;
			}
			if(this.hp <= 0) die();
			else if(this.damageNoise != null && damage > 0) this.damageNoise.play();
		}
	}

	public void setShifting(boolean value) {
		shifting = value;
	}

	public String getWallType() {
		return type;
	}

	public void setWallType(String type) {
		this.type = type;
	}

	public static void createWallType(String name, ImageTile image) {
		ImageTile wallImage = image;		
		wallImage.setLightBlock(Light.FULL);
		imageMap.put(name, wallImage);
	}
}