package org.genshin.scrollninja;

import java.awt.RenderingHints.Key;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;

// 制作メモ
// 10/2 制作開始
// 10/3 変数と空の関数を実装
// 		ジャンプと移動だけ先に明日実装！
// 10/4 ジャンプと移動は実装完了だけど実行してない
//		明日アニメーション関連進行。今週までに表示までいきたい
// 10/8 移動だけ動作確認。段差のところで空中移行になってるのを直そう
//		重力弱いから要調整。ジャンプできてねー＾ｑ＾

// *メモ*
// 攻撃はダッシュしながら攻撃可能（足は止まらない）
// 右クリック押しっぱなしで伸び続ける
// 壁とかに付いた後も押しっぱでそっちに移動
// 壁とかに付いた状態で離すとブラーン
// もう一回右クリックで離す

//========================================
// クラス宣言
//========================================
public class Player extends CharacterBase {

	// 定数宣言
	private static final float FIRST_SPEED	=  30f;		// 初速度
	private static final float JUMP_POWER	=  30f;		// ジャンプ加速度
	private static final float GRAVITY		=  -20f;	// 重力
	private Vector2 velocity;							// 移動用速度

	private static final int RIGHT			=  1;
	private static final int LEFT			= -1;
	private static final int STAND			=  0;
	private static final int WALK			=  1;
	private static final int DASH			=  2;
	private static final int JUMP			=  3;
	private static final int ATTACK			=  4;

	// 変数宣言
	private String			name;					// 名前
	private int				charge;					// チャージゲージ
	private int				money;					// お金
	private int				direction;				// 向いてる方向
	private int				currentState;			// 現在の状態
	private int				nowAttack;				// 現在の攻撃方法
	private int				count;					// カウント用変数
	private float			fall;					// 落下量
	private float			stateTime;
	private Weapon			weapon;					// 武器のポインタ
	private boolean			jump;					// ジャンプフラグ
	private Animation		standAnimation;			// 立ちアニメーション
	private Animation		walkAnimation;			// 歩きアニメーション
	private Animation		dashAnimation;			// ダッシュアニメーション
	private Animation		jumpAnimation;			// ジャンプアニメーション
	private Animation		attackAnimation;		// 攻撃アニメーション
	private Animation		footWalkAnimation;		// 下半身・歩きアニメーション
	private Sprite			footSprite;				// 下半身用のスプライト
	private TextureRegion[]	frame;					// アニメーションのコマ
	private TextureRegion	nowFrame;				// 現在のコマ
	private TextureRegion	nowFootFrame;			// 下半身用の現在のコマ

	//************************************************************
	// Get
	// ゲッターまとめ
	//************************************************************
	public String GetName(){ return name; }
	public int GetDirection(){ return direction; }
	public Sprite GetSprite(String type) {
		if (type.equals("BODY"))
			return sprite;
		else
			return footSprite;
	}

	// コンストラクタ
	public Player(String Name) {
		velocity = new Vector2(0, 0);

		// テクスチャの読み込み
		Texture texture = new Texture(Gdx.files.internal("data/player.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// アニメーション
		TextureRegion[][] tmp = TextureRegion.split(texture, 64, 64);

		// 下半身・歩き １行目６フレーム
		frame = new TextureRegion[6];
		int index = 0;
		for (int i = 0; i < frame.length; i++)
			frame[index++] = tmp[0][i];
		footWalkAnimation = new Animation(5.0f, frame);

		// 上半身・歩き　２行目６フレーム
		frame = new TextureRegion[6];
		index = 0;
		for (int i = 0; i < frame.length; i++)
			frame[index++] = tmp[1][i];
		walkAnimation = new Animation(5.0f, frame);

		// スプライトに反映 最初は立ちの第１フレーム
		// （※現在は用意されていないので歩きの第１フレームで代用）
		sprite = new Sprite(walkAnimation.getKeyFrame(0, true));
		sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
		sprite.setScale(0.1f);
		footSprite = new Sprite(footWalkAnimation.getKeyFrame(0, true));
		footSprite.setOrigin(footSprite.getWidth() * 0.5f, footSprite.getHeight() * 0.5f);
		footSprite.setScale(0.1f);

		// 一番最初の表示　現在は歩きで代用
		nowFrame = walkAnimation.getKeyFrame(0, true);
		nowFootFrame = footWalkAnimation.getKeyFrame(0, true);

		name		 = Name;
		charge		 = 0;
		money		 = 0;
		direction	 = 1;
		currentState = STAND;
		fall		 = 0;
		count		 = 0;
//		weapon		 = WeaponManager.GetInstace().GetWeapon("");
		jump		 = false;

		EffectManager.CreateEffect(Effect.FIRE_2, this);
		nowAttack = Effect.FIRE_2;
	}

	//************************************************************
	// Update
	// 更新処理はここにまとめる
	//************************************************************
	public void Update(World world) {
		position = body.getPosition();
		body.setTransform(position ,0);
		sprite.setRegion(nowFrame);
		footSprite.setRegion(nowFootFrame);

		Stand(world);		// 立ち処理
		Move(world);		// 移動処理
		Jump(world);		// ジャンプ処理
		Attack();
		Gravity(world);		// 重力計算処理
		animation();		// アニメーション処理

	/*	if( prevAngle != body.getAngle() ) {
			body.setTransform(position ,0);
		}
	*/
		body.setTransform(position, body.getAngle());
	//	prevAngle = body.getAngle();
	//	System.out.println(currentState);
	}

	//************************************************************
	// Draw
	// 描画処理はここでまとめる
	//************************************************************
	public void Draw(SpriteBatch batch) {
		// 下半身から描画
		footSprite.draw(batch);
		sprite.draw(batch);
	}

	//************************************************************
	// Stand
	// 立ち処理。
	//************************************************************
	private void Stand(World world) {
		if( GetGroundJudge(world) ) {
			currentState = STAND;
		}
	}
	//************************************************************
	// Jump
	// ジャンプ処理。上押すとジャンプ！
	//************************************************************
	private void Jump(World world) {


		// 地面に接触しているならジャンプ可能
		if( /*GetGroundJudge(world)*/ !jump ) {
			// 上押したらジャンプ！
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				jump = true;
				currentState = JUMP;
				velocity.y = JUMP_POWER;
			}
		}

		// ジャンプ中の処理
		if( jump ) {
			body.setLinearVelocity(velocity.x, velocity.y);
			velocity.y -= 1;
		}
	}

	//************************************************************
	// Gravity
	// 重力計算処理。常にやってます
	//************************************************************
	private void Gravity(World world) {
		// 空中にいる時は落下移動
		if(!GetGroundJudge(world)) {
			/*
			fall -= 0.25;
			position.y -= 5;
			if( fall < -5 ) {
				fall = -5;
			}
			*/
		}
	}

	//************************************************************
	// Move
	// 移動処理。左右押すと移動します
	// 状態遷移は空中にいなければ歩きに！
	//************************************************************
	private void Move(World world) {
		// 右が押された
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			direction = RIGHT;				// プレイヤーの向きを変更。
			velocity.x = FIRST_SPEED * RIGHT;
			body.setLinearVelocity(velocity.x, GRAVITY);		// プレイヤーの移動
			sprite.setScale(-0.1f, 0.1f);
			footSprite.setScale(-0.1f, 0.1f);

			if( GetGroundJudge(world) ) {	// もし地面なら歩くモーションにするので現在の状態を歩きに。
				currentState = WALK;
			}
		}
		// 左が押された
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			direction = LEFT;
			velocity.x = FIRST_SPEED * LEFT;
			body.setLinearVelocity(velocity.x, GRAVITY);		// プレイヤーの移動
			sprite.setScale(0.1f, 0.1f);
			footSprite.setScale(0.1f, 0.1f);

			if( GetGroundJudge(world) ) {
				currentState = WALK;
			}
		}
		// 移動キーが押されていない時は少しずつ減速
		if (!Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT)) {
			velocity.x *= 0.9;
			if (velocity.x < 5)
				velocity.x = 0;
			body.setLinearVelocity(velocity.x, GRAVITY);
		}
	}

	//************************************************************
	// Attack
	// 攻撃処理。左クリックで攻撃
	//************************************************************
	private void Attack() {
		if(Gdx.input.isKeyPressed(Keys.Z)) {
			currentState = ATTACK;

			switch(nowAttack) {
			case Effect.FIRE_1:
				break;
			case Effect.FIRE_2:
				EffectManager.GetEffect(Effect.FIRE_2).SetUseFlag(true);
				break;
			}
		}
	}

	// カギ縄
	private void Kaginawa(){}

	//************************************************************
	// animation
	// 現在の状態を参照して画像を更新
	//************************************************************
	private void animation() {
		switch(currentState) {
		case STAND:		// 立ち
			break;
		case WALK:		// 歩き
			nowFrame = walkAnimation.getKeyFrame(stateTime, true);
			nowFootFrame = footWalkAnimation.getKeyFrame(stateTime, true);
			stateTime ++;
			break;
		case DASH:		// 走り
			break;
		case JUMP:		// ジャンプ
			break;
		case ATTACK:
			count ++;
			break;
		}
	}

	// 武器変更
	private void changeWeapon() {
	}

	//************************************************************
	// GetGroundJudge
	// 戻り値： true:地面接地		false:空中
	// 接触判定。長いのでここで関数化
	//************************************************************
	private boolean GetGroundJudge(World world) {
		List<Contact> contactList = world.getContactList();

		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);

			// 地面に当たったよ
			for( int j = 0; j < Background.GetBody().getFixtureList().size(); j ++) {
				if(contact.isTouching() &&
						(( contact.getFixtureA() == sensor && contact.getFixtureB() == Background.GetSensor(j) ) ||
						( contact.getFixtureA() == Background.GetSensor(j) && contact.getFixtureB() == sensor ))) {
					jump = false;
					fall = 0;
					//System.out.println("地面！");
					return true;
				}
			}
		}
		return false;
	}

	//************************************************************
	// ExceptionProcess
	// 当たり判定の例外処理
	//************************************************************
/*	private boolean ExceptionProcess(World world) {
		List<Contact> contactList = world.getContactList();

		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);

			for(int j = 0; j < EffectManager.GetListSize(); j++ ) {
				if(contact.isTouching() && ( contact.getFixtureA() == sensor || contact.getFixtureB() == sensor) &&
						( contact.getFixtureA() == EffectManager.GetEffectForLoop(j).GetSensor() || contact.getFixtureB() == EffectManager.GetEffectForLoop(j).GetSensor() )) {
					return true;
				}
			}
		}

		return false;
	}*/
}
