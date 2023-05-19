package game;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

import engine.gfx.Image;
import engine.gfx.ImageTile;
import engine.gfx.Light;
import engine.audio.SoundClip;

import game.board.Board;
import game.board.Route;
import game.board.Terrain;
import game.board.Tile;
import game.entities.Creature;
import game.entities.Humanoid;
import game.entities.Projectile;
import game.entities.Item;
import game.entities.Equipment;
import game.entities.Species;
import game.entities.Faction;
import game.entities.Wall;
import game.entities.Portal;

public class BoardBuilder {
	static void SavePlayer(Creature pc) {
		try {
			FileWriter saveWriter = new FileWriter("/src/res/save/player.txt");
			String saveData = "";

			// board data

			int time = pc.getTile().getBoard().getTime();
			boolean timeUp = pc.getTile().getBoard().getTimeUp();

			saveData +=
				time + "\n"
				+ timeUp + "\n"
				+ "\n";

			// player data

			String species = pc.getSpecies().getType();
			String faction = pc.getFaction().getName();
			float speed =  pc.getWalkSpeed();

			saveData +=
				species + "\n"
				+ faction + "\n"
				+ speed + "\n"
				+ "\n";

			// equipment

			ArrayList<Item> items = pc.getInventory().getItems();
			for(int i = 0; i < items.size(); i++) {
				saveData +=
					items.get(i).getTag() + "\n"
					+ items.get(i).getType() + "\n"
					+ items.get(i).isEquipped() + "\n"
					+ "\n";
			}

			saveData += "END";

      		saveWriter.write(saveData);
      		saveWriter.close();
		} catch (IOException e) {
			System.out.println("Failed to write save.");
			e.printStackTrace();
		}
	}

	static Creature LoadPlayer(Board gameBoard, int spawnX, int spawnY) {
		Creature pc;

		try {
			File assetData = new File("/src/res/save/player.txt");
			Scanner scanner = new Scanner(assetData);
			String line = "";
			
			int time = Integer.parseInt(scanner.nextLine());
			gameBoard.setTime(time);

			boolean timeUp = Boolean.parseBoolean(scanner.nextLine());
			gameBoard.setTimeUp(timeUp);

			scanner.nextLine();

			// get data

			String species = scanner.nextLine();

			String faction = scanner.nextLine();

			line = scanner.nextLine();
			float speed = Float.parseFloat(line);

			pc  = new Humanoid(gameBoard.getTile(spawnX, spawnY), species);
			pc.setFaction(faction);
			pc.setWalkSpeed(speed);

			pc.setTag("player");
			
			scanner.nextLine();
			line = scanner.nextLine();
			while(!line.equals("END")) {
				String itemName = line;

				String type = scanner.nextLine();

				line = scanner.nextLine();
				Boolean isEquipped = Boolean.parseBoolean(line);

				if(isEquipped) {
					gameBoard.equipItem(pc, itemName);
				} else if(type.equals("equipment")) {
					gameBoard.giveEquipment(pc, itemName);
				}

				// handle loading of other types

				scanner.nextLine();
				line = scanner.nextLine();
			}

			gameBoard.setPlayer(pc);
			gameBoard.getCamera().setTarget(pc);

			scanner.close();
		} catch(FileNotFoundException e) {
			System.out.println("Map data not found.");
			e.printStackTrace();
		}

		return null;
	}

	static void LoadAssets() {
		ImageLoader.loadImage("campfire", "/res/structure/campfire.png", 40, 40);

		ImageLoader.loadImage("animatedGrass", "/res/tile/animatedGrass.png", 20, 40);
		ImageLoader.loadImage("animatedWater", "/res/tile/animatedWater.png", 20, 40);
		ImageLoader.loadImage("animatedSludge", "/res/tile/animatedSludge.png", 20, 40);

		AudioLoader.loadAudio("chopping", "/res/sounds/chopping.wav");
		AudioLoader.loadAudio("treeDeath", "/res/sounds/treeDeath.wav");

		AudioLoader.loadAudio("mining", "/res/sounds/mining.wav");

		try {
			File assetData = new File("src/res/world/assetData.txt");
			Scanner scanner = new Scanner(assetData);
			String line = "";

			line = scanner.nextLine();
			if(line.equals("MUSIC")) {
				scanner.nextLine();

				String name;

				line = scanner.nextLine();
				while(!line.equals("STRUCTURES")) {

					name = line;

					AudioLoader.loadAudio(name, "src/res/music/" + name + ".wav");

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("STRUCTURES")) {
				scanner.nextLine();

				String name;
				int tileX;
				int tileY;

				line = scanner.nextLine();
				while(!line.equals("TILESET")) {

					name = line;

					line = scanner.nextLine();
					tileX = Integer.parseInt(line);

					line = scanner.nextLine();
					tileY = Integer.parseInt(line);

					Wall.createWallType(
						name,
						new ImageTile("/res/structure/" + name + ".png", tileX, tileY)
					);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("TILESET")) {
				scanner.nextLine();

				// get the first tile type
				line = scanner.nextLine();
				while(!line.equals("SPECIES")) {
					String name = line;

					line = scanner.nextLine();
					String lightColor = line;

					line = scanner.nextLine();
					boolean blocked = Boolean.parseBoolean(line);

					if(lightColor.equals("null"))
						new Terrain(name, new Image("/res/tile/" + name + ".png"), null, blocked);
					else
						new Terrain(name, new Image("/res/tile/" + name + ".png"), 0xff000000 | Integer.parseInt(lightColor, 16), blocked);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			// HARD-CODED DATA
			// INTEGRATE WITH BUILDER

			new Projectile(
				new ImageTile("/res/projectile/arrow.png", 20, 40),
				"arrow",
				19
			);

			new Projectile(
				new ImageTile("/res/projectile/firebolt.png", 20, 40),
				"firebolt",
				100
			);

			if(line.equals("SPECIES")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("FACTIONS")) {
					String name = line;

					line = scanner.nextLine();
					int tileX = Integer.parseInt(line);

					line = scanner.nextLine();
					int tileY = Integer.parseInt(line);

					line = scanner.nextLine();
					int hp = Integer.parseInt(line);

					line = scanner.nextLine();
					int dmg = Integer.parseInt(line);

					line = scanner.nextLine();
					int lightColor = 0xff000000 | Integer.parseInt(line, 16);

					line = scanner.nextLine();
					SoundClip injurySound;
					if(line.equals("null")) injurySound = null;
					else injurySound = AudioLoader.safeLoad(line, "/res/sounds/" + line + ".wav");

					line = scanner.nextLine();
					SoundClip deathSound;
					if(line.equals("null")) deathSound = null;
					else deathSound = AudioLoader.safeLoad(line, "/res/sounds/" + line + ".wav");

					line = scanner.nextLine();
					SoundClip attackSound;
					if(line.equals("null")) attackSound = null;
					else attackSound = AudioLoader.safeLoad(line, "/res/sounds/" + line + ".wav");

					line = scanner.nextLine();

					new Species(
						name,
						hp, dmg,
						new ImageTile("/res/creature/" + name + ".png", tileX, tileY),
						new Light((int) (tileY * 1.5), lightColor),
						injurySound,
						deathSound,
						attackSound,
						null
						);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("FACTIONS")) {
				scanner.nextLine();

				// get first faction name
				line = scanner.nextLine();
				while(!line.equals("ITEMS")) {
					String faction = line;

					ArrayList<String> enemies = new ArrayList<String>();

					line = scanner.nextLine();
					while(!line.equals("end")) {
						enemies.add(line);

						line = scanner.nextLine();
					}

					new Faction(faction, enemies);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("ITEMS")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("EQUIPMENT")) {
					// String name = line;

					// line = scanner.nextLine();
					// String equipmentType = line;

					// line = scanner.nextLine();
					// int dmgModifier = Integer.parseInt(line);

					// line = scanner.nextLine();
					// float armorModifier = Float.parseFloat(line);

					// line = scanner.nextLine();
					// String damageType = line;

					// Equipment.create(
					// 	new ImageTile("/res/items/" + name + ".png", 60, 60),
					// 	name,
					// 	equipmentType,
					// 	dmgModifier,
					// 	armorModifier,
					// 	damageType
					// );

					// scanner.nextLine();
					// line = scanner.nextLine();
				}
			}

			if(line.equals("EQUIPMENT")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("END")) {
					String name = line;

					line = scanner.nextLine();
					String equipmentType = line;

					line = scanner.nextLine();
					int dmgModifier = Integer.parseInt(line);

					line = scanner.nextLine();
					float armorModifier = Float.parseFloat(line);

					line = scanner.nextLine();
					String damageType = line;

					Equipment.create(
						new ImageTile("/res/items/" + name + ".png", 60, 60),
						name,
						equipmentType,
						dmgModifier,
						armorModifier,
						damageType
					);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			scanner.close();
		} catch(FileNotFoundException e) {
			System.out.println("Map data not found.");
			e.printStackTrace();
		}
	}

	static Board BuildBoard(String mapName) {
		return BuildBoard(mapName, -1, -1);
	}

	static Board BuildBoard(String mapName, int spawnX, int spawnY) {
		Board gameBoard = null;

		try {
			File mapData = new File("src/res/world/" + mapName + "/mapData.txt");
			Scanner scanner = new Scanner(mapData);
			String line = "";

			line = scanner.nextLine();
			if(line.equals("BOARD")) {
				scanner.nextLine();
				int tileSize = Integer.parseInt(scanner.nextLine());

				line = scanner.nextLine();
				int ambientColor = 0xff000000 | Integer.parseInt(line, 16);

				String music = scanner.nextLine();

				// board layout
				gameBoard = new Board(
					new Image("src/res/world/" + mapName + "/tileMap.png"),
					new Image("src/res/world/" + mapName + "/structureMap.png"),
					tileSize
					);

				gameBoard.setDefaultAmbientColor(ambientColor);
				gameBoard.setDefaultMusic(music);

				scanner.nextLine();
				line = scanner.nextLine();
			}

			if(line.equals("ZONES")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("ROUTES")) {
					int x = Integer.parseInt(line);

					line = scanner.nextLine();
					int y = Integer.parseInt(line);

					line = scanner.nextLine();
					int width = Integer.parseInt(line);

					line = scanner.nextLine();
					int height = Integer.parseInt(line);

					line = scanner.nextLine();
					int ambientColor = 0xff000000 | Integer.parseInt(line, 16);

					line = scanner.nextLine();
					SoundClip music;
					if(line.equals("null")) music = null;
					else music = AudioLoader.safeLoad(line, "src/res/music/" + line + ".wav");

					gameBoard.addZone(
						x, y,
						width, height,
						ambientColor,
						music
						);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("ROUTES")) {
				scanner.nextLine();
				line = scanner.nextLine();
				while(!line.equals("CRITTERS")) {
					String name = line;

					ArrayList<Tile> route = new ArrayList<Tile>();

					int x, y;
					line = scanner.nextLine();
					while(!line.equals("end")) {
						x = Integer.parseInt(line);
						line = scanner.nextLine();
						y = Integer.parseInt(line);

						route.add(gameBoard.getTile(x, y));

						line = scanner.nextLine();
					}

					new Route(name, route);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("CRITTERS")) {
				scanner.nextLine();
				
				line = scanner.nextLine();
				while(!line.equals("PLAYER")) {
					String species = line;

					line = scanner.nextLine();
					int maxPop = Integer.parseInt(line);

					line = scanner.nextLine();
					String terrain = line;

					line = scanner.nextLine();
					String type = line;

					int pop = 0;
					while(pop < maxPop) {
						int randomX = (int) (gameBoard.getWidth() * Math.random());
						int randomY = (int) (gameBoard.getWidth() * Math.random());

						if(
							(gameBoard.getTile(randomX, randomY).getTerrain().getType().equals(terrain)
							&& !gameBoard.getTile(randomX, randomY).isBlocked()) ||
								terrain.equals("any")
						) {
							if(type.equals("fauna")) {
								gameBoard.spawn(randomX, randomY, species, "wander");
							} else if(type.equals("flora")) {
								gameBoard.addPlant(randomX, randomY);
							}
							
							pop++;
						}	
					}

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("PLAYER")) {
				scanner.nextLine();

				line = scanner.nextLine();
				String species = line; 
					
				line = scanner.nextLine();
				if(spawnX == -1)
					spawnX = Integer.parseInt(line);

				line = scanner.nextLine();
				if(spawnY == -1)
					spawnY = Integer.parseInt(line);

				//System.out.println("teleport to " + spawnX + ", " + spawnY);
				
				// player
				//gameBoard.spawn(spawnX, spawnY, "player", "player");
				LoadPlayer(gameBoard, spawnX, spawnY);

				scanner.nextLine();
				line = scanner.nextLine();
			}

			if(line.equals("ITEMS")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("CREATURES")) {
					String name = line; 
					
					line = scanner.nextLine();
					int x = Integer.parseInt(line);

					line = scanner.nextLine();
					int y = Integer.parseInt(line);

					if(name.equals("fireScroll")) {
						gameBoard.placeItem(
							x,
							y,
							new ImageTile("../../res/items/fireScroll.png", 20, 20),
							"consumable",
							"scrollFireNova"
						);
					}

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("CREATURES")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("PORTALS")) {
					String type = line; 
					
					line = scanner.nextLine();
					int x = Integer.parseInt(line);

					line = scanner.nextLine();
					int y = Integer.parseInt(line);

					line = scanner.nextLine();
					String ai = line;

					if(ai.equals("wander")) {
						gameBoard.spawn(
							x, y,
							type,
							ai
						);
					} else if(ai.equals("patrol")) {
						gameBoard.spawn(
							x, y,
							type,
							Route.get(scanner.nextLine())
						);
					} else if(ai.equals("sentry")) {
						gameBoard.spawn(
							x, y,
							type,
							Arrays.asList(
								gameBoard.getTile(x, y)
							)
						);
					} else if(ai.equals("none")) {
						gameBoard.spawn(
							gameBoard.getTile(x, y),
							type
						);
					}

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			if(line.equals("PORTALS")) {
				scanner.nextLine();

				line = scanner.nextLine();
				while(!line.equals("END")) {
					String destination = line; 
					
					line = scanner.nextLine();
					int x = Integer.parseInt(line);

					line = scanner.nextLine();
					int y = Integer.parseInt(line);

					line = scanner.nextLine();
					int outX = Integer.parseInt(line);

					line = scanner.nextLine();
					int outY = Integer.parseInt(line);

					//System.out.println("out to " + outX + ", " + outY);

					new Portal(gameBoard.getTile(x, y), destination, outX, outY);

					scanner.nextLine();
					line = scanner.nextLine();
				}
			}

			// HARD-CODED DATA

			// gameBoard.spawn(
			// 	35, 90,
			// 	"death clan warrior", 
			// 	chief
			// );

			scanner.close();
		} catch(FileNotFoundException e) {
			System.out.println("Map data not found.");
			e.printStackTrace();
		}

		return gameBoard;
	}
}