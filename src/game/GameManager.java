package game;

import java.awt.event.KeyEvent;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Renderer;
import engine.Input;

import game.board.Board;

public class GameManager extends AbstractGame {
	private Board gameBoard = null;

	private int spawnX;
	private int spawnY;

	private boolean paused = false;
	private boolean loading = false;

	public GameManager() {
		BoardBuilder.LoadAssets();
		gameBoard = BoardBuilder.BuildBoard("castle");
	}

	@Override
	public void init(GameContainer gc) {
		//gc.getRenderer().setAmbientColor(0xff777777);
		//gc.getRenderer().setAmbientColor(-1);
	}

	@Override
	public void update(GameContainer gc, float dt) {
		if(gc.getInput().isKeyUp(KeyEvent.VK_ESCAPE)) {
			paused = !paused;
		}

		if(paused == false) {
			gameBoard.update(gc, dt);

			if(loading == true) {
				gameBoard.stopMusic();

				if(!gameBoard.getPlayer().isDead()) {
					BoardBuilder.SavePlayer(gameBoard.getPlayer());
					spawnX = gameBoard.getSpawnX();
					spawnY = gameBoard.getSpawnY();
					gameBoard = BoardBuilder.BuildBoard(gameBoard.switchTo(), spawnX, spawnY);
				} else {
					gameBoard = BoardBuilder.BuildBoard("castle");
				}
				
				gameBoard.setSwitch(null);
				loading = false;
			}
		}
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		gameBoard.camera.render(gc, r);
		gameBoard.render(gc, r);

		if(paused) {
			r.setCamX(0);
			r.setCamY(0);
			r.drawText("PAUSED", gc.getWidth() / 2 - 3, (gc.getHeight() - 100) / 2, 0xff00ffff);
			gameBoard.camera.render(gc, r);
		}

		// trigger on gameBoard requests switch to another board
		if(gameBoard.switchTo() != null || gameBoard.getPlayer().isDead()) {
			r.setCamX(0);
			r.setCamY(0);
			r.drawFillRect(0, 0, gc.getWidth(), gc.getHeight(), 0xff000000);
			r.drawText("LOADING", gc.getWidth() / 2 - 3, (gc.getHeight() - 100) / 2, 0xff00ffff);

			loading = true;
		}
	}
}