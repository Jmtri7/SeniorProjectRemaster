package game.entities;

import engine.GameContainer;
import engine.Renderer;

import game.board.Tile;

public class Portal extends Entity {
	private String destination;
	private int outX;
	private int outY;

	public Portal(Tile tile, String destination, int outX, int outY) {
		super(tile);

		this.destination = destination;

		this.outX = outX;
		this.outY = outY;
	}

	public void trigger() {
		tile.getBoard().setSwitch(this.destination);
		tile.getBoard().setSpawnX(outX);
		tile.getBoard().setSpawnY(outY);
	}

	public void update(GameContainer gc, float dt) {
		if(tile.getBoard().getPlayer().getTile().getId() == tile.getId())
			trigger();
	}

	public void render(GameContainer gc, Renderer r) {
		int tileSize = tile.getBoard().getTileSize();
		int tileX = tile.getX() * tileSize;
		int tileY = tile.getY() * tileSize;
		r.drawFillRect(tileX, tileY, tileSize, tileSize, 0xffff0000);
	}
}