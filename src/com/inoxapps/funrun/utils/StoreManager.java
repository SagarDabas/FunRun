package com.inoxapps.funrun.utils;

import com.inoxapps.funrun.gameObjects.PlayerType;

public class StoreManager {

	public static enum Item_Status {
		BOUGHT, EQUIPPED
	}

	public static enum Items {
		ITEM_BLACKBOY(0, PlayerType.BLACKBOY), ITEM_GIRL(50, PlayerType.GIRL), ITEM_BOY(100, PlayerType.BOY), ITEM_NINJA(
				250, PlayerType.NINJA), ITEM_REDGIRL(500, PlayerType.REDGIRL), ITEM_BLACKBOY2(1000,
				PlayerType.BLACKBOY2), ITEM_HELMETBOY(2000, PlayerType.HELMETBOY), ITEM_BULLHATBOY(3000,
				PlayerType.BULLHATBOY);

		private Items(int rate, PlayerType type) {
			this.rate = rate;
			this.type = type;
			loadItemStatus();

			// System.out.println("FREE COINS");
			// CoinManager.addCoins(1000);
			// CoinManager.flushTotalCoins();
			// CoinManager.resetCoins();
			// CoinManager.flushTotalCoins();
			// resetItemStatus();
		}

		private void loadItemStatus() {
			isItemBought = PrefBean.getBooleanPreference(this, Item_Status.BOUGHT, false);
			isItemEquipped = PrefBean.getBooleanPreference(this, Item_Status.EQUIPPED, false);
		}

		private void resetItemStatus() {
			PrefBean.setBooleanPreference(this, Item_Status.BOUGHT, false);
			PrefBean.setBooleanPreference(this, Item_Status.EQUIPPED, false);
			isItemBought = false;
			isItemEquipped = false;
		}

		private void setItemBought(boolean isBought) {
			this.isItemBought = isBought;
			PrefBean.setBooleanPreference(this, Item_Status.BOUGHT, isBought);
		}

		private void setItemEquipped(boolean isEquipped) {
			this.isItemEquipped = isEquipped;
			PrefBean.setBooleanPreference(this, Item_Status.EQUIPPED, isEquipped);
		}

		public boolean isItemBought() {
			return isItemBought;
		}

		public boolean isItemEquipped() {
			return isItemEquipped;
		}

		private boolean isItemEquipped;
		private boolean isItemBought;
		public final int rate;
		public final PlayerType type;
	}

	public static Items getEquippedItem() {
		for (Items item : Items.values()) {
			if (item.isItemEquipped)
				return item;
		}
		return null;
	}

	public static boolean buyItem(Items item) {
		if (CoinManager.getTotalCoins() >= item.rate) {
			CoinManager.addCoins(-item.rate);
			CoinManager.flushTotalCoins();
			item.setItemBought(true);
			return true;
		}
		return false;
	}

	public static boolean equipItem(Items selectedItem) {
		if (selectedItem.isItemBought()) {
			for (Items item : Items.values()) {
				if (item.isItemEquipped) {
					item.setItemEquipped(false);
				}
			}
			selectedItem.setItemEquipped(true);
			return true;
		}
		return false;
	}

	public static void equipDefaultItem() {
		Items.ITEM_BLACKBOY.setItemBought(true);
		Items.ITEM_BLACKBOY.setItemEquipped(true);
	}
}
