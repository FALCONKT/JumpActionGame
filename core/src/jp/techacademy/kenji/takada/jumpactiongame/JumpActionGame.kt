package jp.techacademy.kenji.takada.jumpactiongame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// 継承元ClassをGameClassにgdlのGameを指定
// Android側のAndroidLauncherを使用し、広告を表示するために 仮置き的にActivityRequestHandlerとする
// ActivityRequestHandler　Interface　の　Method　を使用
// 引数にConstructorを追加
// Core側ではAndroid側のClassを知ることが出来ないため　引数には　ActivityRequestHandler
class JumpActionGame(val mRequestHandler: ActivityRequestHandler) : Game() {
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()

        //GameScrennを表示する
        setScreen(GameScreen(this))
    }

}
