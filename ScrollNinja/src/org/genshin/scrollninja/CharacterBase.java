package org.genshin.scrollninja;

//========================================
// インポート
//========================================
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

//========================================
// クラス宣言
//========================================
public abstract class CharacterBase extends ObJectBase {
	// 定数宣言
	protected int	MAX_HP		=	100;

	// 変数宣言
	protected int 		hp;						// HP

	// いずれ消す変数
	protected float		speed;					// 素早さ
	protected Vector2 	position;				// 座標

	// コンストラクタ
	public CharacterBase() {
		super();
		
		hp				= MAX_HP;
		speed			= 0;
		position		= new Vector2(0,0);
	}

	//************************************************************
	// Get
	// ゲッターまとめ
	//************************************************************
	protected int GetHp() { return hp; }
	protected float GetSpeed(){ return speed; }
	protected Vector2 GetPosition(){ return position; }

	//************************************************************
	// Set
	// セッターまとめ
	//************************************************************
	protected void SetHp(int Hp){ hp = Hp; }
	protected void SetSpeed(int Speed){ speed = Speed; }
	protected void SetPosition(Vector2 pos){ position = pos;}
	protected void SetPosition(float x, float y){ position.x = x; position.y = y; }
	protected void SetPositionX(float x){ position.x = x; }
	protected void SetPositionY(float y){ position.y = y; }

}