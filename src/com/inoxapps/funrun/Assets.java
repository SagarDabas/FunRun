package com.inoxapps.funrun;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.gameObjects.PlayerType;
import com.inoxapps.funrun.utils.PrefBean;
import com.inoxapps.funrun.utils.PrefBean.Pref;

public class Assets {

	private static AssetManager[] managers = new AssetManager[3];
	public static TextureAtlas funRunAtlas;
	public static String playerName;

	public static Sprite commonlevel_backGround;
	public static Rectangle commonlevel_deployBounds;
	public static Rectangle commonlevel_jumpBounds;
	public static Animation commonlevel_freezeNew;
	public static Animation commonlevel_bearTrapAnimation;
	public static Animation commonlevel_bombAnimation;
	public static Animation commonlevel_teleportAnimation;
	public static Array<Sprite> commonlevel_powerUpIcons;
	public static Array<Sprite> commonlevel_bombSprites;
	public static Array<Sprite> commonlevel_buildingsBackup;
	public static Array<Sprite> commonlevel_clouds;
	public static Array<Sprite> commonlevel_thunderClouds;
	public static Array<Sprite> commonlevel_shocks;
	public static Array<Sprite> commonlevel_bloodPatch;
	public static Array<Sprite> commonlevel_finishLineSprites;
	public static Array<Sprite> commonlevel_playersHead;
	public static Animation commonlevel_finishLineAnim;
	public static Sprite commonlevel_jumpButton;
	public static Sprite commonlevel_thunder;
	public static Sprite commonlevel_deployButton;
	public static Sprite commonlevel_powerUpTrigger;
	public static Sprite commonlevel_shield;
	public static Sprite commonlevel_freezeBullet;
	public static Sprite commonlevel_playerSwitchBullet;
	public static Sprite commonlevel_slicer;
	public static Sprite commonlevel_slicerBlood;
	public static Sprite commonlevel_translucent;
	public static Sprite commonlevel_playerArrow;
	public static Sprite[] commonlevel_buildings;
	public static Sprite commonlevel_line;
	public static Sprite commonlevel_cross;
	private static HashMap<Sprite, Sprite> commonlevel_smallIcons = new HashMap<Sprite, Sprite>();
	private static HashMap<String, Animation> commonlevel_animationMap = new HashMap<String, Animation>();
	private static HashMap<String, Array<Sprite>> commonlevel_bodyMap = new HashMap<String, Array<Sprite>>();

	public static void loadCommonLevel(AssetManager manager) {
		managers[1] = manager;
		funRunAtlas = manager.get("data/images/funrun-commonlevel-pack", TextureAtlas.class);
		commonlevel_powerUpIcons = funRunAtlas.createSprites("icon");
		commonlevel_deployButton = funRunAtlas.createSprite("deployButton");
		commonlevel_deployBounds = new Rectangle(0, 0, commonlevel_deployButton.getRegionWidth() * 2,
				commonlevel_deployButton.getRegionHeight() * 2);
		commonlevel_jumpButton = funRunAtlas.createSprite("jumpButton");
		commonlevel_jumpBounds = new Rectangle(Settings.VIEWPORT_WIDTH - commonlevel_jumpButton.getRegionWidth() * 3f,
				0, commonlevel_jumpButton.getRegionWidth() * 3, commonlevel_jumpButton.getRegionHeight() * 3);
		commonlevel_bearTrapAnimation = new Animation(1 / 12f, initWorldSprites(("beartrap")));
		commonlevel_bearTrapAnimation.setPlayMode(Animation.NORMAL);
		commonlevel_bombAnimation = new Animation(1 / 12f, initWorldSprites("mind-blast"));
		commonlevel_bombAnimation.setPlayMode(Animation.NORMAL);
		commonlevel_bombSprites = initWorldSprites("bombtrap");
		commonlevel_freezeNew = new Animation(1 / 12f, initWorldSprites("freezeNew"));
		commonlevel_freezeNew.setPlayMode(Animation.LOOP);
		commonlevel_teleportAnimation = new Animation(1 / 25f, initWorldSprites("teleport"));
		commonlevel_teleportAnimation.setPlayMode(Animation.NORMAL);
		commonlevel_translucent = funRunAtlas.createSprite("translucent");
		commonlevel_bloodPatch = funRunAtlas.createSprites("bloodPatch");
		commonlevel_freezeBullet = initWorldSprite("freez bullet");
		commonlevel_playerSwitchBullet = initWorldSprite("switchBullet");
		commonlevel_slicer = initWorldSprite("slicer");
		commonlevel_slicerBlood = initWorldSprite("slicerBlood");
		commonlevel_shield = initWorldSprite("shield");
		commonlevel_powerUpTrigger = initWorldSprite("obstacleTrigger");
		commonlevel_clouds = funRunAtlas.createSprites("cloud");
		commonlevel_clouds.add(new Sprite(commonlevel_clouds.get(0)));
		initClouds();
		commonlevel_thunderClouds = funRunAtlas.createSprites("thundercloud");
		commonlevel_shocks = initWorldSprites("shock");
		commonlevel_thunder = initWorldSprite("thunder");
		commonlevel_finishLineSprites = initWorldSprites("finish line");
		commonlevel_finishLineAnim = new Animation(1 / 12f, commonlevel_finishLineSprites);
		commonlevel_finishLineAnim.setPlayMode(Animation.LOOP);
		commonlevel_playersHead = funRunAtlas.createSprites("playersIcon");

		// Sometimes PlayerType is loaded before the assets are loaded, so
		// initialize the values here.
		for (PlayerType type : PlayerType.values()) {
			type.setCommonLevelPlayerIcon();
		}
		commonlevel_playerArrow = initWorldSprite("arrow");
		commonlevel_backGround = funRunAtlas.createSprite("levelBG");
		commonlevel_cross = funRunAtlas.createSprite("cross");
		commonlevel_line = funRunAtlas.createSprite("line");
		loadPlayerAnims();
		loadPlayerSprites();
		initIcons(2f);
		initBuildings();
	}

	public static Array<Sprite> level_platforms;
	public static Sprite level_airyPlatform;
	public static Sprite level_staticObstacle;
	public static Sprite level_cloud;

	public static void loadLevel(AssetManager manager, String levelPack) {
		managers[2] = manager;
		funRunAtlas = manager.get("data/images/" + levelPack, TextureAtlas.class);
		level_staticObstacle = initWorldSprite("obs");
		level_cloud = initWorldSprite("cloud");
		level_airyPlatform = initWorldSprite("airyPlatform");
		level_platforms = initWorldSprites("platform");
		if (level_platforms != null) {
			level_platforms.add(new AtlasSprite((AtlasSprite) level_platforms.get(1)));
			level_platforms.get(2).flip(true, false);
		}
		// initBuildings();
	}

	public static Sprite ui_levelChangeArrowRight;
	public static Sprite ui_levelChangeArrowLeft;
	public static Sprite ui_commonBG;
	public static Sprite ui_friendInviteButton;
	public static Sprite ui_gameSelectBG;
	public static Sprite ui_gameSelectHomeButton;
	public static Sprite ui_leaderboard;
	public static Sprite ui_leaderBoardButton;
	public static Sprite ui_multiPlayerButton;
	public static Sprite ui_multiPlayerHomeButton;
	public static Sprite ui_playButton;
	public static Sprite ui_resultRefreshButton;
	public static Sprite ui_resultHomeButton;
	public static Sprite ui_resultStoreButton;
	public static Sprite ui_resultText;
	public static Sprite ui_searchingText;
	public static Sprite ui_selectLevelBG;
	public static Sprite ui_selectLevelHomeButton;
	public static Sprite ui_singlePlayerButton;
	public static Sprite ui_storeButton;
	public static Sprite ui_store_buyButton;
	public static Sprite ui_store_buycoinsButton;
	public static Sprite ui_store_coins;
	public static Sprite ui_store_equipped;
	public static Sprite ui_store_frame;
	public static Sprite ui_store_freecoinsButton;
	public static Sprite ui_store_equipButton;
	public static Sprite ui_store_storetext;
	public static Sprite ui_store_scrollOverLeft;
	public static Sprite ui_store_scrollOverRight;
	public static Sprite ui_soundBox;
	public static Sprite ui_soundOn;
	public static Sprite ui_soundOff;
	public static Sprite ui_coin;
	public static Array<Sprite> ui_scores;
	public static Array<Sprite> ui_levelIcons;
	public static Array<Sprite> ui_multiplayer_playerIcons;
	public static Array<Sprite> ui_store_playerIcons;

	public static void loadUI(AssetManager manager) {
		managers[0] = manager;
		funRunAtlas = manager.get("data/images/funrun-ui-pack", TextureAtlas.class);
		ui_soundBox = funRunAtlas.createSprite("soundbox");
		ui_soundOn = funRunAtlas.createSprite("soundOn");
		ui_soundOff = funRunAtlas.createSprite("soundOff");
		ui_coin = funRunAtlas.createSprite("coin");
		ui_scores = funRunAtlas.createSprites("uiscores");
		ui_store_playerIcons = funRunAtlas.createSprites("ui-store-playerIcon");
		ui_store_buyButton = funRunAtlas.createSprite("ui-store-buy");
		ui_store_buycoinsButton = funRunAtlas.createSprite("ui-store-buycoins");
		ui_store_coins = funRunAtlas.createSprite("ui-store-coins");
		ui_store_equipButton = funRunAtlas.createSprite("ui-store-equip");
		ui_store_equipped = funRunAtlas.createSprite("ui-store-equipped");
		ui_store_frame = funRunAtlas.createSprite("ui-store-frame");
		ui_store_freecoinsButton = funRunAtlas.createSprite("ui-store-freecoins");
		ui_store_storetext = funRunAtlas.createSprite("ui-store-storetext");
		ui_levelChangeArrowRight = funRunAtlas.createSprite("arrowRight");
		ui_levelChangeArrowLeft = funRunAtlas.createSprite("arrowLeft");
		ui_commonBG = funRunAtlas.createSprite("commonBG");
		ui_friendInviteButton = funRunAtlas.createSprite("friendInvite");
		ui_gameSelectBG = funRunAtlas.createSprite("gameSelectbg");
		ui_gameSelectHomeButton = funRunAtlas.createSprite("gameSelectHome");
		ui_leaderboard = funRunAtlas.createSprite("leaderBoard");
		ui_leaderBoardButton = funRunAtlas.createSprite("leaderBoardButton");
		ui_multiPlayerButton = funRunAtlas.createSprite("multiplayer");
		ui_multiPlayerHomeButton = funRunAtlas.createSprite("multiplayerHome");
		ui_playButton = funRunAtlas.createSprite("playButton");
		ui_resultRefreshButton = funRunAtlas.createSprite("resultRefresh");
		ui_resultHomeButton = funRunAtlas.createSprite("resultHome");
		ui_resultStoreButton = funRunAtlas.createSprite("resultStore");
		ui_resultText = funRunAtlas.createSprite("resultText");
		ui_searchingText = funRunAtlas.createSprite("searchingText");
		ui_selectLevelBG = funRunAtlas.createSprite("selectLevelBG");
		ui_selectLevelHomeButton = funRunAtlas.createSprite("selectLevelHome");
		ui_singlePlayerButton = funRunAtlas.createSprite("singlePlayer");
		ui_storeButton = funRunAtlas.createSprite("storeButton");
		ui_levelIcons = funRunAtlas.createSprites("levelIcon");
		ui_multiplayer_playerIcons = funRunAtlas.createSprites("ui-multiplayer-playerIcon");
		ui_store_scrollOverRight = funRunAtlas.createSprite("ui-store-scrollOver");
		ui_store_scrollOverLeft = new AtlasSprite((AtlasSprite)ui_store_scrollOverRight);
		ui_store_scrollOverLeft.flip(true, false);
		for (PlayerType type : PlayerType.values()) {
			type.setUIPlayerIcon();
		}
		getSoundVolume();
		playerName = PrefBean.getStringPreference(PrefBean.Pref.NAME, "YOU");
	}

	public static Sound assets_button_Sound;
	public static Sound assets_beartrap_Sound;
	public static Sound assets_freeze_out_Sound;
	public static Sound assets_freeze_Sound;
	public static Sound assets_freeze_teleport_shoot_Sound;
	public static Sound assets_mine_blast_Sound;
	public static Sound assets_mine_step_Sound;
	public static Sound assets_power_collect_Sound;
	public static Sound assets_result_screen_Sound;
	public static Sound assets_teleport_Sound;
	public static Sound assets_thunder_Sound;
	public static Sound assets_slicer_Sound;
	public static Sound assets_purchaseYES_Sound;
	public static Sound assets_purchaseNO_Sound;
	public static BitmapFont assets_Whitefont;
	public static BitmapFont assets_Blackfont;


	public static void initAssets(AssetManager manager) {
		assets_Whitefont = manager.get("data/funrunFont.fnt", BitmapFont.class);
		assets_Whitefont.setColor(1,1,1,1);
		assets_Blackfont = manager.get("data/funrunFont1.fnt", BitmapFont.class);
		assets_Blackfont.setColor(0,0,0,1);
		assets_button_Sound = manager.get("data/sounds/button.ogg", Sound.class);
		assets_beartrap_Sound = manager.get("data/sounds/beartrap.ogg", Sound.class);
		assets_freeze_out_Sound = manager.get("data/sounds/freeze-out.ogg", Sound.class);
		assets_freeze_teleport_shoot_Sound = manager.get("data/sounds/freeze-teleport-shoot.ogg", Sound.class);
		assets_freeze_Sound = manager.get("data/sounds/freeze.ogg", Sound.class);
		assets_mine_blast_Sound = manager.get("data/sounds/mine-blast.ogg", Sound.class);
		assets_mine_step_Sound = manager.get("data/sounds/mine-step.ogg", Sound.class);
		assets_power_collect_Sound = manager.get("data/sounds/power-collect.ogg", Sound.class);
		assets_result_screen_Sound = manager.get("data/sounds/result-screen.ogg", Sound.class);
		assets_teleport_Sound = manager.get("data/sounds/teleport.ogg", Sound.class);
		assets_thunder_Sound = manager.get("data/sounds/thunder.ogg", Sound.class);
		assets_slicer_Sound = manager.get("data/sounds/slicer.ogg", Sound.class);
		assets_purchaseYES_Sound = manager.get("data/sounds/purchaseYES.ogg", Sound.class);
		assets_purchaseNO_Sound = manager.get("data/sounds/purchaseNO.ogg", Sound.class);
	}

	private static void initIcons(float scale) {
		for (Sprite sprite : commonlevel_powerUpIcons) {
			Sprite smallSprite = new Sprite(sprite);
			smallSprite.setSize(smallSprite.getWidth() / scale, smallSprite.getHeight() / scale);
			smallSprite.setOrigin(smallSprite.getWidth() / 2, smallSprite.getHeight() / 2);
			commonlevel_smallIcons.put(sprite, smallSprite);
		}
	}

	public static Sprite getSmallIcon(Sprite powerUpIcon) {
		return commonlevel_smallIcons.get(powerUpIcon);
	}

	private static void initBuildings() {
		commonlevel_buildingsBackup = funRunAtlas.createSprites("building");
		commonlevel_buildings = new Sprite[8];
		updateBuildings(0, 8);
	}

	public static void updateBuildings(int firstIndex, int lastIndex) {
		for (int i = firstIndex; i < lastIndex; i++) {
			commonlevel_buildings[i] = commonlevel_buildingsBackup.get(MathUtils.random(0, 7));
		}
	}

	private static void loadPlayerAnims() {
		for (PlayerType type : PlayerType.values()) {
			for (PlayerState state : PlayerState.values()) {

				if (state == PlayerState.READY || state == PlayerState.RUNNING || state == PlayerState.JUMP
						|| state == PlayerState.FALL) {
					Animation anim;
					if (state != PlayerState.READY) {
						anim = new Animation(1 / 12f, initWorldSprites(type.name() + state.name()));
					} else {
						anim = new Animation(1 / 6f, initWorldSprites(type.name() + state.name()));
					}
					if (state == PlayerState.JUMP) {
						anim.setPlayMode(Animation.NORMAL);
					} else {
						anim.setPlayMode(Animation.LOOP);
					}
					commonlevel_animationMap.put(type.name() + state.name(), anim);
				}
			}
		}
	}

	private static void loadPlayerSprites() {
		for (PlayerType type : PlayerType.values()) {

			commonlevel_bodyMap.put(type.name() + "BODY", initWorldSprites(type.name() + "BODY"));
			commonlevel_bodyMap.put(type.name() + "SLICER", initWorldSprites(type.name() + "SLICER"));

			if (type != PlayerType.BLACKBOY && type != PlayerType.GIRL) {
				int i = 0;
				for (Sprite sprite : commonlevel_bodyMap.get(PlayerType.BLACKBOY.name() + "BODY")) {
					if (i > 0) {
						commonlevel_bodyMap.get(type.name() + "BODY").add(sprite);
					}
					i++;
				}
			}
		}
	}

	public static Array<Sprite> getPlayerSprites(PlayerType type, String slicerOrBody) {
		return commonlevel_bodyMap.get(type.name() + slicerOrBody);
	}

	private static void initClouds() {
		Assets.commonlevel_clouds.get(0).setPosition(100, getRandomY() - Assets.commonlevel_clouds.get(0).getHeight());
		Assets.commonlevel_clouds.get(1).setPosition(100 + 2 * Assets.commonlevel_clouds.get(0).getWidth(),
				getRandomY() - Assets.commonlevel_clouds.get(1).getHeight());
		Assets.commonlevel_clouds.get(2).setPosition(Settings.VIEWPORT_WIDTH / 2,
				getRandomY() - Assets.commonlevel_clouds.get(2).getHeight());
		Assets.commonlevel_clouds.get(3).setPosition(Settings.VIEWPORT_WIDTH * 1.5f,
				getRandomY() - Assets.commonlevel_clouds.get(3).getHeight());
		Assets.commonlevel_clouds.get(4).setPosition(Settings.VIEWPORT_WIDTH * 1.5f,
				getRandomY() - Assets.commonlevel_clouds.get(4).getHeight());
	}

	private static float getRandomY() {
		return MathUtils.random() * Settings.VIEWPORT_HEIGHT / 1.5f + Settings.VIEWPORT_HEIGHT / 3f;
	}

	private static Sprite initWorldSprite(String name) {
		Sprite sprite = funRunAtlas.createSprite(name);
		if (sprite != null)
			sprite.setSize(sprite.getWidth() / Settings.CAM_SCALE, sprite.getHeight() / Settings.CAM_SCALE);
		return sprite;
	}

	private static Array<Sprite> initWorldSprites(String name) {
		Array<Sprite> sprites = funRunAtlas.createSprites(name);
		for (Sprite sprite : sprites) {
			sprite.setSize(sprite.getWidth() / Settings.CAM_SCALE, sprite.getHeight() / Settings.CAM_SCALE);
		}
		if (sprites.size == 0)
			return null;
		else
			return sprites;
	}

	public static Animation getAnimation(PlayerState state, PlayerType type) {
		return commonlevel_animationMap.get(type.name() + state.name());
	}

	public static void dispose() {
		funRunAtlas = null;
		for (AssetManager manager : managers) {
			if (manager != null) {
				manager.clear();
			}
		}
	}

	public static void disposeLevel() {
		managers[2].clear();
	}

	public static Sprite splashScreen;

	public static void setSplashScreen(Sprite splashScreen) {
		Assets.splashScreen = splashScreen;
	}

	public static float soundVolume;

	private static void getSoundVolume(){
		if (PrefBean.getBooleanPreference(Pref.SOUND, false)) {
			soundVolume = 1;
		} else {
			soundVolume = 0;
		}
	}
	
	public static void setSoundsVolume() {
		if (PrefBean.getBooleanPreference(Pref.SOUND, false)) {
			PrefBean.setBooleanPreference(Pref.SOUND, false);
			soundVolume = 0;
		} else {
			PrefBean.setBooleanPreference(Pref.SOUND, true);
			soundVolume = 1;
		}
	}
}
