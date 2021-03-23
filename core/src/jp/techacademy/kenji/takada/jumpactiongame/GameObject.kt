package jp.techacademy.kenji.takada.jumpactiongame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

//GameObjectClassはSpriteClassを継承している

open class GameObject
(texture: Texture, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int)
    : Sprite(texture, srcX, srcY, srcWidth, srcHeight) {

    //Vector2ClassをPropertyに持つClass
    //x方向、y方向の速度を保持する
    val velocity: Vector2

    //Constructorで　Vector2Classであるvelocityを初期化
    //座標などxとyを持つ場合に利用　x方向の速度と、y方向の速度を保持する
    init {
        velocity = Vector2()
    }

}