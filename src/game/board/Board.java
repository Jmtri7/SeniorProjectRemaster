// by: James Trinity
package game.board;

import java.util.ArrayList;
import java.util.List;

import engine.GameContainer;
import engine.Renderer;
import engine.gfx.Image;
import engine.gfx.ImageTile;
import engine.audio.SoundClip;

import game.Camera;
import game.AudioLoader;
import game.controllers.AIController;
import game.controllers.PlayerController;
import game.entities.Creature;
import game.entities.Humanoid;
import game.entities.Item;
import game.entities.Consumable;
import game.entities.Equipment;
import game.entities.Grass;
import game.entities.Plant;
import game.entities.Wall;

public class Board {
	public Camera camera = new Camera(null);

	private Tile[] tiles;
	private int width, height;
	private int tileSize;

	private PlayerController pc = new PlayerController(null);
	private ArrayList<AIController> creatures = new ArrayList<AIController>();

	private ArrayList<Zone> zones = new ArrayList<Zone>();
	private Zone currentZone;
	private SoundClip defaultMusic;
	private SoundClip music;	
	private int defaultColor;
	private int ambientColor;

	private int time = 0xff888888;
	private double timer = 0;
	private boolean timeUp = true;

	// changed to switch maps
	private String switchTo = null;
	private int spawnX = -1;
	private int spawnY = -1;

	public Board(Image tileMap, Image structureMap, int tileSize) {
		this.width = tileMap.getW();
		this.height = tileMap.getH();
		this.tileSize = tileSize;
		this.tiles = new Tile[width * height];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				tiles[i * width + j] = new Tile(j, i, this);
				int tileMapData = tileMap.getP()[i * tileMap.getW() + j];
				switch(tileMapData) {
					case 0xffff0000: this.getTile(j, i).setTerrain("lava");
					break;
					case 0xff00ff00:
						this.getTile(j, i).setTerrain("grass");
						//addGrass(j, i, "Grass");
					break;
					case 0xff00ee00:
						this.getTile(j, i).setTerrain("fungi");
					break;
					case 0xff0000ff: this.getTile(j, i).setTerrain("water");
						addGrass(j, i, "animatedWater");
					break;
					case 0xffee0000: this.getTile(j, i).setTerrain("lava");
						addGrass(j, i, "animatedSludge");
					break;
					case 0xff888888: this.getTile(j, i).setTerrain("stone");
					break;
					case 0xff907060: this.getTile(j, i).setTerrain("plank");
					break;
					case 0xff806050: this.getTile(j, i).setTerrain("dirt");
					break;
					case 0xffffeeaa: this.getTile(j, i).setTerrain("sand");
					break;
				}
				
				int mapData = structureMap.getP()[i * structureMap.getW() + j];
				if(mapData == 0xff000000) this.addWall(j, i, "none");
				if(mapData == 0xffee5522) this.addCampfire(j, i);
				if(mapData == 0xff0000ff) {
					if(this.getTile(j, i).getTerrain().getType().equals("fungi")) {
						this.addDecor(j, i, "greatMushroom");
					} else {
						this.addDecor(j, i, "greatTree");
					}
				}
				if(mapData == 0xff888888) this.addWall(j, i, "stoneWall");
				if(mapData == 0xff907060) this.addWall(j, i, "fence");
				if(mapData == 0xff806050) this.addWall(j, i, "smallTable");
				if(mapData == 0xffff0000) this.addWall(j, i, "grave");
				if(mapData == 0xff009900) this.addShrub(j, i);
				if(mapData == 0xff777777) this.addBoulder(j, i);
				if(mapData == 0xff00ff00) this.addTree(j, i);
			}
		}
	}

	// START SPAWN FUNCTIONS

	// player, wanderer
	public Creature spawn(int x, int y, String creatureType, String aiType) {
		Tile tile = this.getTile(x, y);

		Creature spawn = null;
		AIController ai;

		spawn = spawn(tile, creatureType);

		// spawn is player controlled
		if(aiType.equals("player")) {
			this.setPlayer(spawn);
			camera.setTarget(spawn);
		} else if(aiType.equals("wander")) {
			ai = new AIController(spawn, tile);
			ai.setWander(true);
			creatures.add(ai);
		}

		return spawn;
	}

	// follow a target creature
	public Creature spawn(int x, int y, String creatureType, Creature leader) {
		Tile tile = this.getTile(x, y);

		Creature spawn = null;
		AIController ai;

		spawn = spawn(tile, creatureType);

		ai = new AIController(spawn, tile);
		ai.setFollow(true);
		ai.setLeader(leader);
		creatures.add(ai);

		return spawn;
	}

	// patrol a route
	public Creature spawn(int x, int y, String creatureType, List<Tile> route) {
		Tile tile = this.getTile(x, y);

		Creature spawn = null;
		AIController ai;

		spawn = spawn(tile, creatureType);

		ai = new AIController(spawn, tile);
		ai.setRoute(new ArrayList<>(route));
		creatures.add(ai);

		return spawn;
	}

	// creates the creature
	public Creature spawn(Tile tile, String type) {
		Creature spawn = null;

		if(type.equals("player")) {
			spawn  = new Humanoid(tile, "skeleton");
			spawn.setFaction("knights");
			spawn.setWalkSpeed(0.3f);
			this.equipItem(spawn, "bow");
			this.equipItem(spawn, "breastPlate");
		}
		else if(type.equals("king")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("knights");
			this.equipItem(spawn, "royalCrown");
			this.equipItem(spawn, "royalRobe");
			this.equipItem(spawn, "ironPlatelegs");
			this.equipItem(spawn, "leatherShoes");
		}
		else if(type.equals("knight")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("knights");
			this.equipItem(spawn, "knightHelm");
			this.equipItem(spawn, "breastPlate");
			this.equipItem(spawn, "ironPlatelegs");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "shortsword");
		}
		else if(type.equals("fool")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setInvincible(true);
			// this.equipItem(spawn, "fools hat");
			this.equipItem(spawn, "peasantGownBrown");
			this.equipItem(spawn, "leatherShoes");
		}
		else if(type.equals("man")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("hunters");
			this.equipItem(spawn, "whiteShirt");
			this.equipItem(spawn, "redPants");
			this.equipItem(spawn, "leatherShoes");

			spawn.setTag("man");
		}
		else if(type.equals("peasant")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("hunters");
			this.equipItem(spawn, "leatherHat");
			this.equipItem(spawn, "peasantGownBrown");
			this.equipItem(spawn, "leatherShoes");

			spawn.setTag("peasant");
		}
		else if(type.equals("woodcutter")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("woodcutters");
			this.equipItem(spawn, "leatherHat");
			this.equipItem(spawn, "peasantGownGreen");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "axe");

			spawn.setTag("woodcutter");
		}
		else if(type.equals("miner")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("miners");
			this.equipItem(spawn, "leatherHat");
			this.equipItem(spawn, "peasantGownGray");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "pickaxe");

			spawn.setTag("miner");
		}
		else if(type.equals("hunter")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("hunters");
			this.equipItem(spawn, "leatherHat");
			this.equipItem(spawn, "peasantGownBrown");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "bow");

			spawn.setTag("woodcutter");
		}
		else if(type.equals("sailor")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("hunters");
			this.equipItem(spawn, "sailorHat");
			this.equipItem(spawn, "sailorShirt");
			this.equipItem(spawn, "bluePants");

			spawn.setTag("sailor");
		}
		else if(type.equals("pirate")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("wild");
			this.equipItem(spawn, "pirateHat");
			this.equipItem(spawn, "pirateShirt");
			this.equipItem(spawn, "redPants");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "shortsword");

			spawn.setTag("pirate");
		}
		else if(type.equals("pirateCaptain")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("wild");
			this.equipItem(spawn, "captainsHat");
			this.equipItem(spawn, "pirateShirt");
			this.equipItem(spawn, "redPants");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "shortsword");

			spawn.setTag("pirate");
		}
		else if(type.equals("nomad")) {
			spawn  = new Humanoid(tile, "human");
			spawn.setFaction("hunters");
			this.equipItem(spawn, "turban");
			this.equipItem(spawn, "redPants");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "morningstar");

			spawn.setTag("nomad");
		}
		else if(type.equals("death clan warrior")) {
			spawn  = new Humanoid(tile, "orc");
			spawn.setFaction("wild");
			this.equipItem(spawn, "orcHat");
			this.equipItem(spawn, "orcGown");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "morningstar");

			spawn.setTag("orc");
		}
		else if(type.equals("skeleton")) {
			spawn  = new Humanoid(tile, "skeleton");
			spawn.setFaction("wild");
			this.equipItem(spawn, "leatherHat");
			this.equipItem(spawn, "leatherShoes");
			this.equipItem(spawn, "morningstar");

			spawn.setTag("skeleton");
		}
		else if(type.equals("maid")) {
			spawn  = new Humanoid(tile, "human");
			this.equipItem(spawn, "maidHair");
			this.equipItem(spawn, "maidGown");
			this.equipItem(spawn, "leatherShoes");

			spawn.setTag("maid");
		}
		else if(type.equals("rabbit")) {
			spawn  = new Creature(tile, "rabbit");
			spawn.setFaction("rabbits");
		}
		else if(type.equals("wisp")) {
			spawn  = new Creature(tile, "wisp");
			spawn.setFloating(true);
			spawn.setBlocking(false);
			spawn.setInvincible(true);

			spawn.setTag("wisp");
		}
		else {
			spawn  = new Creature(tile, type);
			spawn.setFaction("wild");
		}

		return spawn;
	}

	// give item from prototype

	public void equipItem(Creature character, String itemName) {
		Equipment item = new Equipment(character.getTile(), itemName);
		character.takeItem();
		character.equip(item);
	}

	public void giveEquipment(Creature character, String itemName) {
		Equipment item = new Equipment(character.getTile(), itemName);
		character.takeItem();
	}

	// END SPAWN FUNCTIONS

	public void addZone(int x, int y, int width, int height, int ambientColor, SoundClip music) {
		zones.add(new Zone(x, y, width, height, ambientColor, music));
	}

	public void addCampfire(int x, int y) {
		Tile tile = this.getTile(x, y);
		Grass grass = new Grass(tile, "campfire", 0xffffffff, 150);
		grass.setInvincible(true);
		grass.setBlocking(true);
	}

	public void addDecor(int x, int y, String type) {
		Tile tile = this.getTile(x, y);
		Wall wall = new Wall(tile, type);
		wall.setInvincible(true);
		wall.setBlocking(false);
		wall.setShifting(true);
	}

	public void addGrass(int x, int y, String type) {
		Tile tile = this.getTile(x, y);
		Grass grass = new Grass(tile, type);
		grass.setInvincible(true);
	}

	public void addPlant(int x, int y) {
		Tile tile = this.getTile(x, y);
		Plant plant = new Plant(tile);
		plant.setFaction("plants");
	}

	public void addTree(int x, int y) {
		Tile tile = this.getTile(x, y);
		Wall wall = null;
		if(tile.getTerrain().getType().equals("sand")) {
			wall = new Wall(tile, "palm");
		} else if(tile.getTerrain().getType().equals("fungi")) {
			if(Math.random() > 0.5) {
				wall = new Wall(tile, "redGiantMushroom");
			} else {
				wall = new Wall(tile, "blueGiantMushroom");
			}
		} else {
			wall = new Wall(tile, "tree");
		}
		wall.setInvincible(false);
		wall.setFaction("trees");
		wall.setDamageNoise("chopping");
		wall.setDeathNoise("treeDeath");
		//wall.setRespawns(true);
		//wall.setRespawnTime(10f);
		wall.setWeakness("chopping");
	}

	public void addShrub(int x, int y) {
		Tile tile = this.getTile(x, y);
		Wall wall = null;
		if(tile.getTerrain().getType().equals("sand")) {
			wall = new Wall(tile, "cactus");
		} else {
			wall = new Wall(tile, "bush");
		}
	}

	public void addBoulder(int x, int y) {
		Tile tile = this.getTile(x, y);
		Wall wall = new Wall(tile, "smallBoulder");
		wall.setInvincible(false);
		wall.setFaction("boulders");
		wall.setDamageNoise("mining");
		wall.setDeathNoise("mining");
		//wall.setRespawns(true);
		//wall.setRespawnTime(10f);
		wall.setWeakness("picking");
	}

	public void addWall(int x, int y, String type) {
		Tile tile = this.getTile(x, y);
		Wall wall = new Wall(tile, type);
		wall.setInvincible(true);
	}

	public Item placeEquipment(int x, int y, String name) {
		Tile tile = this.getTile(x, y);
		Equipment equipment = new Equipment(tile, name);
		return equipment;
	}

	public Item placeItem(int x, int y, ImageTile image, String type, String tag) {
		Tile tile = this.getTile(x, y);

		Item item;
		if(type.equals("consumable")) {
			item = new Consumable(tile, image, tag);
		} else {
			item = new Item(tile, image, type);
		}
		
		return item;
	}

	public void update(GameContainer gc, float dt) {

		// CLOCK
		// ======================================
		timer += dt / 10;
		// System.out.println(timer + " / " + 1);
		if(timer > 1) {

			// day / night
			if(time >= 0xffffffff || time <= 0xff555555) {
				timeUp = !timeUp;
			}

			timer -= 1;
			if(timeUp) time += 0x00111111;
			else time -= 0x00111111;

			defaultColor = time;
		}

		// set outdoor lighting	
		if(currentZone == null) ambientColor = defaultColor;
		// =======================================
		
		Creature player = pc.getPlayer();

		// if player is not in current zone or any zone
		if(
			(
				currentZone != null
				&& !currentZone.contains(player.getTile().getX(), player.getTile().getY())
			)
			|| currentZone == null
		) {
			
			// leaving zone
			if(currentZone != null) {
				currentZone = null;
				ambientColor = defaultColor;
				if(music != null) music.stop();
				music = defaultMusic;
			}

			// check all zones
			for(int i = 0; i < zones.size(); i++) {
				if(zones.get(i).contains(player.getTile().getX(), player.getTile().getY())) {

					// switch zones
					currentZone = zones.get(i);

					// switch music
					if(music != null) music.stop();
					music = zones.get(i).getMusic();

					// switch color
					ambientColor = zones.get(i).getAmbientColor();
				}
			}
		}
		if(music != null && !music.isRunning()) music.loop();

		gc.getRenderer().fadeAmbient(ambientColor, dt);

		// UPDATE ENTITIES

		// update with radius

		// int updateRadius = 14;
		// Tile playerTile;
		// playerTile = pc.getPlayer().getTile();
		// for(int i = -updateRadius; i < updateRadius; i++) {
		// 	for(int j = -updateRadius; j < updateRadius; j++) {
		// 		int tileLocation = this.width * (playerTile.getY() + j) + (playerTile.getX() + i);
		// 		if(
		// 			tileLocation >= 0 
		// 			&& tileLocation < tiles.length
		// 		) tiles[tileLocation].update(gc, dt);
		// 	}
		// }

		// update without radius
		for(int i = 0; i < tiles.length; i++) tiles[i].update(gc, dt);

		if(pc.getPlayer() != null) pc.update(gc, dt);
		for(int i = 0; i < creatures.size(); i++) creatures.get(i).update(gc, dt);
	}

	public void render(GameContainer gc, Renderer r) {
		// BIG PERFORMANCE BOOST WITH RADIUS

		int renderRadius = (int) (gc.getWidth() / 2 / 20) + 1;
		Tile playerTile;
		playerTile = pc.getPlayer().getTile();
		for(int i = -renderRadius; i < renderRadius; i++) {
			for(int j = -renderRadius; j < renderRadius; j++) {
				int tileLocation = this.width * (playerTile.getY() + j) + (playerTile.getX() + i);
				if(
					tileLocation >= 0 
					&& tileLocation < tiles.length
				) tiles[tileLocation].render(gc, r);
			}
		}

		//entities drawn row first
		// for(int i = -renderRadius; i < renderRadius; i++) {
		// 	for(int j = -renderRadius; j < renderRadius; j++) {
		// 		int tileLocation = this.width * (playerTile.getY() + i) + (playerTile.getX() + j);
		// 		if(
		// 			tileLocation >= 0 
		// 			&& tileLocation < tiles.length
		// 		)
		// 		for(int k = 0; k < tiles[tileLocation].getEntities().size(); k++)
		// 			tiles[tileLocation].getEntities().get(k).render(gc, r);
		// 	}
		// }

		// TEMPORARY? FIX TO MAKE SURE GRASS IS ALWAYS DRAWN BELOW CREATURES
		// SCAN THE ROW TWICE

		// grass drawn first
		for(int i = -renderRadius; i < renderRadius; i++) {

			// draw grass
			for(int j = -renderRadius; j < renderRadius; j++) {
				int tileLocation = this.width * (playerTile.getY() + i) + (playerTile.getX() + j);
				if(
					tileLocation >= 0 
					&& tileLocation < tiles.length
				) {
					for(int k = 0; k < tiles[tileLocation].getEntities().size(); k++)
						if(tiles[tileLocation].getEntities().get(k).getTag().equals("grass"))
							tiles[tileLocation].getEntities().get(k).render(gc, r);
				}
			}

			// draw others
			for(int j = -renderRadius; j < renderRadius; j++) {
				int tileLocation = this.width * (playerTile.getY() + i) + (playerTile.getX() + j);
				if(
					tileLocation >= 0 
					&& tileLocation < tiles.length
				) {
					// items
					for(int k = 0; k < tiles[tileLocation].getItems().size(); k++)
						tiles[tileLocation].getItems().get(k).render(gc, r);

					// obstacles, creatures
					for(int k = 0; k < tiles[tileLocation].getEntities().size(); k++)
						if(!tiles[tileLocation].getEntities().get(k).getTag().equals("grass"))
							tiles[tileLocation].getEntities().get(k).render(gc, r);
				}
			}
		}

		// render player inventory
		r.setCamX(0);
		r.setCamY(0);
		int frameWidth = 5;
		int inventoryHeight = 100;
		r.drawFillRect(0, gc.getHeight() - inventoryHeight, gc.getWidth(), inventoryHeight, 0xffff0000);
		r.drawFillRect(frameWidth, gc.getHeight() - inventoryHeight + frameWidth, gc.getWidth() - 2 * frameWidth, inventoryHeight - 2 * frameWidth, 0xff000000);

		ArrayList<Item> items = pc.getPlayer().getInventory().getItems();
		for(int i = 0; i < items.size(); i++) {
			int itemX = i * tileSize + frameWidth;
			int itemY = gc.getHeight() - inventoryHeight + frameWidth;

			if(i == pc.getPlayer().getInventory().getSelector()) {
				r.drawFillRect(itemX, itemY, tileSize, tileSize, 0xffffff00);
			}

			// draw equipment
			if(items.get(i).getType().equals("equipment")) {
				ImageTile graphic = items.get(i).getGraphic();
				r.drawImageTile(graphic, itemX - graphic.getTileW() / 2 + tileSize / 2, itemY - graphic.getTileH() + tileSize, 0, 4);
				if(items.get(i).isEquipped()) {
					r.drawRect(itemX, itemY, tileSize, tileSize, 0xff00ff00);
				}

			// draw other items
			} else {
				r.drawImageTile(items.get(i).getGraphic(), itemX, itemY, 0, 0);
			}
		}

		// reset camera for lights
		float cameraX = pc.getPlayer().getX() - gc.getWidth() / 2;
		float cameraY = pc.getPlayer().getY() - (gc.getHeight() - inventoryHeight) / 2;
		r.setCamX((int) cameraX);
		r.setCamY((int) cameraY);

	}

	public int getTileSize() {
		return tileSize;
	}

	public Tile getTile(int x, int y) {
		return tiles[y * width + x];
	}

	public Creature getPlayer() {
		return pc.getPlayer();
	}

	public void setPlayer(Creature player) {
		pc.setPlayer(player);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setDefaultAmbientColor(int value) {
		defaultColor = value;
		ambientColor = defaultColor;
	}

	public void setDefaultMusic(String name) {
		if (name.equals("null")) defaultMusic = null;
		else defaultMusic = AudioLoader.safeLoad(name, "/res/music/" + name + ".wav");
		music = defaultMusic;
	}

	public int getAmbientColor() {
		return ambientColor;
	}

	public int getTime() {
		return time;
	}

	public boolean getTimeUp() {
		return timeUp;
	}

	public void setTime(int value) {
		time = value;
		defaultColor = time;
	}

	public void setTimeUp(boolean value) {
		timeUp = value;
	}

	public String switchTo() {
		return switchTo;
	}

	public void setSwitch(String mapName) {
		switchTo = mapName;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	public void stopMusic() {
		if(music != null && music.isRunning()) {
			music.stop();
		}
	}

	public Camera getCamera() {
		return camera;
	}
}