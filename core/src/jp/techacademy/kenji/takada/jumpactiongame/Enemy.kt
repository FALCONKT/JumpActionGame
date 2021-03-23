package jp.techacademy.kenji.takada.jumpactiongame

import com.badlogic.gdx.graphics.Texture

//■GameObject継承　XY座標取得するもの
//■type　は　動くか　動かないかの　種別区別
//■type　は継承してきているため変えられない
//■type として　別Class にわたる　　
class Enemy(
        type_e: Int,
        texture: Texture,
        srcX: Int,
        srcY: Int,
        srcWidth: Int,
        srcHeight: Int)
    : GameObject(texture, srcX, srcY, srcWidth, srcHeight) {

    //横幅と高さ、Type_e、状態、速度を定数として定義
    companion object {
        // 横幅、高さ
        // ■　倍率
        val ENEMY_WIDTH = 0.8f
        val ENEMY_HEIGHT = 0.8f

        // Type_e（通常と動くの2種）
        val ENEMY_TYPE_STATIC = 0
        val ENEMY_TYPE_MOVING = 1

//        // 状態（通常と消えた状態）
        //■通常の状態のみ
//            val ENEMY_STATE_NORMAL = 0
//        val ENEMY_STATE_VANISH = 1

        // 速度
        //■　少々早くした　倍率
        val ENEMY_VELOCITY = 1.5f
    }

    // ENEMY_TYPE_MOVING ENEMY_TYPE_STATIC ENEMY_WIDTH
    // Tyoeと状態を保持するPropertyも定義する
//    var mState: Int = 0
   var Type_E: Int

    init {
        setSize(ENEMY_WIDTH, ENEMY_HEIGHT)
        Type_E = type_e
        if (Type_E == ENEMY_TYPE_MOVING) {
            velocity.x = ENEMY_VELOCITY
        }
    }

    // Screen の render Methodから呼ばれて座標の更新を行うための update Method
    // Type_e が STEP_TYPE_MOVINGの場合に速度から座標を計算

    // 座標を更新する
    //■　決まり文句として更新する際のかたまりとして利用
    //■ 左右の画面の外に出てしまった場合戻反対側に戻すための指定
    fun update(deltaTime: Float) {
        if (Type_E == ENEMY_TYPE_MOVING) {

            x += velocity.x * deltaTime

            if (x <  ENEMY_WIDTH / 2) {
                velocity.x = -velocity.x
                x =  ENEMY_WIDTH / 2
            }
            if (x > GameScreen.WORLD_WIDTH -  ENEMY_WIDTH / 2) {
                velocity.x = -velocity.x
                x = GameScreen.WORLD_WIDTH -  ENEMY_WIDTH / 2
            }
        }
    }

//    // 一定の確率でPlayerが踏んだときに呼び出されるvanish Method
//    // setAlphaMethodを引数を0で呼び出すことで透明に
//    // 速度も0に設定  pdateMethodでは座標の計算が行われなくなる
//    //　以降updateMethodを修正した時に意図せず動いてしまうようなことがないように固定する
//
//    // 消える
//    fun vanish() {
//        mState = STEP_STATE_VANISH
//        setAlpha(0f)
//        velocity.x = 0f
//    }

}