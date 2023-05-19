// by: James Trinity
package game.controllers;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Renderer;

import game.entities.Creature;

import java.awt.event.KeyEvent;

public class PlayerController {
	private Creature player = null;

	public PlayerController(Creature player) {
		this.player = player;
	}

	public void update(GameContainer gc, float dt) {
		if(player.isDead() && gc.getInput().isKeyDown(KeyEvent.VK_SPACE)) {
			System.out.println("dead");
		}

		if(gc.getInput().isKey(KeyEvent.VK_SHIFT) || gc.getInput().isKey(KeyEvent.VK_SPACE)) {
			if(gc.getInput().isKey(KeyEvent.VK_W)) {
				player.turn(0);
			}

			if(gc.getInput().isKey(KeyEvent.VK_D)) {
				player.turn(1);
			}

			if(gc.getInput().isKey(KeyEvent.VK_S)) {
				player.turn(2);
			}

			if(gc.getInput().isKey(KeyEvent.VK_A)) {
				player.turn(3);
			}
		} else {
			if(gc.getInput().isKey(KeyEvent.VK_W)) {
				player.walk(0);
			}

			if(gc.getInput().isKey(KeyEvent.VK_D)) {
				player.walk(1);
			}

			if(gc.getInput().isKey(KeyEvent.VK_S)) {
				player.walk(2);
			}

			if(gc.getInput().isKey(KeyEvent.VK_A)) {
				player.walk(3);
			}
		}

		if(gc.getInput().isKey(KeyEvent.VK_SPACE)) {
			player.attack();
		}

		// inventory

		if(gc.getInput().isKeyUp(KeyEvent.VK_E)) {
			player.useItem();
		}

		if(gc.getInput().isKeyUp(KeyEvent.VK_T)) {
			player.takeItem();
		}

		if(gc.getInput().isKeyUp(KeyEvent.VK_R)) {
			player.dropItem();
		}

		if(gc.getInput().isKeyUp(KeyEvent.VK_OPEN_BRACKET)) {
			player.getInventory().selectPrevious();
		}

		if(gc.getInput().isKeyUp(KeyEvent.VK_CLOSE_BRACKET)) {
			player.getInventory().selectNext();
		}
	}

	public void render(GameContainer gc, Renderer r) {

	}

	public Creature getPlayer() {
		return player;
	}

	public void setPlayer(Creature player) {
		this.player = player;
	}
}