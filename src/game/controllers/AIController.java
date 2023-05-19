// by: James Trinity
package game.controllers;

import java.util.ArrayList;

import engine.AbstractGame;
import engine.GameContainer;
import engine.Renderer;
import game.entities.Entity;
import game.entities.Creature;
import game.board.Tile;

// waits at home until it finds an enemy
public class AIController {
	protected Creature character;
	protected ArrayList<Tile> visited;

	// PATH
	protected ArrayList<Tile> path;
	protected ArrayList<ArrayList<Tile>> pathList;

	// COMBAT CONSTAINTS

	protected Entity target;
	protected int aggroRange = 20;

	// TRAVELING CONSTRAINTS

	protected Tile home = null;

	// patrol
	private ArrayList<Tile> route;
	private int routeIndex;

	// wander
	protected boolean wander = false;
	protected int wanderDistance = 2;

	// follow
	protected Entity leader = null;
	protected boolean follow = false;
	protected int followDistance = 4;

	public AIController(Creature character, Tile home) {
		this.character = character;
		this.home = home;
		visited = new ArrayList<Tile>();
	}

	public void update(GameContainer gc, float dt) {
		if(!character.isDead()) {

			// clear dead target
			if(target != null && target.isDead()) target = null;

			// character is home
			if(character.getTile().getId() == home.getId()) {
				// patrolling
				if(route != null && route.size() >= 1) {
					routeIndex++;
					if(routeIndex >= route.size()) routeIndex = 0;
					home = route.get(routeIndex);
			 	}
			}

			// character is a follower
			if(
				follow == true
				&& (int) getLineDistance(character.getTile(), leader.getTile()) > followDistance
			) {
				home = leader.getTile();
			}

			// look for an enemy
			resetPath();
			path = findEnemy(pathList);

			// look for home
			if(
				!wander &&
				(
					(path == null && character.getTile().getId() != home.getId())

					// won't attack if far from home
					|| (
						target != null
						&& (int) getLineDistance(target.getTile(), home) > aggroRange
					)
				)
			) {
				resetPath();
				path = findHome(pathList);
			}

			// found something
			if(path != null) {
				// adjacent to enemy
				if(
					target != null 
					&& path.size() < 3
				) {
					for(int i = 0; i < 4; i++) {
						Tile neighbor = character.getTile().getNeighbor(i);
						if(
							neighbor != null 
							&& neighbor.getId() == path.get(1).getId()
						) {
							for(int j = 0; j < neighbor.getEntities().size(); j++) {
								if(target.getId() == neighbor.getEntities().get(j).getId()) {
									character.turn(i);
									character.attack();
									return;
								}
							}
						}
					}
				}

				// target at a distance
				else {
					// using a bow
					if(
						character.getWeapon() != null
						&& character.getWeapon().getTag().equals("bow")
						&& target != null
					) {

						// for each direction
						for(int i = 0; i < 4; i++) {
							Tile neighbor = character.getTile().getNeighbor(i);

							if(neighbor == null) break;

							// check line
							for(int j = 0; j < aggroRange; j++) {
								neighbor = neighbor.getNeighbor(i);

								if(neighbor == null) break;
								
								if(
									neighbor != null
								) {
									for(int k = 0; k < neighbor.getEntities().size(); k++) {
										if(target.getId() == neighbor.getEntities().get(k).getId()) {
											character.turn(i);
											character.attack();
											return;
										}
									}
								}
							}
						}
					}

					for(int i = 0; i < 4; i++) {
						Tile neighbor = character.getTile().getNeighbor(i);
						if(
							neighbor != null 
							&& path.size() > 1
							&& neighbor.getId() == path.get(1).getId()
						) {
							character.walk(i);
							return;
						}
					}
				}
			}

			// wanderer didn't find anything
			else if(wander == true && (int) (100 * Math.random()) + 1 <= 10) {
			 	// wander
			 	character.walk((int) (Math.random() * 4));
			}
		}	
	}

	public ArrayList<Tile> findEnemy(ArrayList<ArrayList<Tile>> paths) {
		ArrayList<Tile> path = null;

		if(
			paths.size() > 0 
			&& paths.get(0).size() <= aggroRange
		) path = paths.remove(0);
		else {
			target = null;
			visited.clear();
			return null;
		}

		// get the last tile in path
		Tile tile = path.get(path.size() - 1);
		// create a new path for each unvisited neighbor
		for(int j = 0; j < 4; j++) {
			Tile neighbor = tile.getNeighbor(j);
			if(neighbor == null) continue;

			// search entities in neighbor tile
			for(int i = 0; i < neighbor.getEntities().size(); i++) {
				Entity entity = neighbor.getEntities().get(i);
				if(
					!entity.isDead()
					&& entity.getFaction() != null 
					&& character.getFaction() != null 
					&& character.getFaction().isEnemy(entity.getFaction().getName())
				) {
					target = neighbor.getEntities().get(i);
					path.add(neighbor);
					visited.clear();
					return path;
				}
			}

			boolean isVisited = false;

			// ignore blocked tiles
			if(neighbor.isBlocked(character)) {
				visited.add(neighbor);
				isVisited = true;

			// check if already visited
			} else {
				for(int k = 0; k < visited.size(); k++) {
					if(neighbor.getId() == visited.get(k).getId()) {
		 				isVisited = true;
		 				break;
		 			}
				}
			}
			
			// fork path for each unvisited neighbor
			if(!isVisited) {
				ArrayList<Tile> newPath = new ArrayList<Tile>();
				for(int k = 0; k < path.size(); k++) newPath.add(path.get(k));
				newPath.add(neighbor);
				paths.add(newPath);
				visited.add(neighbor);
			}
		}

		// path find on new paths
		return findEnemy(paths);
	}

	public ArrayList<Tile> findHome(ArrayList<ArrayList<Tile>> paths) {
		ArrayList<Tile> path = null;

		if(
			paths.size() > 0 
			&& paths.get(0).size() <= 3 * aggroRange
		) path = paths.remove(0);
		else {
			target = null;
			visited.clear();
			return null;
		}

		// get the last tile in path
		Tile tile = path.get(path.size() - 1);
		// create a new path for each unvisited neighbor
		for(int j = 0; j < 4; j++) {
			Tile neighbor = tile.getNeighbor(j);
			if(neighbor == null) continue;

			// check for home
			if(
				home != null
				&& neighbor.getId() == home.getId()
			) {
				path.add(neighbor);
				visited.clear();
				return path;
			}

			boolean isVisited = false;

			// ignore blocked tiles
			if(neighbor.isBlocked(character)) {
				visited.add(neighbor);
				isVisited = true;

			// check if already visited
			} else {
				for(int k = 0; k < visited.size(); k++) {
					if(neighbor.getId() == visited.get(k).getId()) {
		 				isVisited = true;
		 				break;
		 			}
				}
			}
			
			// fork path for each unvisited neighbor
			if(!isVisited) {
				ArrayList<Tile> newPath = new ArrayList<Tile>();
				for(int k = 0; k < path.size(); k++) newPath.add(path.get(k));
				newPath.add(neighbor);
				paths.add(newPath);
				visited.add(neighbor);
			}
		}

		// path find on new paths
		return findHome(paths);
	}

	public void render(GameContainer gc, Renderer r) {

	}

	public void setRoute(ArrayList<Tile> route) {
		this.route = route;
	}

	public void setWander(boolean value) {
		this.wander = value;
	}

	public void setFollow(boolean value) {
		this.follow = value;
	}

	public void setLeader(Entity leader) {
		this.leader = leader;
	}

	public double getLineDistance(Tile tile1, Tile tile2) {
		return Math.sqrt(Math.pow(tile1.getX() - tile2.getX(), 2) + Math.pow(tile1.getY() - tile2.getY(), 2));
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public void resetPath() {
		path = new ArrayList<Tile>();
		pathList = new ArrayList<ArrayList<Tile>>();
		path.add(character.getTile());
		pathList.add(path);
	}
}