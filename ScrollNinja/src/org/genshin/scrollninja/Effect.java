//******************************
//	Effect.java 
//******************************

package org.genshin.scrollninja;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;


public class Effect {

	//変数宣言
	private int 		texture;		// テクスチャー
	private String 	name;			// 名前
	private int 		effectTime;	// 効果時間

	//コンストラクタ
	public Effect()
	{
		this.name       = "";
		this.effectTime = 0;
	}
		
	

	//アニメーション
	public void EffectAnimation()
	{
		//-------------------------
		// 画像切り替え
		//-------------------------
		
	}
	
	public int GetEffectTime()
	{
		return this.effectTime;
	}
	
	// 
	public void play()
	{
		
	}
	
}

