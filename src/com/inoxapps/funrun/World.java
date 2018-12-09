package com.inoxapps.funrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.inoxapps.funrun.gameObjects.Platform;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.gameObjects.PlayerType;
import com.inoxapps.funrun.gameObjects.PowerUpPool;
import com.inoxapps.funrun.gameObjects.PowerUpTrigger;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster.BoosterState;
import com.inoxapps.funrun.physics.Physics;
import com.inoxapps.funrun.ui.ScreenEnum;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.StoreManager;

public class World {

	private final Vector3 cameraTarget = new Vector3();
	private boolean isScoreUpdated = false;
	private Element rootElement;
	private GameState state;
	private Platform lastSpawnPlatform;
	private final float playersGap = 3f;
	private int currentPosition = 1;
	private int playersPosition = 4;
	public final HashMap<Platform, Integer> platformMap = new HashMap<Platform, Integer>();
	public final List<Player> players = new ArrayList<Player>();
	public final Array<Platform> platforms = new Array<Platform>();
	public final Array<AbstractBooster> powerUps = new Array<AbstractBooster>();
	public final Array<PowerUpTrigger> triggers = new Array<PowerUpTrigger>();
	public final Player player;
	public final OrthographicCamera gameCam;
	public final PowerUpPool powerUpsPool;
	public final HashMap<Integer, Player> playersPositions = new HashMap<Integer, Player>();

	public World() {
		// CoinManager.resetCoins();
		// CoinManager.flushTotalCoins();
		// System.out.println(CoinManager.getTotalCoins());
		if (ScreenEnum.GAME.isMultiPlayer)
			CoinManager.updateInitialCoins();
		state = GameState.READY;
		Physics.gravity = Settings.GRAVITY;
		Physics.initPhysics();
		AbstractBooster.boosterNotifications = new ArrayList<AbstractBooster>();
		try {
			this.rootElement = new XmlReader().parse(Gdx.files.internal("data/level2.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.parseData();
		this.gameCam = new OrthographicCamera(Settings.VIEWPORT_WIDTH / Settings.CAM_SCALE, Settings.VIEWPORT_HEIGHT
				/ Settings.CAM_SCALE);
		this.powerUpsPool = new PowerUpPool(gameCam);
		this.player = initArcadePlayers();
		// this.player = players.get(Settings.PLAYER_NUMBER - 1);
		this.gameCam.position.set(player.getPosition().x * gameCam.zoom,
				gameCam.viewportHeight / 2 + 2f * gameCam.zoom, 0);
		this.gameCam.zoom = 1.5f;
	}

	private void parseData() {
		Element levelElement = rootElement.getChild(ScreenEnum.GAME.levelSelected);
		Iterator<Element> iterator = levelElement.getChildrenByName("GameObject").iterator();
		while (iterator.hasNext()) {
			Element gameObject = iterator.next();
			float x = Float.parseFloat(gameObject.getAttribute("x")) / Settings.CAM_SCALE;
			float y = Float.parseFloat(gameObject.getAttribute("y")) / Settings.CAM_SCALE;
			float width = Float.parseFloat(gameObject.getAttribute("width")) / Settings.CAM_SCALE;
			float height = Float.parseFloat(gameObject.getAttribute("height")) / Settings.CAM_SCALE;
			if (gameObject.getAttribute("type").equals("Platform")) {
				Platform platform = new Platform(new Vector2(x, y), width, height, Assets.level_platforms);
				addPlatform(gameObject, platform);
			} else if (gameObject.getAttribute("type").equals("obs")) {
				Platform platform = new Platform(new Vector2(x, y), width, height, Assets.level_staticObstacle);
				platforms.add(platform);
			} else if (gameObject.getAttribute("type").equals("cloud")) {
				Platform platform = new Platform(new Vector2(x, y), width, height / 4, Assets.level_cloud);
				addPlatform(gameObject, platform);
			} else if (gameObject.getAttribute("type").equals("AiryPlatform")) {
				Platform platform = new Platform(new Vector2(x, y), width, height, Assets.level_airyPlatform);
				platform.setRotation(Integer.parseInt(gameObject.getAttribute("rotation")));
				addPlatform(gameObject, platform);
			} else if (gameObject.getAttribute("type").equals("booster")) {
				triggers.add(new PowerUpTrigger(new Vector2(x, y), Assets.commonlevel_powerUpTrigger));
			} else if (gameObject.getAttribute("type").equals("finishLine")) {
				for (Sprite sprite : Assets.commonlevel_finishLineSprites) {
					sprite.setPosition(x, y);
				}
			}
		}
	}

	private void addPlatform(Element gameObject, Platform platform) {
		platforms.add(platform);
		if (gameObject.getAttribute("spawnplatform").equals("true")) {
			platform.setSpawnPlatform(true);
			if (lastSpawnPlatform != null)
				platformMap.put(lastSpawnPlatform, platforms.size - 1);
			lastSpawnPlatform = platform;
		}
		if (gameObject.getAttribute("canJump").equals("true")) 
			platform.setCanJump(true);
	}

	public Player initArcadePlayers() {
		Player playerNotAI = null;
		for (int i = 0; i < Settings.PLAYER_NUMBER; i++) {
			Player player;
			if (!ScreenEnum.GAME.isMultiPlayer) {
				if (i == Settings.PLAYER_NUMBER - 1) {
					player = new Player(new Vector2((i + 1) * playersGap,
							platforms.get(0).getBounds().getVertices()[3].y), false,
							StoreManager.getEquippedItem().type, this, Assets.playerName);
					playerNotAI = player;
				} else
					player = new Player(new Vector2((i + 1) * playersGap,
							platforms.get(0).getBounds().getVertices()[3].y), true,
							PlayerType.values()[Settings.PLAYER_NUMBER - 1 - i], this,
							PlayerType.values()[Settings.PLAYER_NUMBER - 1 - i].name());
			} else {
				if (ScreenEnum.GAME.names.get(i).equals(Assets.playerName)) {
					player = new Player(new Vector2((i + 1) * playersGap,
							platforms.get(0).getBounds().getVertices()[3].y), false, ScreenEnum.GAME.types.get(i),
							this, ScreenEnum.GAME.names.get(i));
					playerNotAI = player;
				} else
					player = new Player(new Vector2((i + 1) * playersGap,
							platforms.get(0).getBounds().getVertices()[3].y), true, ScreenEnum.GAME.types.get(i), this,
							ScreenEnum.GAME.names.get(i));
			}
			players.add(player);
		}
		return playerNotAI;

	}

	public void update(float deltaTime) {

		if (player.getState() != PlayerState.ON_FINISHLINE)
			updateCamera();

		updatePlayers(deltaTime);

		updateObstacles(deltaTime);

		Physics.update(Physics.deltaTime * deltaTime);
	}

	private void updateObstacles(float deltaTime) {
		for (AbstractBooster powerUp : powerUps) {
			if (powerUp.getState() == BoosterState.FREE)
				freePowerUp(powerUp);
			else if (powerUp.getState() != BoosterState.PICKED)
				powerUp.update(deltaTime);
		}
	}

	// TODO refactor this method
	private void updatePlayers(float deltaTime) {

		this.player.update(deltaTime);

		if (player.getState() != PlayerState.ON_FINISHLINE) {
			playersPosition = 4;
		}

		boolean isGameOver = true;

		for (Player player : players) {

			if (player != this.player)
				player.update(deltaTime);

			if (this.player.getState() != PlayerState.ON_FINISHLINE && player.getState() != PlayerState.ON_FINISHLINE
					&& this.player != player) {
				if (this.player.getPosition().x > player.getPosition().x) {
					playersPosition--;
				}
			}

			if (player.getState() == PlayerState.ON_FINISHLINE) {
				if (!playersPositions.containsValue(player)) {
					playersPositions.put(currentPosition, player);
					if (ScreenEnum.GAME.isMultiPlayer && player == this.player) {
						if (CoinManager.updateCoins(PlayerPosition.values()[currentPosition - 1]))
							isScoreUpdated = true;
					}
					currentPosition++;
				}
				isGameOver = isGameOver && (true);
			} else {
				isGameOver = isGameOver && (false);
			}
		}

		gameOver(isGameOver);
	}

	public boolean isScoreUpdated() {
		return isScoreUpdated;
	}

	private void updateCamera() {
		float y = player.getPosition().y + 2f * gameCam.zoom;
		cameraTarget.set(player.getPosition().x + 8f * gameCam.zoom, y, 0);
		gameCam.position.lerp(cameraTarget, 0.1f);
		gameCam.position.y = y;
	}

	private void freePowerUp(AbstractBooster powerUp) {
		powerUpsPool.free(powerUp);
		powerUps.removeValue(powerUp, true);
	}

	public void gameOver(boolean isGameOver) {
		if (isGameOver) {
			CoinManager.flushTotalCoins();
			setState(GameState.FINISH);
			Assets.assets_result_screen_Sound.play(Assets.soundVolume);
		}
	}

	public int getPlayersPosition() {
		return playersPosition;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}
}
