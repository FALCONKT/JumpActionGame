package jp.techacademy.kenji.takada.jumpactiongame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite


class Star(texture: Texture, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int)
    : Sprite(texture, srcX, srcY, srcWidth, srcHeight) {

    companion object {
        // 横幅、高さ
        val STAR_WIDTH = 0.8f
        val STAR_HEIGHT = 0.8f

        // 状態
        val STAR_EXIST = 0
        val STAR_NONE = 1
    }

    var mState: Int = 0

    //ConstructorでSizeの設定と、状態をSTAR_EXISTと初期設定
    //星は動かないのでupdateMethodは用意しない
    init {
        setSize(STAR_WIDTH, STAR_HEIGHT)
        mState = STAR_EXIST
    }


    //Playerが触れた時に呼ばれるget Methodでは状態をSTAR_NONEにし
    //setAthodで透明に
    fun get() {
        mState = STAR_NONE
        setAlpha(0f)
    }
}