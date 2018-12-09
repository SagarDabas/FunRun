package com.inoxapps.funrun.gameObjects;

import java.text.DecimalFormat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.World;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.AbstractObstacle;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.ObstacleType;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.PlaceSwitcherObstacle;
import com.inoxapps.funrun.gameObjects.boosters.powerups.AbstractPowerUp;
import com.inoxapps.funrun.gameObjects.boosters.powerups.ShieldPowerUp;
import com.inoxapps.funrun.gameObjects.boosters.powerups.ThunderPowerUp;
import com.inoxapps.funrun.physics.CollisionSide;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Screen;

public final class Player extends DynamicGameObject {

	public final PlayerType type;
	public final boolean isAI;
	public final Array<DynamicGameObject> bodyParts;
	private Vector2 jumpSpeed;
	private Vector2 maximumSpeed;
	private float stateTime;
	private PlayerState state;
	private Sprite keyFrame;
	private Animation stateAnimation;
	private AbstractBooster booster;
	private AbstractPowerUp powerUpUsed;
	private AbstractObstacle obstacleUsed;
	private PowerUpTrigger triggerUsed;
	private Platform onStaticObject;
	private Platform nextSpawnPlatform;
	private Platform lastSpawnPlatform;
	private final World world;
	private final PlayerModifiers modifier;
	private float finishTimer = 3f;
	private boolean isGuarded;
	private boolean isOnStaticObject;
	private float raceTime;
	private String name;

	public Player(Vector2 position, boolean isAI, PlayerType type, World world, String name) {

		super(position, 0, 0, new Vector2(Settings.PlayerSettings.VELOCITY), new Vector2(
				Settings.PlayerSettings.ACCELERATION));
		this.world = world;
		this.name = name;
		this.type = type;
		this.isStatic = false;
		this.isAI = isAI;
		this.isCollidable = true;
		this.jumpSpeed = new Vector2(Settings.PlayerSettings.JUMP_SPEED);
		this.maximumSpeed = new Vector2(Settings.PlayerSettings.MAX_SPEED);
		this.nextSpawnPlatform = world.platforms.get(0);
		// TODO never pass this in a constructor
		this.bodyParts = createBodyParts();
		this.modifier = new PlayerModifiers(this);
		this.modifier.setAccRatio(0, 0);
		this.modifier.setVelRatio(0, 0);
		this.changeState(PlayerState.READY);
		grabPowerUp();
	}

	public Array<DynamicGameObject> createBodyParts() {
		Array<DynamicGameObject> temp = new Array<DynamicGameObject>();
		for (int i = 0; i < type.bodyCount; i++) {
			DynamicGameObject part = new DynamicGameObject() {
				@Override
				public void onCollision(DynamicGameObject gameObject, CollisionSide side) {
					super.onCollision(gameObject, side);
					if (gameObject.isStatic && side == CollisionSide.DOWNWARD)
						this.setVelocity(0, 0);
				}
			};
			part.isStatic = true;
			temp.add(part);
		}
		return temp;
	}

	public void resetBodyParts() {
		for (DynamicGameObject part : bodyParts) {
			part.isStatic = true;
			part.isTrigger = false;
			part.isCollidable = false;
		}
	}

	public void update(float deltaTime) {

		this.updateGeneral(deltaTime);

		switch (state) {
		case READY:
			if (isOnStaticObject && finishTimer < 0) {
				changeState(PlayerState.RUNNING);
				finishTimer = 3f;
			}
			finishTimer -= deltaTime;
			break;
		case SPAWN:
			updateSpawn();
			break;
		case RUNNING:
			if (isAI)
				updateAIjump();
			updateFall();
			break;
		case JUMP:
			getVelocity().x = jumpSpeed.x;
			if (isOnStaticObject) {
				changeState(PlayerState.RUNNING);
			}
			break;
		case FALL:
			if (isOnStaticObject)
				changeState(PlayerState.RUNNING);
			break;

		case HIT_OVER:
			if (isOnStaticObject)
				changeState(PlayerState.RUNNING);
			else {
				modifier.setVelRatio(0, 1f);
				modifier.setAccRatio(0, 1f);
				changeState(PlayerState.FALL);
			}
			break;
		case ON_FINISHLINE:
			updateOnFinishLine(deltaTime);
			break;
		}

	}

	private void updateOnFinishLine(float deltaTime) {
		if (finishTimer < 0 && isOnStaticObject) {
			modifier.setVelRatio(0, 1f);
			modifier.setAccRatio(0, 1f);
		}
		finishTimer -= deltaTime;
	}

	private void updateGeneral(float deltaTime) {
		if (keyFrame != null)
			setBounds(keyFrame.getWidth(), keyFrame.getHeight());

		if (getLastSpawnPlatform() != null) {
			if (getLastSpawnPlatform().getBounds().rotation <= 0
					&& getBounds().getVertex(3).y < getLastSpawnPlatform().getBounds().getVertex(1).y) {
				spawnOnNextPlatform();
			} else if (getLastSpawnPlatform().getBounds().rotation > 0
					&& getPosition().x > getLastSpawnPlatform().getBounds().getVertex(2).x
					&& getPosition().y < getLastSpawnPlatform().getBounds().getVertex(1).y) {
				spawnOnNextPlatform();
			}
		}

		if (getBounds().getVertex(0).x > nextSpawnPlatform.getBounds().getVertex(0).x) {
			updateSpawnPlatforms();
		}
		if (isGuarded) {
			updateGuarded(deltaTime);
		}

		maintainSpeed();

		if (isAI) {
			updateAIdeploy(deltaTime);
		}

		if (state != PlayerState.ON_FINISHLINE && state != PlayerState.READY)
			updateGameOver(deltaTime);

		if (state != PlayerState.HIT && state != PlayerState.HIT_OVER && state != PlayerState.SPAWN)
			stateTime += deltaTime;
	}

	private void updateGuarded(float deltaTime) {
		if (finishTimer < 0) {
			setGuarded(false);
			finishTimer = 3f;
		} else
			finishTimer -= deltaTime;
	}

	private void updateGameOver(float deltaTime) {
		if (position.x > ((Sprite) Assets.commonlevel_finishLineSprites.get(0)).getX()) {
			finishTimer = 1f;
			changeState(PlayerState.ON_FINISHLINE);
			modifier.setVelRatio(0.9f, 1f);
		}
		raceTime += deltaTime;
	}

	private void updateSpawn() {
		keyFrame.scale(0.15f);
		if (keyFrame.getScaleX() >= 1) {
			keyFrame.setScale(1f, 1f);
			changeState(PlayerState.RUNNING);
		}
	}

	private void spawnOnNextPlatform() {

		if (getObstacleUsed() != null) {
			getObstacleUsed().onHitTimeOver();
		}

		changeState(PlayerState.RUNNING);
		keyFrame = (Sprite) stateAnimation.getKeyFrame(0);
		changeState(PlayerState.SPAWN);

		modifier.setVelRatio(0, 0);
		modifier.setAccRatio(0, 0);

		setPosition(nextSpawnPlatform.getBounds().getVertices()[3].x, nextSpawnPlatform.getBounds().getVertices()[3].y);
		keyFrame.setOrigin(keyFrame.getWidth() / 2, keyFrame.getHeight() / 2);
		keyFrame.setScale(0, 0);
		freePowerUpUsed();

		updateSpawnPlatforms();
	}

	private void updateSpawnPlatforms() {
		setLastSpawnPlatform(nextSpawnPlatform);
		if (world.platformMap.get(getLastSpawnPlatform()) != null)
			nextSpawnPlatform = world.platforms.get(world.platformMap.get(getLastSpawnPlatform()));
	}

	@Override
	public void onTriggered(DynamicGameObject gameObject) {
		super.onTriggered(gameObject);
		if (gameObject instanceof PowerUpTrigger) {
			triggerUsed = (PowerUpTrigger) gameObject;
			grabPowerUp();
		} else if (gameObject instanceof AbstractObstacle && !isGuarded()) {
			hitWithObstacle((AbstractObstacle) gameObject);
		}
	}

	public void hitWithObstacle(AbstractObstacle obstacle) {
		if (obstacle.canHit(this)) {
			if ((state == PlayerState.JUMP && (obstacle.TYPE == ObstacleType.DYNAMIC))
					|| (state == PlayerState.RUNNING)) {
				freePowerUpUsed();
				obstacle.hitPlayer(this);
				setObstacleUsed(obstacle);
				changeState(PlayerState.HIT);
			}
		}
	}

	private float AIdeployTime;

	// TODO change it to center or a range
	private void updateAIdeploy(float deltaTime) {
		if (onStaticObject != null && AIdeployTime < 0)
			deploy();
		if (AIdeployTime >= 0)
			AIdeployTime -= deltaTime;
	}

	private void updateAIjump() {
		if (onStaticObject != null && ((Platform)onStaticObject).canJump() && position.x > (onStaticObject.getBounds().getVertices()[1].x - 1f)
				&& MathUtils.randomBoolean()) {
			jump();
		}
	}

	private void updateFall() {
		if (((!isOnStaticObject) || (onStaticObject != null && position.x > onStaticObject.getBounds().getVertex(2).x))) {
			changeState(PlayerState.JUMP);
			isOnStaticObject = false;
			onStaticObject = null;
		}
	}

	public void render(float deltaTime) {
		if (state != PlayerState.HIT && state != PlayerState.SPAWN && state != PlayerState.ON_FINISHLINE
				&& state != PlayerState.HIT_OVER) {
			keyFrame = (Sprite) stateAnimation.getKeyFrame(stateTime);
			keyFrame.setPosition(position.x, position.y);
			rotateSprite();
			if (isGuarded)
				keyFrame.draw(Screen.batcher, 0.5f);
			else
				keyFrame.draw(Screen.batcher);
		} else if (state == PlayerState.SPAWN || state == PlayerState.ON_FINISHLINE) {
			keyFrame.setPosition(position.x, position.y);
			rotateSprite();
			if (isGuarded)
				keyFrame.draw(Screen.batcher, 0.5f);
			else
				keyFrame.draw(Screen.batcher);
		}
	}

	private void rotateSprite() {
		if (onStaticObject != null) {
			if (onStaticObject.getBounds().rotation > 0)
				keyFrame.setOrigin(keyFrame.getWidth(), 0);
			else if (onStaticObject.getBounds().rotation < 0)
				keyFrame.setOrigin(0, 0);
			keyFrame.setRotation(onStaticObject.getBounds().rotation);
		} else
			keyFrame.setRotation(0);
	}

	private void maintainSpeed() {
		if (velocity.x > maximumSpeed.x) {
			velocity.x = maximumSpeed.x;
			this.setAcceleration(0, 0);
		} else if (getAcceleration().x == 0 && velocity.x < maximumSpeed.x) {
			this.setAcceleration(Settings.PlayerSettings.ACCELERATION.x, Settings.PlayerSettings.ACCELERATION.y);
		}
	}

	@Override
	public void onCollision(DynamicGameObject gameObject, CollisionSide side) {
		if (gameObject.isStatic) {
			if (side == CollisionSide.FORWARD && isAI)
				jump();
			else if (side == CollisionSide.DOWNWARD) {
				if (gameObject instanceof Platform) {
					onStaticObject = (Platform) gameObject;
				}
				isOnStaticObject = true;
			}
		}
	}

	public void deploy() {
		if (booster != null && (state == PlayerState.RUNNING || state == PlayerState.JUMP) && !isCollidingWithTrigger()) {
			if (booster instanceof AbstractPowerUp) {
				if (!(booster instanceof ThunderPowerUp)) {
					freePowerUpUsed();
					powerUpUsed = (AbstractPowerUp) booster;
				}
				booster.deploy(position.x, position.y, this);
				booster = null;
			} else if (booster instanceof AbstractObstacle) {
				booster.deploy(position.x, position.y, this);
				booster = null;
			}
		}
	}

	private boolean isCollidingWithTrigger() {
		if (triggerUsed == null)
			return false;
		else if (getBounds().getVertices()[1].x < triggerUsed.getBounds().getVertices()[0].x
				|| getBounds().getVertices()[0].x > triggerUsed.getBounds().getVertices()[1].x) {
			triggerUsed = null;
			return false;
		}
		return true;
	}

	private void changeState(PlayerState state) {
		this.state = state;
		stateTime = 0;
		if (state != PlayerState.HIT && state != PlayerState.HIT_OVER)
			stateAnimation = Assets.getAnimation(state, type);
		if (state == PlayerState.RUNNING) {
			modifier.setVelRatio(1f, 1f);
			modifier.setAccRatio(1f, 1f);
		}
	}

	private void grabPowerUp() {
		if (booster == null) {
			AbstractBooster powerUp = world.powerUpsPool.obtain();
			if (powerUp instanceof ThunderPowerUp)
				((ThunderPowerUp) powerUp).setPlayers(world.players);
			this.booster = powerUp;
			this.booster.grabbed(this);
			world.powerUps.add(powerUp);
			if (isAI)
				if (powerUp instanceof AbstractObstacle)
					AIdeployTime = MathUtils.random(1, 3);
				else
					AIdeployTime = 0;
		}
	}

	public void jump() {
		if (state == PlayerState.RUNNING) {
			setVelocity(jumpSpeed.x, jumpSpeed.y);
			modifier.setAccRatio(0f, 0f);
			stateTime = 0;
			isOnStaticObject = false;
			onStaticObject = null;
			changeState(PlayerState.JUMP);
		}
	}

	public void hitOver(boolean freePowerUp) {
		if (state == PlayerState.HIT) {
			if (!(getObstacleUsed() instanceof PlaceSwitcherObstacle))
				setGuarded(true);
			setObstacleUsed(null);
			changeState(PlayerState.HIT_OVER);
		}
		if (freePowerUp)
			powerUpUsed = null;
	}

	// TODO state is changed by the PlaceSwitcher and ThunderPowerUp, increased
	// coupling
	public void isHit(AbstractObstacle obstacle) {
		freePowerUpUsed();
		setObstacleUsed(obstacle);
		changeState(PlayerState.HIT);
	}

	private void freePowerUpUsed() {
		if (powerUpUsed != null) {
			powerUpUsed.onHitTimeOver();
		}
	}

	@Override
	public void setVelocity(float x, float y) {
		super.setVelocity(modifier.getVelRatioX() * x, y * modifier.getVelRatioY());
	}

	@Override
	public void setAcceleration(float x, float y) {
		super.setAcceleration(x * modifier.getAccRatioX(), y * modifier.getAccRatioY());
	}

	public void setJumpSpeed(float x, float y) {

		jumpSpeed.set(x * modifier.getJumpRatioX(), y * modifier.getJumpRatioY());
	}

	public void setMaxSpeed(float x, float y) {
		maximumSpeed.x = x * modifier.getMaxSpeedRatioX();
		maximumSpeed.y = y * modifier.getMaxSpeedRatioY();
	}

	public Sprite getPowerUpIcon() {
		if (booster != null) {
			return booster.icon;
		}
		return null;
	}

	public boolean isGuarded() {
		return isGuarded || powerUpUsed instanceof ShieldPowerUp;
	}

	public PlayerState getState() {
		return state;
	}

	public PlayerModifiers getModifier() {
		return modifier;
	}

	public Sprite getKeyFrame() {
		return keyFrame;
	}

	public void setGuarded(boolean isGuarded) {
		this.isGuarded = isGuarded;
	}

	public AbstractObstacle getObstacleUsed() {
		return obstacleUsed;
	}

	public void setObstacleUsed(AbstractObstacle obstacleUsed) {
		this.obstacleUsed = obstacleUsed;
	}

	public Platform getLastSpawnPlatform() {
		return lastSpawnPlatform;
	}

	public void setLastSpawnPlatform(Platform lastSpawnPlatform) {
		this.lastSpawnPlatform = lastSpawnPlatform;
	}

	public String getRaceTime() {
		return new DecimalFormat("##.##").format(raceTime);
	}

	public String getName() {
		return this.name;
	}
}