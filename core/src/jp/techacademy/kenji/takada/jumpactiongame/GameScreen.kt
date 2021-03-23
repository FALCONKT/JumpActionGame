package jp.techacademy.kenji.takada.jumpactiongame


//import 画像関連　　    画面を描画する前提設定
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.audio

//継承元
import com.badlogic.gdx.ScreenAdapter

import jp.techacademy.kenji.takada.jumpactiongame.JumpActionGame

//import 画像関連    画面を描画する前提設定
import com.badlogic.gdx.graphics.GL20

//Texture Class import　Spriteに張り付ける画像　そのもの
import com.badlogic.gdx.graphics.Texture

//Sprite Class import  画像処理
import com.badlogic.gdx.graphics.g2d.Sprite

//TextureRegion Class import  画像処理
import com.badlogic.gdx.graphics.g2d.TextureRegion

//追加import Camera、ViewPort関連
import com.badlogic.gdx.utils.viewport.FitViewport

//追加import Camera、ViewPort関連
import com.badlogic.gdx.graphics.OrthographicCamera

// 追加　得点表示　関連
import com.badlogic.gdx.graphics.g2d.BitmapFont

// 追記　Toutch Event 関連
import com.badlogic.gdx.math.Rectangle
// 追記　Toutch Event 関連
import com.badlogic.gdx.math.Vector3

//追加　Object配置関連　
import java.util.*

// 追加　HighScoreの保存　関連
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.audio.Sound


//ScreenAdapterClassを継承
//Constructor引数で受け取ったJumpActionGame　Class　Object　を　Propertyに保持
class GameScreen(private val mGame: JumpActionGame) : ScreenAdapter() {

    // 追記　Camera、ViewPort用
    //  Classの先頭でCameraの大きさを表す定数を定義
    companion object {
        val CAMERA_WIDTH = 10f
        val CAMERA_HEIGHT = 15f

        //追加　Object配置関連
        //横幅はCameと同じ10、高さは20画面分設定
        val WORLD_WIDTH = 10f

        //■追加修正　Enemiy関連
        //■実験容易に
        val WORLD_HEIGHT = 15 * 2    // 20画面分登れば終了(仮)　→　2面程度にして調整

        //追加 GUI用のCamera設定　関連
        val GUI_WIDTH = 320f    // 追加
        val GUI_HEIGHT = 480f   // 追加

        // Gameの状態を表す定数も定義
        // Game開始前
        val GAME_STATE_READY = 0
        // Game中
        val GAME_STATE_PLAYING = 1
        // Goal か 落下してGame終了
        val GAME_STATE_GAMEOVER = 2

        // 重力
        // この値を大きくすると重力が強くなる
        val GRAVITY = -12
    }

    private val mBg: Sprite

    //追加　変数宣言　OrthographicCamera用　Property定義
    private val mCamera: OrthographicCamera

    //追加 GUI用のCamera設定　関連
    private val mGuiCamera: OrthographicCamera  //追加

    //追加　変数宣言　FitViewport用　Property定義
    private val mViewPort: FitViewport

    //追加 GUI用のCamera設定　関連
    private val mGuiViewPort: FitViewport   //追加


    //追加　Object配置関連　
    // 乱数（Randomeに生成される数字）を取得するための
    private var mRandom: Random
    // 配置した踏み台を保持するList
    private var mSteps: ArrayList<Step>
    // 配置した★を保持するList
    private var mStars: ArrayList<Star>

    //■追加修正　Enemiy関連
    //■ 配置したEnemyを保持するList　変数宣言のみ！！
    private var mEnemys: ArrayList<Enemy>


    // 配置したUFO（Goal）を保持する
    private lateinit var mUfo: Ufo
    // 生成して配置したPlayerを保持する
    private lateinit var mPlayer: Player
    // Gameの状態を保持する
    private var mGameState: Int


    // 追記　Toutch Event 関連
    //どの高さからPlayerが地面からどれだけ離れたかを保持するmHeightSoFarも合わせて定義
    private var mHeightSoFar: Float = 0f    // 追加
    private var mTouchPoint: Vector3    // 追加

    // 追加　得点表示　関連
    // BitmapFontClassと現在のScore、HighScoreを保持するPropertyを追加
    private var mFont: BitmapFont   //追加
    private var mScore: Int // 追加
    private var mHighScore: Int //追加

    // 追加　HighScoreの保存　関連
    private var mPrefs: Preferences // 追加する


    //■ 効果音変数指定
    //■　敵に当たった時の効果音
    //■　GameOver時の効果音
    val sound_enemy_toutch = Gdx.audio.newSound(Gdx.files.internal("enemy_toutch_re.mp3"))

    val sound_star_get = Gdx.audio.newSound(Gdx.files.internal("star_get.mp3"))

    val sound_ufo_reach = Gdx.audio.newSound(Gdx.files.internal("ufo.mp3"))

//    val sound_jump = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"))

    val sound_step_jump = Gdx.audio.newSound(Gdx.files.internal("step_jump.mp3"))

    val sound_bgm = Gdx.audio.newSound(Gdx.files.internal("bgm_8bit.mp3"))


    init {
        // 背景の前提設定
        val bgTexture = Texture("back.png")
        // TextureRegionClass　Textureとして用意した画像の一部を切り取ってSpriteに貼り付けるためのもの
        // TextureRegionで切り出す時の原点は左上
        mBg = Sprite(TextureRegion(bgTexture, 0, 0, 540, 810))
        // SpriteクラスのsetPositionMethodで描画する位置を指定。左下を基準として位置を指定

        // 追記　Camera、ViewPort用
        mBg.setSize(CAMERA_WIDTH, CAMERA_HEIGHT)

        mBg.setPosition(0f, 0f)


        // 追記　Camera、ViewPort用
        //  Constructor　で　Propertyの初期化をして行く
        //  Cameraの大きさとViewPortの大きさどちらもCAMERA_WIDTHとCAMERA_HEIGHTを使って同じにする
        //　Camera、ViewPortを生成、設定する
        mCamera = OrthographicCamera()
        mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT)
        mViewPort = FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, mCamera)

        //追加 GUI用のCamera設定　関連
        // Constructorでの初期化を行う。
        // 既存のCamera(上記)と同じようにObjectの作成及びSizeの設定、ViewPortの設定を行う
        mGuiCamera = OrthographicCamera()   // 追加
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT) // 追加
        mGuiViewPort = FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera)   // 追加する

        //追加　Object配置関連
        // Constuctorの初期化　
        // Propertyの初期化
        mRandom = Random()
        mSteps = ArrayList<Step>()
        mStars = ArrayList<Star>()

        //■追加修正　Enemiy関連
        // 配置したEnemyを保持するList　変数へ代入する！！
        mEnemys = ArrayList<Enemy>()

        mGameState = GAME_STATE_READY

        //　追加　Toutch Event 関連
        //　Toutchされた座標を保持するPropertymTouchPointを定義して、Constructorで初期化
        mTouchPoint = Vector3() // ←追加

        // 追加　得点表示　関連
        // Constuctorの初期化　
        // FontFileを指定して読み込んだあと、mFont.data.setScale(0.8f)とし、Sizeを小さくする
        // 変数にMethodを実行したものを代入している
        mFont = BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false)   //追加

        mFont.data.setScale(0.8f)   //追加
        mScore = 0  //追加
        mHighScore = 0  //追加


        // 追加　HighScoreの保存　関連
        // HighScoreをPreferencesから取得する
        // 使用は　Package名にする　PCで動かす場合独自な文字列にする必要
        // 取得したPreferencesのgetIntegerMethodにKey与えて値を取得
        mPrefs = Gdx.app.getPreferences("jp.techacademy.taro.kirameki.jumpactiongame") // 追加
        mHighScore = mPrefs.getInteger("HIGHSCORE", 0) // 追加



        //追加　Object配置関連
        //createStageを使用
        createStage()


    }



    //ScreenAdapterを継承したClassのrendod
    override fun render(delta: Float) {

        //それぞれの状態をUpdateする
        update(delta)

        // 画面を描画する前提設定
        // glClearColor　Methodは画面をClearする時の色を赤、緑、青、透過で指定
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        // 画面を描画する前提設定
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)で実際にその色でClear（塗りつぶし）を行い
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // 追記　Camera、移動用
        // Camera中心を超えたらCameraを上に移動させる
        // Charactorが画面の上半分には絶対に行かない
        if (mPlayer.y > mCamera.position.y) { // 追加
            mCamera.position.y = mPlayer.y // 追加
        } // 追加


        // 追記　Camera、ViewPort用
        // Cameraの座標をUpdate（計算）し、Spieteの表示に反映させる
        // updateMethodでは行列演算を行ってCameraの座標値の再計算を行う
        mCamera.update()

        // SpriteBatchClass　の　setProjectionMatrixMethodを
        // OrthographicCameraClass　の　combined　Property　を引数に与えている
        mGame.batch.projectionMatrix = mCamera.combined


        mGame.batch.begin()

        // 背景
        // 原点は左下
        // 追記　Camera、ViewPort用
        mBg.setPosition(mCamera.position.x - CAMERA_WIDTH / 2, mCamera.position.y - CAMERA_HEIGHT / 2)

        // SpriteBatchClassのbegin　MethodとendMethodの間で行う
        mBg.draw(mGame.batch)

        //追加　Object配置関連　
        // renderMethodではそれぞれのObjectの状態（座標など）を
        // Updateするupdate Methodの呼び出しと,Obejctの描画を行います。
        // 踏み台と星はListで保持しているので順番に取り出して描画

        //追加　Object配置関連　
        // Step
        for (i in 0 until mSteps.size) {
            mSteps[i].draw(mGame.batch)
        }

        //追加　Object配置関連　
        // Star
        for (i in 0 until mStars.size) {
            mStars[i].draw(mGame.batch)
        }

        //■追加修正　Enemiy関連
        //■Enemyuの配列の長さ分　配置
        //■Enemy
        for (i in 0 until mEnemys.size) {
            mEnemys[i].draw(mGame.batch)
        }


        //追加　Object配置関連　
        // UFO
        mUfo.draw(mGame.batch)

        //追加　Object配置関連　
        //Player
        mPlayer.draw(mGame.batch)


        mGame.batch.end()


        // 追加　得点表示　関連
        // BitmapFontClassのdrawMethodで描画
        // 通常のCameraと同じようにupdateMethodを呼ぶこと
        // mGame.batch.begin()とmGame.batch.end()内に描画する処理を記述することなど同じ
        // 第1引数にSprteBatch、第2引数に表示されたい文字列、第3引数にx座標、第4引数にy座標を指定
        mGuiCamera.update() // 追加
        mGame.batch.projectionMatrix = mGuiCamera.combined  // 追加
        mGame.batch.begin() // 追加
        mFont.draw(mGame.batch, "HighScore: $mHighScore", 16f, GUI_HEIGHT - 15) // 追加
        mFont.draw(mGame.batch, "Score: $mScore", 16f, GUI_HEIGHT - 35)    // 追加
        mGame.batch.end()   // 追加

    }

    // 追記　Camera、ViewPort用
    // resize　MethodをOverrideしてFitViewportClassのupdateMethodを呼び出し
    // resize Methodは物理的な画面の大きさが変更されたときに呼び出される

    //追加 GUI用のCamera設定　関連
    //resize　Method　2行目に追記
    override fun resize(width: Int, height: Int) {
        mViewPort.update(width, height)

        //追加 GUI用のCamera設定　関連
        mGuiViewPort.update(width, height)
    }


    //■常にBGM再生
    //■音声追加！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    //■1回　変数化しておきえいえんと再生する
    private fun bgm_play() {
//    sound_bgm.play(1.0f)
//    sound_bgm.setLooping(,true)()
        val bgm = sound_bgm.play(0.75f)
        sound_bgm.setLooping(bgm,true)

    }




    //追加　Object配置関連
    //追加 Satageを作成する Method
    //createStage Method は Object を配置する
    //踏み台をPlayerがJumpできる高さ以下の距離を空けつつ配置
    //Satageを作成する
    private fun createStage() {


        // Texturrの準備
        val stepTexture = Texture("step.png")
        val starTexture = Texture("star.png")
        val playerTexture = Texture("uma.png")
        val ufoTexture = Texture("ufo.png")

        //■追加修正　Enemiy関連
        //■画像の配置
        val enemyTexture = Texture("emeny_re.png")


        // StepとStarをGoalの高さまで配置していく

        //■追加修正　Enemiy関連
        //■Enemiyも
        var y = 0f

        // 乱数はmRandom.nextFloat()のように呼び出すと 0.0 から 1.0 までの値が取得
        // 1/2の確率で何か行いたい場合はmRandom.nextFloat() > 0.5のように条件判断

        // ！！Jamp高さの変数定義
        val maxJumpHeight = Player.PLAYER_JUMP_VELOCITY * Player.PLAYER_JUMP_VELOCITY / (2 * -GRAVITY)

        while (y < WORLD_HEIGHT - 5) {
            //!! 踏み台の　種類指定　　Stepは　Class型名
            //!! STEP_TYPE_MOVING STEP_TYPE_STATIC WORLD_WIDTH   は Step Classで定義
            //!! 踏み台のの　発生確率

            val type = if (mRandom.nextFloat() > 0.8f) Step.STEP_TYPE_MOVING else Step.STEP_TYPE_STATIC

            //!!　踏み台の　X座標　乱数*(横幅-Stepの幅)　本体以外の　幅の中で乱的に　X座標が生成される
            val x = mRandom.nextFloat() * (WORLD_WIDTH - Step.STEP_WIDTH)

            //！！踏み台の配置
            val step = Step(type, stepTexture, 0, 0, 144, 36)
            step.setPosition(x, y)
            mSteps.add(step)


            //■ Enemyの　種類指定　Enemyは　Class型名
            //■ ENEMY_TYPE_MOVING ENEMY_TYPE_STATIC ENEMY_WIDTH　　は　EnemyClass で定義
            //■ 動く敵の　発生確率
            val type_e = if (mRandom.nextFloat() > 0.9f) Enemy.ENEMY_TYPE_MOVING else Enemy.ENEMY_TYPE_STATIC

            //■ 敵の　X座標　乱数*(横幅-Enemyの幅)　本体以外の　幅の中で乱的に　X座標が生成される
            val xE = mRandom.nextFloat() * (WORLD_WIDTH - Enemy.ENEMY_WIDTH*2)


            //■追加修正　Enemy
            //■Class経由の　画像　0　0(基準)　本体そのものの実際の幅　本体そのものの実際の高さ　初期値指定
            //■初期場所　設定
            //■配列で指定した要素　を追加
            val enemy = Enemy(type_e, enemyTexture, 0, 0, 148, 158)
            enemy.setPosition(xE*mRandom.nextFloat(),y-mRandom.nextFloat()*4)
            mEnemys.add(enemy)


            if (mRandom.nextFloat() > 0.6f) {
                val star = Star(starTexture, 0, 0, 72, 72)
                star.setPosition(step.x + mRandom.nextFloat(), step.y + Star.STAR_HEIGHT + mRandom.nextFloat() * 3)
                mStars.add(star)
            }

            y += (maxJumpHeight - 0.5f)
            y -= mRandom.nextFloat() * (maxJumpHeight / 3)
        }


        // Playerを配置
        mPlayer = Player(playerTexture, 0, 0, 72, 72)
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.width / 2, Step.STEP_HEIGHT)

        // GoalのUFOを配置
        mUfo = Ufo(ufoTexture, 0, 0, 120, 74)
        mUfo.setPosition(WORLD_WIDTH / 2 - Ufo.UFO_WIDTH / 2, y)

    }

    //追加　Object配置関連
    //追加 Satageを作成する Method
    //最後にupdateMethod。
    //先ずはではGameの状態によって処理を分けられるように準備だけ
    // それぞれのObjectの状態をUpdateする

    //追加　Toutch Event 関連
    //ToutchEventを判断
    //それぞれの状態用のupdate Methodを呼び出す

    private fun update(delta: Float) {
        when (mGameState) {
            GAME_STATE_READY ->
                updateReady()
            GAME_STATE_PLAYING ->
                updatePlaying(delta)
            GAME_STATE_GAMEOVER ->
                updateGameOver()
        }


    }

    //追加　Toutch Event 関連
    // それぞれのupdate Method

    //追加　Toutch Event 関連
    //    Game開始前の updateReady Method
    //    toutchされたら状態をGame中であるGAME_STATE_PLAYINGに変更
    private fun updateReady() {
        if (Gdx.input.justTouched()) {
            mGameState = GAME_STATE_PLAYING


        }
    }


    // 追加　Toutch Event 関連
    // Game中の updatePlaying Method
    // Toutchされていたら、その座標が画面の左側なのか右側なのかを判断
    // toutchされた座標はGdx.input.xとGdx.input.yで取得
    // 値をmTouchPointにset Methodで設定
    private fun updatePlaying(delta: Float) {

        var accel = 0f


        if (Gdx.input.isTouched) {
            // 追加　Toutch Event 関連
            // Vector3Classはx,yだけでなくZ軸を保持するProperty zも持っているため
            // set Methodの第3引数には0fを指定
            // そのmTouchPointをViewports Classのunproject Methodに与えて使用することで
            // Cameraを使った座標に変換
            mViewPort.unproject(mTouchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            // 追加　Toutch Event 関連
            //画面のどこをToutchされたかの判断は
            //RectangleClassの左半分を表す矩形leftと右半分を表す矩形rightを定義

            //追加　Toutch Event 関連　以下通常のCamera使用版
            //val left = Rectangle(0f, 0f, CAMERA_WIDTH / 2, CAMERA_HEIGHT)

            //追加 GUI用のCamera設定　関連
            //ToutchEventを処理している箇所をGUI用のCameraに変更
            val left = Rectangle(0f, 0f, GUI_WIDTH / 2, GUI_HEIGHT) // 修正

            val right = Rectangle(CAMERA_WIDTH / 2, 0f, CAMERA_WIDTH / 2, CAMERA_HEIGHT)

            // 追加　Toutch Event 関連
            // containsMethodにtoutchされた座標を与えることでその領域をtoutchしているのか判断
            // 左側をtoutchされた時は加速度としてaccel = 5.0fと、
            // 右側をtoutchされたときはaccel = -5.0fを設定
            // その加速度をPlayer Class の update Method に与えて使用
            if (left.contains(mTouchPoint.x, mTouchPoint.y)) {
                accel = 5.0f
            }

            if (right.contains(mTouchPoint.x, mTouchPoint.y)) {
                accel = -5.0f
            }
        }

        // 追加　Toutch Event 関連
        // 踏み台の状態も更新させるため Step Class の update Method 使用
        // Playerの座標が0.5以下になった場合は踏み台に乗ったと同じ処理(hitStep Method)を行い、Jumpさせる
        // 追加　Toutch Event 関連
        // Step
        for (i in 0 until mSteps.size) {
            mSteps[i].update(delta)
        }
        // 追加　Toutch Event 関連
        // Player
        if (mPlayer.y <= 0.5f) {
            mPlayer.hitStep()
        }

        // 追加　Toutch Event 関連
        // updatePlaying　Method　の最後は　Player がどれだけ地面から離れたかを、
        // Math Class の max Methopd を呼び出して保持している距離か、今のPlayerの高さか大きい方を保持
        mPlayer.update(delta, accel)
        mHeightSoFar = Math.max(mPlayer.y, mHeightSoFar)

        //追加 当たり判定関連
        // checkCollision()　Method使用
        //当たり判定を行う
        checkCollision() // 追加


        // 追加　GameOver　関連
        // GameOverか判断する
        checkGameOver()
    }

    // 追加　GameOver　関連
    // checkGameOver　Methodの追加
    // Playerの地面との距離であるmHeightSoFarから、
    // Cameraの高さの半分を引いた値よりPlayerの位置が低くなったらGame Overer
    private fun checkGameOver() {
        if (mHeightSoFar - CAMERA_HEIGHT / 2 > mPlayer.y) {
            Gdx.app.log("JampActionGame", "GAMEOVER")
            mGameState = GAME_STATE_GAMEOVER
        }
    }


    // 追加　Toutch Event 関連
    //updateGameOver　Method　未定
    private fun updateGameOver() {

        // 追加　Game終了後画面　関連
        // Game終了後にToutchしたらResultScreenに遷移するように
        //ResultScreen Class を作成した後記載
        if (Gdx.input.justTouched()) {
            mGame.screen = ResultScreen(mGame, mScore)
        }
    }




    // 追加 当たり判定関連
    // 当たり判定　Method定義
    // 当たり判定を行うには
    // 当たり判定を行うobject（sprite）の矩形同士が重なっているかを判断し、
    // 重なっていれば当たっていると判断する
    private fun checkCollision() {

        // 追加 当たり判定関連
        // SpriteClassのboundingRectangle Propertyで
        // spriteの矩形を表すRectangleを取得
        // GameObjectClassを継承していて使用出来る
        // Rectangle Class の overlaps Method に当たり判定を行いたい相手のRectangleを指定

        // 追加 当たり判定関連
        // UFOと当たった場合はGameClearとなるので
        // 状態をGAME_STATE_GAMEOVERにしてMethodを抜ける
        // UFO(ゴールとの当たり判定)
        if (mPlayer.boundingRectangle.overlaps(mUfo.boundingRectangle)) {
            //■　音声追加
            sound_ufo_reach.play(1.5f);
            mGameState = GAME_STATE_GAMEOVER
            return
        }



        // 追加 当たり判定関連
        // 相手となるStarClassのmStateがStar.STAR_NONEの場合は
        // すでに当たって獲得済みとなるなので当たり判定を行わない
        // Starとの当たり判定
        for (i in 0 until mStars.size) {
            val star = mStars[i]

            if (star.mState == Star.STAR_NONE) {
                continue
            }



            if (mPlayer.boundingRectangle.overlaps(star.boundingRectangle)) {
                star.get()

                //■　音声追加
                sound_star_get.play(1.5f);

                // 追加　得点表示　関連
                // 得点をCountUpする処理、HighScoreを保持する処理を記述
                // 星に触れた時にCountUpする　checkCollision　Methodを修正
                // 星と当たり判定を行い当たったときにmScoreに1足す
                //　mHighScoreと比較し、現在のスコアの方が大きければmHighScoreに現在のスコアを代入
                mScore++    //追加
                if (mScore > mHighScore) {  // 追加
                    mHighScore = mScore     // 追加

                    // 追加　HighScoreの保存　関連
                    //checkCollision　Metho　でPreferencesにHighScoreを保存
                    // putIntegerMethodで第1引数にKey、第2引数に値を指定
                    // そのあとflushMethodを呼び出すことで実際に値を永続化
                    // HighScoreをPreferenceに保存する
                    mPrefs.putInteger("HIGHSCORE", mHighScore)  //追加
                    mPrefs.flush()  // ←追加する

                }   // 追加
                break
            }
        }

        // 追加 当たり判定関連
        // 踏み台との当たり判定は
        // Playerが上昇中＝mPlayer.velocity.y > 0の時は行わない
        // Stepとの当たり判定
        // 上昇中はStepとの当たり判定を確認しない
        for (i in 0 until mSteps.size) {
            val step = mSteps[i]

            if (step.mState == Step.STEP_STATE_VANISH) {
                continue
            }

            // 追加 当たり判定関連
            // 踏み台との当たり判定はstep.mState == Step.STEP_STATE_VANISH以外のもので判定を行う
            // 踏み台と当たった場合はmRandom.nextFloat() > 0.5fで判断して、つまり1/2の確率で踏み台を消す
            if (mPlayer.y > step.y) {



                if (mPlayer.velocity.y > 0) {
                    return
                }

                if (mPlayer.boundingRectangle.overlaps(step.boundingRectangle)) {
                    mPlayer.hitStep()
                    //■　音声追加
                    sound_step_jump.play(1.5f);

                    if (mRandom.nextFloat() > 0.5f) {
                        step.vanish()
                    }
                    break
                }
            }
        }




        // ■追加修正　Enemy
        //■ 敵　当たり判定関連
        for (i in 0 until mEnemys.size) {
            val enemy = mEnemys[i]

            //■ 敵　当たり判定関連
            //■ 敵と当たったら跳ねるだけ(演出)
            if (mPlayer.y > enemy.y || mPlayer.y == enemy.y || mPlayer.y < enemy.y) {
                if (mPlayer.boundingRectangle.overlaps(enemy.boundingRectangle)) {
                    mPlayer.hitStep()
                    sound_enemy_toutch.play(1.5f);
                    mGameState = GAME_STATE_GAMEOVER
                    return
                    // ■敵当たり判定関連
                    // ■敵と当たった場合はGameClear
                    // ■状態をGAME_STATE_GAMEOVERにしてMethodを抜ける
//                    break
                }
            }

        }

    }
    //    当たり判定　Method　END



}
//Class END
