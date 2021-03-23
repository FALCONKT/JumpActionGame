package jp.techacademy.kenji.takada.jumpactiongame

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

//このClassはAndroid側のため
//Core側のActivityRequestHandler　経由で読み込む
//このCoreは感知される
//ActivityRequestHandlerIntefaceのMethodはlibGDX側のThreadから呼び出される
//、Handlerを利用してMainThreadでAdViewの表示非表示を切り替える
class AndroidLauncher : AndroidApplication(), ActivityRequestHandler {

    //AdMobのViewはAdViewClass　Propertyとして定義
    private lateinit var mAdView: AdView

    private val SHOW_ADS = 1
    private val HIDE_ADS = 0

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SHOW_ADS -> mAdView.visibility = View.VISIBLE
                HIDE_ADS -> mAdView.visibility = View.GONE
            }
        }
    }

    //onCreateMethod　で　libGDXのView　と　AdMobのViewを生成
    //onCreateMethodの最後でshowAds(false)　として　AdMobの表示を高速化

    //onCreate　MethodでObjectを作成し、
    //setAdSize(AdSize.BANNER) で BannerTypeの広告として設定
    //setAdUnitId Methodでstring.xmlに定義した広告IDを設定
    //AdRequest　Classを作成し、loadAd　Methodで設定することで広告を読み込み表示
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = AndroidApplicationConfiguration()
        val gameView = initializeForView(JumpActionGame(this), config)

        mAdView = AdView(this)
        mAdView.adSize = AdSize.BANNER
        mAdView.adUnitId = resources.getString(R.string.banner_ad_unit_id)
        mAdView.visibility = View.INVISIBLE
        mAdView.setBackgroundColor(Color.BLACK)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        // RelativeLayoutでLayoutすることで双方のViewを表示
        //Layout
        val layout = RelativeLayout(this)
        layout.addView(gameView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)

        val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layout.addView(mAdView, params)

        setContentView(layout)

        showAds(false)
    }

    //ActivityRequestHandler　Interfaceの
    //showAdsMethodで表示、非表示を切り替える
    override fun showAds(show: Boolean) {
        if (show) {
            mHandler.sendEmptyMessage(SHOW_ADS)
        } else {
            mHandler.sendEmptyMessage(HIDE_ADS)
        }
    }
}