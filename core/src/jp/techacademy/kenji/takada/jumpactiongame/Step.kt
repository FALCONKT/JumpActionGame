package jp.techacademy.kenji.takada.jumpactiongame

import com.badlogic.gdx.graphics.Texture

class Step(type: Int, texture: Texture, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int)
    : GameObject(texture, srcX, srcY, srcWidth, srcHeight) {

    //横幅と高さ、Type、状態、速度を定数として定義
    companion object {
        // 横幅、高さ
        val STEP_WIDTH = 2.0f
        val STEP_HEIGHT = 0.5f

        // Type（通常と動くType）
        val STEP_TYPE_STATIC = 0
        val STEP_TYPE_MOVING = 1

        // 状態（通常と消えた状態）
        val STEP_STATE_NORMAL = 0
        val STEP_STATE_VANISH = 1

        // 速度
        val STEP_VELOCITY = 2.0f
    }

    // Tyoeと状態を保持するPropertyも定義する
    var mState: Int = 0
    var mType: Int

    init {
        setSize(STEP_WIDTH, STEP_HEIGHT)
        mType = type
        if (mType == STEP_TYPE_MOVING) {
            velocity.x = STEP_VELOCITY
        }
    }

    // Screen の render Methodから呼ばれて座標の更新を行うための update Method
    // Type が STEP_TYPE_MOVINGの場合に速度から座標を計算

    // 座標を更新する
    fun update(deltaTime: Float) {
        if (mType == STEP_TYPE_MOVING) {
            x += velocity.x * deltaTime

            if (x < STEP_WIDTH / 2) {
                velocity.x = -velocity.x
                x = STEP_WIDTH / 2
            }
            if (x > GameScreen.WORLD_WIDTH - STEP_WIDTH / 2) {
                velocity.x = -velocity.x
                x = GameScreen.WORLD_WIDTH - STEP_WIDTH / 2
            }
        }
    }

    // 一定の確率でPlayerが踏んだときに呼び出されるvanish Method
    // setAlphaMethodを引数を0で呼び出すことで透明に
    // 速度も0に設定  pdateMethodでは座標の計算が行われなくなる
    //　以降updateMethodを修正した時に意図せず動いてしまうようなことがないように固定する

    // 消える
    fun vanish() {
        mState = STEP_STATE_VANISH
        setAlpha(0f)
        velocity.x = 0f
    }
}