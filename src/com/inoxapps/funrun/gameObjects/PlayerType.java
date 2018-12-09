package com.inoxapps.funrun.gameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.Assets;

public enum PlayerType {

	BLACKBOY(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(0);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(0);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(0);
		}
	},
	GIRL(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(1);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(1);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(1);
		}
	},
	BOY(5) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(2);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(2);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(2);
		}
	},
	NINJA(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(3);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(3);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(3);
		}
	},
	REDGIRL(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(4);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(4);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(4);
		}
	},
	BLACKBOY2(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(5);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(5);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(5);
		}
	},
	HELMETBOY(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(6);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(6);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(6);
		}
	},
	BULLHATBOY(4) {
		@Override
		public void setCommonLevelPlayerIcon() {
			this.playerHead = Assets.commonlevel_playersHead.get(7);
		}

		@Override
		public void setUIPlayerIcon() {
			this.ui_multiplayer_playerIcon = Assets.ui_multiplayer_playerIcons.get(7);
			this.ui_store_playerIcon = Assets.ui_store_playerIcons.get(7);
		}
	},
	;

	public final int bodyCount;
	public Sprite playerHead;
	public Sprite ui_multiplayer_playerIcon;
	public Sprite ui_store_playerIcon;

	private PlayerType(int bodyCount) {
		this.bodyCount = bodyCount;
	}

	public abstract void setCommonLevelPlayerIcon();

	public abstract void setUIPlayerIcon();

}
