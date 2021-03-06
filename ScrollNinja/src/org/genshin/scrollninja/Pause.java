package org.genshin.scrollninja;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


/*
 * 10/25 ゲームメインのポーズ処理をこっちで
 * 10/25 キャラクターアニメーション + マップ移動
 * */
// ポーズ中の処理クラス
public class Pause {
	private static boolean drawflag;		// (仮)ワールドマップ表示フラグ
	private Sprite returnGame;				// (仮)コンティニュー文字
	private Sprite title;					// (仮)タイトル文字
	private Sprite load;						// (仮)ロード
	private Sprite pausemenu;				// ポーズメニュー
	private Sprite worldMap;					// ワールドマップ
	private Sprite fireVillage;				// 火の里
	private Sprite waterVillage;			// 水の里
	private Sprite windVillage;				// 風の里
	
	private boolean gotoMain;				// メイン移行フラグ
	private boolean gotoTitleMenu;			// タイトル移行フラグ

	private Sprite foot;
	private Sprite body;
	private Animation walk;
	private Animation footwalk;
	private TextureRegion[] frame;
	private TextureRegion nowFrame;
	private TextureRegion nowFootFrame;
	private int countFrame;
	private float stateTime;
	private ArrayList<Sprite> sprite;
	/**
	 * 10/29
	 * doubleの小数点切り捨てないとキャラがブレる
	 * のでBigDecimal使う。
	 * 始点と終点の間に通りたい座標を出して
	 * moveVecで移動ベクトル出す
	 * ↓2点間の移動のみ
	 * */
	
	// 始点位置
	private BigDecimal 	SRoundX;			// 丸めスタート
	private BigDecimal 	SRoundY;
	private BigDecimal 	SAfterRoundX;		// 小数点切り捨て後
	private BigDecimal 	SAfterRoundY;
	private double 		SbeforeRoundX;	// 小数点切り捨て前
	private double 		SbeforeRoundY;
	
	// 終点位置
	private BigDecimal 	ERoundX;
	private BigDecimal 	ERoundY;
	private BigDecimal 	EAfterRoundX;
	private BigDecimal 	EAfterRoundY;
	private double 		EbeforeRoundX;
	private double 		EbeforeRoundY;
	
	private float 			StartTime;			// 移動時間カウント
	private float 			EndTime;			// 移動にかける時間
	private Vector2 		StartPosition;	// 始点
	private Vector2 		EndPosition;		// 終点
	
	private Vector2 moveVec;					// 移動方向ベクトル
	/**
	 * ベジエ曲線(キャラ移動用)
	 * 4点座標を取り、Sのような曲線移動する
	 * 
	 * 4点でベジェ曲線 or 4点曲線4点曲線のカーブを二つつなげる
	 * */
	private Vector2 		BezierAvec;		// 移動方向ベクトル 点A(火の里)
	private BigDecimal	BezierARxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierARyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierARxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierARyResult;	// 小数点切り捨て用 y
	private double 		BezierARxd;		// double型 x座標
	private double 		BezierARyd;		// double型 y座標
	
	private Vector2 		BezierBvec;		// 移動方向ベクトル 点B
	private BigDecimal	BezierBRxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierBRyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierBRxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierBRyResult;	// 小数点切り捨て用 y
	private double 		BezierBRxd;		// double型 x座標
	private double 		BezierBRyd;		// double型 y座標
	
	private Vector2 		BezierCvec;		// 移動方向ベクトル 点C
	private BigDecimal	BezierCRxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierCRyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierCRxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierCRyResult;	// 小数点切り捨て用 y
	private double 		BezierCRxd;		// double型 x座標
	private double 		BezierCRyd;		// double型 y座標
	
	private Vector2 		BezierDvec;		// 移動方向ベクトル 点D(水の里)
	private BigDecimal	BezierDRxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierDRyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierDRxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierDRyResult;	// 小数点切り捨て用 y
	private double 		BezierDRxd;		// double型 x座標
	private double 		BezierDRyd;		// double型 y座標

	private Vector2		BezierEvec;		// 移動方向ベクトル 点E
	private BigDecimal	BezierERxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierERyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierERxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierERyResult;	// 小数点切り捨て用 y
	private double		BezierERxd;		// double型 x座標
	private double		BezierERyd;		// double型 y座標

	private Vector2		BezierFvec;		// 移動方向ベクトル 点F
	private BigDecimal	BezierFRxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierFRyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierFRxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierFRyResult;	// 小数点切り捨て用 y
	private double		BezierFRxd;		// double型 x座標
	private double		BezierFRyd;		// double型 y座標

	private Vector2		BezierGvec;		// 移動方向ベクトル 点G
	private BigDecimal	BezierGRxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierGRyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierGRxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierGRyResult;	// 小数点切り捨て用 y
	private double		BezierGRxd;		// double型 x座標
	private double		BezierGRyd;		// double型 y座標

	private Vector2		BezierHvec;		// 移動方向ベクトル 点H(風の里)
	private BigDecimal	BezierHRxf;		// 小数点切り捨て後をfloat型へ変換用 x
	private BigDecimal	BezierHRyf;		// 小数点切り捨て後をfloat型へ変換用 y
	private BigDecimal	BezierHRxResult;	// 小数点切り捨て用 x
	private BigDecimal	BezierHRyResult;	// 小数点切り捨て用 y
	private double		BezierHRxd;		// double型 x座標
	private double		BezierHRyd;		// double型 y座標

	// draw curv2D bezier float
	private float			BezAx;
	private float			BezAy;
	private float			BezBx;
	private float			BezBy;
	private float			BezCx;
	private float			BezCy;
	private float			BezDx;
	private float			BezDy;
	
	private boolean WtoF;		// 水から火へ移動するフラグ
	private static int villageState;
	private final static int FIRE  = 0;
	private final static int WATER = 1;
	private final static int WIND  = 2;
	
	private Graphics g;
	private Graphics2D g2;
	
	
	// コンストラクタ
	public Pause() {
		// ワールドマップ
		Texture worldMaptexture = new Texture(Gdx.files.internal("data/map.png"));
		worldMaptexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion worldRegion = new TextureRegion(worldMaptexture,0,0,1024,1024);
		worldMap = new Sprite(worldRegion);
		worldMap.setScale(ScrollNinja.scale * 1.7f);
		TextureRegion fireRegion = new TextureRegion(worldMaptexture,0,512,256,256);
		fireVillage = new Sprite(fireRegion);
		fireVillage.setScale(ScrollNinja.scale);
		TextureRegion waterRegion = new TextureRegion(worldMaptexture,256,512,256,256);
		waterVillage = new Sprite(waterRegion);
		waterVillage.setScale(ScrollNinja.scale);
		TextureRegion windRegion = new TextureRegion(worldMaptexture,512,512,256,256);
		windVillage = new Sprite(windRegion);
		windVillage.setScale(ScrollNinja.scale);
		
		// ポーズ画面中の背景
		Texture pausemenubackTexture = new Texture(Gdx.files.internal("data/pausemenuback.png"));
		pausemenubackTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion pauseRegion = new TextureRegion(pausemenubackTexture);
		pausemenu = new Sprite(pauseRegion);
		pausemenu.setScale(ScrollNinja.scale * 1.5f);

		// ポーズメニュー
		Texture pauseMenuTexture = new Texture(Gdx.files.internal("data/menu.png"));
		pauseMenuTexture.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		TextureRegion returnGameRegion = new TextureRegion(pauseMenuTexture,0,0,256,35);
		returnGame = new Sprite(returnGameRegion);
		returnGame.setScale(ScrollNinja.scale);
		TextureRegion titleRegion = new TextureRegion(pauseMenuTexture,0,40,256,35);
		title = new Sprite(titleRegion);
		title.setScale(ScrollNinja.scale);
		TextureRegion loadRegion = new TextureRegion(pauseMenuTexture,0,85,256,35);
		load = new Sprite(loadRegion);
		load.setScale(ScrollNinja.scale);

		// アニメーション
		Texture texture = new Texture(Gdx.files.internal("data/player.png"));
		texture.setFilter(TextureFilter.Linear,TextureFilter.Linear);
		TextureRegion[][] tmp = TextureRegion.split(texture,64,64);
		frame = new TextureRegion[6];
		int index = 0;
		for(int i = 0 ; i < frame.length;i++)
			frame[index++] = tmp[1][i];
		walk = new Animation(5.0f,frame);

		frame = new TextureRegion[6];
		index = 0;
		for(int i = 0 ; i < frame.length;i++)
			frame[index++] = tmp[0][i];
		footwalk = new Animation(5.0f,frame);

		foot = new Sprite(footwalk.getKeyFrame(0,true));
		foot.setOrigin(foot.getWidth()*0.5f,foot.getHeight()*0.5f-8);
		body = new Sprite(walk.getKeyFrame(0,true));
		body.setOrigin(body.getWidth()*0.5f,body.getHeight()*0.5f-8);
		foot.setScale(ScrollNinja.scale);
		body.setScale(ScrollNinja.scale);

		sprite = new ArrayList<Sprite>();
		sprite.add(foot);
		sprite.add(body);

		nowFrame = walk.getKeyFrame(0,true);
		nowFootFrame = footwalk.getKeyFrame(0,true);

		EndPosition = new Vector2(0,0);
		moveVec = new Vector2(0,0);

		BezierAvec = new Vector2(0,0);
		BezierBvec = new Vector2(0,0);
		BezierCvec = new Vector2(0,0);
		BezierDvec = new Vector2(0,0);
		BezierEvec = new Vector2(0,0);
		BezierFvec = new Vector2(0,0);
		BezierGvec = new Vector2(0,0);
		BezierHvec = new Vector2(0,0);
		
		// ラインを描画するたの
		/*Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		BasicStroke wideStroke = new BasicStroke(4.0f);
		g2.setStroke(wideStroke);*/
		
		StartTime = 0;
		EndTime = 1;
		
		villageState = FIRE;
		
		// フラグ初期化
		gotoMain = false;
		gotoTitleMenu = false;
		drawflag = false;
		WtoF = false;
	}

	// アップデート
	public void update() {
		spriteUpdate();
		clickedUpdate();
		pressedUpdate();
		playerAnimation();
		villageUpdate();
	}

	// 描画
	public void draw() {
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		GameMain.spriteBatch.begin();
		pausemenu.draw(GameMain.spriteBatch);
		if(drawflag) {
			worldMap.draw(GameMain.spriteBatch);
			foot.draw(GameMain.spriteBatch);
			body.draw(GameMain.spriteBatch);

			switch(villageState) {
			case FIRE:
				fireVillage.draw(GameMain.spriteBatch);
				break;
			case WATER:
				waterVillage.draw(GameMain.spriteBatch);
				break;
			case WIND:
				windVillage.draw(GameMain.spriteBatch);
				break;
			}
		}
		returnGame.draw(GameMain.spriteBatch);
		title.draw(GameMain.spriteBatch);
		load.draw(GameMain.spriteBatch);

		GameMain.spriteBatch.end();
	}

	// スプライトアップデート
	public void spriteUpdate() {
		worldMap.setPosition(GameMain.camera.position.x - worldMap.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - worldMap.getWidth()*0.5f*0.1f,
				GameMain.camera.position.y - worldMap.getHeight()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - worldMap.getWidth()*0.5f*0.22f);

		pausemenu.setPosition(GameMain.camera.position.x - pausemenu.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - pausemenu.getWidth()*0.5f*0.12f,
				GameMain.camera.position.y - pausemenu.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - pausemenu.getHeight()*0.5f*0.115f);

		returnGame.setPosition(GameMain.camera.position.x - returnGame.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - returnGame.getWidth()*0.5f*0.1f,
				GameMain.camera.position.y - returnGame.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale)- returnGame.getHeight()*0.5f*0.5f);

		title.setPosition(GameMain.camera.position.x - title.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - title.getWidth()*0.5f*0.1f,
				GameMain.camera.position.y - title.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale)- title.getHeight()*0.5f*0.7f);

		load.setPosition(GameMain.camera.position.x - load.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - load.getWidth()*0.5f*0.1f,
				GameMain.camera.position.y - load.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale)- load.getHeight()*0.5f*0.9f);
		
		fireVillage.setPosition(GameMain.camera.position.x - fireVillage.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - fireVillage.getWidth()*0.5f*0.18f,
				GameMain.camera.position.y - fireVillage.getHeight()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - fireVillage.getWidth()*0.5f*0.65f);
		
		waterVillage.setPosition(GameMain.camera.position.x - waterVillage.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - waterVillage.getWidth()*0.5f*0.43f,
				GameMain.camera.position.y - waterVillage.getHeight()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - waterVillage.getWidth()*0.5f*0.43f);
		
		windVillage.setPosition(GameMain.camera.position.x - windVillage.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - windVillage.getWidth()*0.5f*0.8f,
				GameMain.camera.position.y - windVillage.getHeight()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - windVillage.getWidth()*0.5f*0.39f);
	}

	// クリック時の処理
	public void clickedUpdate() {
		if(Gdx.input.isTouched()) {
			/*マウス座標取得 ウィンドウの中心が原点*/
			float mousePositionX = Gdx.input.getX() - Gdx.graphics.getWidth()*0.5f;
			float mousePositionY =  Gdx.graphics.getHeight()*0.5f - Gdx.input.getY();
			System.out.println("mousePX:"+mousePositionX);
			System.out.println("mousePY:"+mousePositionY);

			// ポーズメニュー中の文字をクリックしたら
			// コンティニュー
			if(mousePositionX > 445 && mousePositionX < 620 &&
				mousePositionY < 282 && mousePositionY > 255) {
				gotoMain = true;
			}
			if(mousePositionX > 450 && mousePositionX < 647 &&
				mousePositionY < 242 && mousePositionY > 215) {}
			if(mousePositionX > 450 && mousePositionX < 656 &&
				mousePositionY < 208 && mousePositionY > 183) {}
		}
	}

	// キーボード押したときの処理
	public void pressedUpdate() {
		if(Gdx.input.isKeyPressed(Keys.L)) {
			gotoMain = true;
		}
		if(Gdx.input.isKeyPressed(Keys.G)) {
			gotoTitleMenu = true;
		}
		if(Gdx.input.isKeyPressed(Keys.V)) {
			drawflag = true;
		}
		if(Gdx.input.isKeyPressed(Keys.B)) {
			drawflag = false;
		}
	}

	// プレイヤーアニメーション
	public void playerAnimation() {
		sprite.get(0).setRegion(nowFrame);
		sprite.get(1).setRegion(nowFootFrame);
		
		if(countFrame > 0)
			countFrame--;
		if(countFrame == 0)
			nowFrame = walk.getKeyFrame(stateTime,true);
		nowFootFrame = footwalk.getKeyFrame(stateTime,true);
		stateTime++;
		
		if(Gdx.input.isKeyPressed(Keys.K)) {
			// 120fまで加算
			if(StartTime <= EndTime) {
				StartTime += 0.01f;
				
				StartPosition.x = BezierAvec.x * ((1-StartTime)*(1-StartTime)*(1-StartTime)) +
									3 * BezierBvec.x * StartTime * ((1-StartTime)*(1-StartTime)) +
									3 * BezierCvec.x * (StartTime * StartTime) * (1-StartTime)+
									 BezierDvec.x * (StartTime * StartTime * StartTime);

				StartPosition.y = BezierAvec.y * ((1-StartTime)*(1-StartTime)*(1-StartTime)) +
									3 * BezierBvec.y * StartTime * ((1-StartTime)*(1-StartTime)) +
									3 * BezierCvec.y * (StartTime * StartTime) * (1-StartTime)+
									 BezierDvec.y * (StartTime * StartTime * StartTime);
				/*　3点ベジェ曲線
				StartPosition.x = (BezierAvec.x * ((1-StartTime)*(1-StartTime))) + 
										(2 * BezierBvec.x * StartTime * (1-StartTime)) +
										(BezierCvec.x * (StartTime * StartTime));
				
				StartPosition.y = (BezierAvec.y * ((1-StartTime)*(1-StartTime))) + 
										(2 * BezierBvec.y * StartTime * (1-StartTime)) +
										(BezierCvec.y * (StartTime * StartTime));
										*/
				if(StartTime >= EndTime)
					villageState = WATER;
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.I)) {
			if(StartTime >= 0) {
				StartPosition.x = BezierAvec.x * ((1-StartTime)*(1-StartTime)*(1-StartTime)) +
									3 * BezierBvec.x * StartTime * ((1-StartTime)*(1-StartTime)) +
									3 * BezierCvec.x * (StartTime * StartTime) * (1-StartTime)+
									 BezierDvec.x * (StartTime * StartTime * StartTime);
				StartPosition.y = BezierAvec.y * ((1-StartTime)*(1-StartTime)*(1-StartTime)) +
									3 * BezierBvec.y * StartTime * ((1-StartTime)*(1-StartTime)) +
									3 * BezierCvec.y * (StartTime * StartTime) * (1-StartTime)+
									 BezierDvec.y * (StartTime * StartTime * StartTime);
				StartTime -= 0.01f;
				if(StartTime <= 0)
					villageState = FIRE;
			}
		}
		//System.out.println(StartPosition.x+"f");
		//System.out.println(StartPosition.y+"f");
		// TODO:クリックでキャラクター移動
		foot.setPosition(StartPosition.x,StartPosition.y);
		body.setPosition(StartPosition.x,StartPosition.y);
	}
	
	public void init() {
		// 小数点切り捨て前
		/*
		SbeforeRoundX = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*0.75f;
		SbeforeRoundY = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.75f;
		SRoundX = new BigDecimal(SbeforeRoundX);
		SRoundY = new BigDecimal(SbeforeRoundY);
		// 小数点第6位まで残す
		SAfterRoundX = SRoundX.setScale(6,BigDecimal.ROUND_DOWN);
		SAfterRoundY = SRoundY.setScale(6,BigDecimal.ROUND_DOWN);
		StartPosition.x = SAfterRoundX.floatValue();
		StartPosition.y = SAfterRoundY.floatValue();
		
		EbeforeRoundX = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*1.75f;
		EbeforeRoundY = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.7f;
		ERoundX = new BigDecimal(EbeforeRoundX);
		ERoundY = new BigDecimal(EbeforeRoundY);
		EAfterRoundX = ERoundX.setScale(6,BigDecimal.ROUND_DOWN);
		EAfterRoundY = ERoundY.setScale(6,BigDecimal.ROUND_DOWN);
		EndPosition.x = EAfterRoundX.floatValue();
		EndPosition.y = EAfterRoundY.floatValue();
		
		moveVec.x = StartPosition.x - EndPosition.x;
		moveVec.y = StartPosition.y - EndPosition.y;
		
		// x秒あたりの移動量 (EndTimeフレームかけて移動する)
		moveVec.x /= EndTime;
		moveVec.y /= EndTime;
		*/
		
		// A
		BezierARxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*0.85f;
		BezierARyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.78f;
		BezierARxResult = new BigDecimal(BezierARxd);
		BezierARyResult = new BigDecimal(BezierARyd);
		BezierARxf = BezierARxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierARyf = BezierARyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierAvec.x = BezierARxf.floatValue();
		BezierAvec.y = BezierARyf.floatValue();
		
		// 初期位置コピー
		StartPosition = BezierAvec.cpy();
		
		// B
		BezierBRxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*2.9f;
		BezierBRyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.8f;
		BezierBRxResult = new BigDecimal(BezierBRxd);
		BezierBRyResult = new BigDecimal(BezierBRyd);
		BezierBRxf = BezierBRxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierBRyf = BezierBRyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierBvec.x = BezierBRxf.floatValue();
		BezierBvec.y = BezierBRyf.floatValue();
		
		// C
		BezierCRxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*0.9f;
		BezierCRyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.1f;
		BezierCRxResult = new BigDecimal(BezierCRxd);
		BezierCRyResult = new BigDecimal(BezierCRyd);
		BezierCRxf = BezierCRxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierCRyf = BezierCRyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierCvec.x = BezierCRxf.floatValue();
		BezierCvec.y = BezierCRyf.floatValue();
		
		// D
		BezierDRxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*1.625f;
		BezierDRyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.0f;
		BezierDRxResult = new BigDecimal(BezierDRxd);
		BezierDRyResult = new BigDecimal(BezierDRyd);
		BezierDRxf = BezierDRxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierDRyf = BezierDRyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierDvec.x = BezierDRxf.floatValue();
		BezierDvec.y = BezierDRyf.floatValue();
		
		//-----------------
		//E～G値未設定
		//-----------------
		// E
		BezierERxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*1.0f;
		BezierERyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.0f;
		BezierERxResult = new BigDecimal(BezierERxd);
		BezierERyResult = new BigDecimal(BezierERyd);
		BezierERxf = BezierERxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierERyf = BezierERyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierEvec.x = BezierERxf.floatValue();
		BezierEvec.y = BezierERyf.floatValue();
		
		// F
		BezierFRxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*1.0f;
		BezierFRyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.0f;
		BezierFRxResult = new BigDecimal(BezierFRxd);
		BezierFRyResult = new BigDecimal(BezierFRyd);
		BezierFRxf = BezierFRxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierFRyf = BezierFRyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierFvec.x = BezierFRxf.floatValue();
		BezierFvec.y = BezierFRyf.floatValue();
		
		// G
		BezierGRxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*1.0f;
		BezierGRyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*1.0f;
		BezierGRxResult = new BigDecimal(BezierGRxd);
		BezierGRyResult = new BigDecimal(BezierGRyd);
		BezierGRxf = BezierGRxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierGRyf = BezierGRyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierGvec.x = BezierGRxf.floatValue();
		BezierGvec.y = BezierGRyf.floatValue();
		
		// H ok
		BezierHRxd = GameMain.camera.position.x - foot.getWidth()*0.5f
				+ (ScrollNinja.window.x*0.5f*ScrollNinja.scale) - foot.getWidth()*0.5f*3.175f;
		BezierHRyd = GameMain.camera.position.y - foot.getHeight()*0.5f
				+ (ScrollNinja.window.y*0.5f*ScrollNinja.scale) - foot.getHeight()*0.5f*0.75f;
		BezierHRxResult = new BigDecimal(BezierHRxd);
		BezierHRyResult = new BigDecimal(BezierHRyd);
		BezierHRxf = BezierHRxResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierHRyf = BezierHRyResult.setScale(6,BigDecimal.ROUND_DOWN);
		BezierHvec.x = BezierHRxf.floatValue();
		BezierHvec.y = BezierHRyf.floatValue();
	}
	
	public void villageUpdate() {
		switch(villageState) {
		case FIRE:
			break;
		case WATER:
			break;
		case WIND:
			break;
		}
	}

	// ゲッター セッター
	public boolean GetgotoMainFlag(){return gotoMain;}
	public void SetgotoMainFlag(boolean flag){gotoMain = flag;}
	public boolean GetgotoTitleFlag(){return gotoTitleMenu;}
	public void SetgotoTitleFlag(boolean flag){gotoTitleMenu = flag;}
}
