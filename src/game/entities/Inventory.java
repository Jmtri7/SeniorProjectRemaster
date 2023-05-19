// by: James Trinity
package game.entities;

import java.util.ArrayList;
import java.util.List;

// replacement for the basic arraylist inventory
public class Inventory {
	private Creature owner;

	private ArrayList<Item> items;
	private int selectedItem = 0;

	public Inventory(Creature owner, List<Item> items) {
		this.owner = owner;

		if(items != null) this.items = new ArrayList<>(items);
		else this.items = new ArrayList<Item>();
	}

	public void addItem(Item item) {
		if(items.size() == 0) selectedItem = 0;
		items.add(item);
	}

	public Item getSelectedItem() {
		if(items.size() == 0) return null;
		else return items.get(selectedItem);
	}

	public void useSelectedItem() {
		if(items.size() > 0) {
			items.get(selectedItem).use(owner);

			if(items.get(selectedItem).isConsumable()) {
				items.remove(selectedItem);
			}
			
			if(selectedItem >= items.size()) {
				selectedItem = items.size() - 1;
			}
		}
	}

	public void dropSelectedItem() {
		if(items.size() > 0) {
			Item item = items.get(selectedItem);
			
			item.setEquipped(false);

			items.remove(selectedItem);
			item.setTile(owner.getTile());
			owner.getTile().addItem(item);

			item.setX(owner.getTile().getX() * owner.getTile().getSize());
			item.setY(owner.getTile().getY() * owner.getTile().getSize());
			
			if(selectedItem >= items.size()) {
				selectedItem = items.size() - 1;
			}
		}
	}

	public int getSelector() {
		return selectedItem;
	}

	public void selectNext() {
		if(items.size() == 0 || items.size() == 1) return;

		selectedItem++;
		if(selectedItem >= items.size()) {
			selectedItem = 0;
		}
	}

	public void selectPrevious() {
		if(items.size() == 0 || items.size() == 1) return;

		selectedItem--;
		if(selectedItem < 0) {
			selectedItem = items.size() - 1;
		}
	}

	public ArrayList<Item> getItems() {
		return items;
	}
}