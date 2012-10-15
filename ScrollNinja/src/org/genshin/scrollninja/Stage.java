

package org.genshin.scrollninja;

import java.util.ArrayList;
import java.util.Vector;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Stage implements StageBase {
	private Player 						player;			// プレイヤー
	private World						world;			// 当たり判定
	private OrthographicCamera			camera;			// カメラ
	private SpriteBatch					spriteBatch;	// スプライトバッチ
	private Box2DDebugRenderer			renderer;		//
	private ArrayList<Item>				popItems;		//
	private ArrayList<Enemy>			popEnemys;		//

	// コンストラクタ
	public Stage(World wrd){
		world = new World(new Vector2(0.0f, -20.0f), true );
		world = wrd;
		/*
		switch (ScroolNinja.aspectRatio) {
		case XGA:	// 4:3
			camera = new OrthographicCamera(10.0f * 0.1f, 7.5f * 0.1f);
			break;
		case HD:	// 16:9
			camera = new OrthographicCamera(10.0f * 0.1f, 5.625f * 0.1f);
			break;
		case SXGA:	// 5:4
			camera = new OrthographicCamera(10.0f * 0.1f, 8.0f * 0.1f);
			break;
		case WUXGA:	// 16:10
			camera = new OrthographicCamera(10.0f * 0.1f, 6.25f * 0.1f);
			break;
		}
		*/
		camera				= new OrthographicCamera(ScrollNinja.window.x * 0.1f, ScrollNinja.window.y * 0.1f);
		spriteBatch 		= new SpriteBatch();
		renderer			= new Box2DDebugRenderer();

		CreateStage();
		CreateStageObject();
		EnemyManager.CreateEnemy("1", 0, 100.0f, 50.0f);
		CreatePlayer();
	}

	//************************************************************
	// Update
	// 更新処理まとめ
	//************************************************************
	public void Update() {
		player.GetSprite("BODY").setPosition(player.GetPosition().x - 32, player.GetPosition().y - 32);
		player.GetSprite("BODY").setRotation((float) (player.GetBody().getAngle()*180/Math.PI));
		player.GetSprite("FOOT").setPosition(player.GetPosition().x - 32, player.GetPosition().y - 32);
		player.GetSprite("FOOT").setRotation((float) (player.GetBody().getAngle()*180/Math.PI));
		EnemyManager.Update(world);

		// 背景スクロール
		Background.moveBackground(player);
		camera.position.set(Background.GetCamPos().x , Background.GetCamPos().y , 0);

		camera.update();
		player.Update(world);

		for(int i = 0; i< EffectManager.GetListSize(); i ++) {
			EffectManager.GetEffectForLoop(i).Update();
		}
	}

	//************************************************************
	// Draw
	// 描画処理まとめ
	//************************************************************
	public void Draw() {
		// 全部クリア
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(camera.combined);		// プロジェクション行列のセット
		spriteBatch.begin();									// 描画開始
		{
			Background.GetSprite(0).draw(spriteBatch);
			Background.GetSprite(1).draw(spriteBatch);
			StageObjectManager.GetStageObject("block").GetSprite().draw(spriteBatch);
			player.Draw(spriteBatch);
			EnemyManager.GetEnemy("1").GetSprite().draw(spriteBatch);
			EffectManager.GetEffect(Effect.FIRE_2).Draw(spriteBatch);
		}
		spriteBatch.end();										// 描画終了

		renderer.render(world, camera.combined);
		world.step(Gdx.graphics.getDeltaTime(), 20, 20);
		player.GetBody().setAwake(true);
	}

	//************************************************************
	// CreateStage
	// ステージのあたり判定の作成
	//************************************************************
	private void CreateStage() {
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/test.json"));

		// ボディタイプ設定
		BodyDef bd	= new BodyDef();
		bd.type		= BodyType.StaticBody;		// 動かない物体
		// -357.5は（2048-1333）÷２　（画像サイズ-実際に描かれているサイズ）=空白　空白は上下にあるので÷２
		bd.position.set(-Background.GetSprite(1).getWidth() * 0.5f * 0.1f,
								(-Background.GetSprite(1).getHeight() * 0.5f -357.5f) * 0.1f);

		// ボディ設定
		FixtureDef fd	= new FixtureDef();
		fd.density		= 1000;		// 密度
		fd.friction		= 100;		// 摩擦
		fd.restitution	= 0;		// 反発係数

		// ボディ作成
		Background.SetBody(world.createBody(bd));
		loader.attachFixture( Background.GetBody(), "bgTest", fd, Background.GetSprite(1).getWidth() * 0.1f);

		for(int i = 0; i < Background.GetBody().getFixtureList().size(); i ++) {
			Background.SetFixture( Background.GetBody().getFixtureList().get(i));
		}
//		System.out.println(Background.GetBody().getFixtureList().size());
	}

	//************************************************************
	// CreatePlayer
	// プレイヤーの作成
	//************************************************************
	private void CreatePlayer() {
		PlayerManager.CreatePlayer("プレイヤー");
		player = PlayerManager.GetPlayer("プレイヤー");

		BodyDef def	= new BodyDef();
		def.type	= BodyType.DynamicBody;		// 動く物体
		player.SetBody(world.createBody(def));

		// 当たり判定の作成
		PolygonShape poly		= new PolygonShape();
		poly.setAsBox(1.6f, 2.4f);

		// ボディ設定
		FixtureDef fd	= new FixtureDef();
		fd.density		= 50;
		fd.friction		= 0;
		fd.restitution	= 0;
		fd.shape		= poly;

		//
		player.GetBody().createFixture(fd);
		player.SetFixture(player.GetBody().createFixture(poly, 0));
		player.GetBody().setBullet(true);			// すり抜け防止
		player.GetBody().setFixedRotation(true);	// シミュレーションでの自動回転をしない
		player.GetBody().setTransform(0, 3, 0);	// 初期位置

		// とりあえず
		EnemyManager.GetEnemy("1").SetBody(world.createBody(def));
		EnemyManager.GetEnemy("1").GetBody().createFixture(fd);
		EnemyManager.GetEnemy("1").SetFixture(EnemyManager.GetEnemy("1").GetBody().createFixture(poly, 0));
		poly.dispose();
		EnemyManager.GetEnemy("1").GetBody().setBullet(true);
		EnemyManager.GetEnemy("1").GetBody().setTransform(50, 10, 0);
	}

	//************************************************************
	// CreateStageObject
	// ステージオブジェクトの作成
	//************************************************************
	private void CreateStageObject() {
		// 当たり判定読み込み
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/stageObject.json"));

		// Bodyのタイプを設定 Staticは動かない物体
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;

		// Bodyの設定を設定
		FixtureDef fd	= new FixtureDef();
		fd.density		= 1000;				// 密度
		fd.friction		= 0;				// 摩擦
		fd.restitution	= 0;				// 反発係数

		// ステージオブジェクトの作成
		StageObjectManager.CreateStageObject("block");
		StageObjectManager.GetStageObject("block").SetBody(world.createBody(bd));

		// 各種設定を適用。引数は　Body、JSON中身のどのデータを使うか、FixtureDef、サイズ
		loader.attachFixture(StageObjectManager.GetStageObject("block").GetBody(), "gravestone", fd,
								StageObjectManager.GetStageObject("block").GetSprite().getWidth() * 0.1f);
		// とりあえず。後で調整
		StageObjectManager.GetStageObject("block").GetBody().setTransform
							(-StageObjectManager.GetStageObject("block").GetSprite().getWidth() * 0.5f,
							-StageObjectManager.GetStageObject("block").GetSprite().getHeight() * 0.5f, 0);
	}

	//************************************************************
	// CreateEnemy
	// 敵の作成
	//************************************************************
	public void CreateEnemy() {
		BodyDef def	= new BodyDef();
		def.type	= BodyType.DynamicBody;		// 動く物体
		player.SetBody(world.createBody(def));

		// 当たり判定の作成
		PolygonShape poly		= new PolygonShape();
		poly.setAsBox(1.6f, 2.4f);

		// ボディ設定
		FixtureDef fd	= new FixtureDef();
		fd.density		= 50;
		fd.friction		= 0;
		fd.restitution	= 0;
		fd.shape		= poly;

		//
		player.GetBody().createFixture(fd);
		player.SetFixture(player.GetBody().createFixture(poly, 0));
		player.GetBody().setBullet(true);			// すり抜け防止
		player.GetBody().setTransform(0, 30, 0);	// 初期位置

		// とりあえず
		EnemyManager.GetEnemy("1").SetBody(world.createBody(def));
		EnemyManager.GetEnemy("1").GetBody().createFixture(fd);
		EnemyManager.GetEnemy("1").SetFixture(EnemyManager.GetEnemy("1").GetBody().createFixture(poly, 0));
		poly.dispose();
		EnemyManager.GetEnemy("1").GetBody().setBullet(true);
		EnemyManager.GetEnemy("1").GetBody().setTransform(0, 30, 0);
	}

	//************************************************************
	// PopEnemy
	// 敵の出現タイミングの設定
	//************************************************************
	public void PopEnemy(Player player) {
		if( player.GetPosition().x > 200 ) {
			EnemyManager.CreateEnemy("1", 0, 20.0f, 30.0f);
		}
	}

	// アイテムポップ
	public void popItem() {

	}

	public void moveBackground() {

	}

	public Player spawnPlayer(Player player) {
		return player;
	}

	public int timeCount(int nowTime) {
		// 制限時間 or 経過時間加算
		nowTime += 1;

		return nowTime;
	}

	//************************************************************
	// Get
	// ゲッターまとめ
	//************************************************************
	public Stage GetStage() { return this; }

	@Override
	public void Init() {
	}

	@Override
	public void Release() {
	}

	//************************************************************
	// Set
	// セッターまとめ
	//************************************************************

}
