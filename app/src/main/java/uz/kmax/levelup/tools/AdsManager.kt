package uz.kmax.levelup.tools

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.appodeal.ads.Appodeal
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdsManager() {

    lateinit var adRequest: AdRequest
    private var onAdDismissClickListener : (()-> Unit)? = null
    fun setOnAdDismissClickListener(f: ()-> Unit){ onAdDismissClickListener = f }

    private var onAdsClickedListener : (()-> Unit)? = null
    fun setOnAdsClickListener(f: ()-> Unit){ onAdsClickedListener = f }

    private var onAdsNotReadyListener : ((type : Int)-> Unit)? = null
    fun setOnAdsNotReadyListener(f: (type : Int)-> Unit){ onAdsNotReadyListener = f }

    private var onAdsNullOrNonNullListener : ((status : Boolean)-> Unit)? = null
    fun setOnAdsNullListener(f : (status : Boolean)-> Unit){
        onAdsNullOrNonNullListener = f
    }

    private var mInterstitialAd: InterstitialAd? = null
    var appodealIsLoaded : Boolean = false

    fun initialize(context: Context){
        MobileAds.initialize(context) {}
        adRequest = AdRequest.Builder().build()
    }

    fun initializeInterstitialAds(context: Context, adUnit : String){
        InterstitialAd.load(context,adUnit, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                onAdsNullOrNonNullListener?.invoke(false)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                onAdsNullOrNonNullListener?.invoke(true)
                callbackInterstitialAds()
            }
        })
    }

    fun initializeAppodeal(activity: Activity,context: Context,appKey : String){
        Appodeal.initialize(
            activity,
            appKey,
            Appodeal.REWARDED_VIDEO,
            object : ApdInitializationCallback {
                override fun onInitializationFinished(list: List<ApdInitializationError>?) {
                    //Appodeal initialization finished
                    callbackAppodealInterstitialAds()
                }
            })
    }

    private fun callbackInterstitialAds(){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                onAdsClickedListener?.invoke()
            }
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                onAdDismissClickListener?.invoke()
            }
            override fun onAdImpression() {}
            override fun onAdShowedFullScreenContent() {}
        }
    }

    private fun callbackAppodealInterstitialAds(){
        Appodeal.setRewardedVideoCallbacks(object : RewardedVideoCallbacks {
            override fun onRewardedVideoLoaded(isPrecache: Boolean) {
                // Called when rewarded video is loaded
                appodealIsLoaded = true
            }
            override fun onRewardedVideoFailedToLoad() {
                // Called when rewarded video failed to load
                appodealIsLoaded = false
            }
            override fun onRewardedVideoShown() {
                // Called when rewarded video is shown
            }
            override fun onRewardedVideoShowFailed() {
                // Called when rewarded video show failed
            }
            override fun onRewardedVideoClicked() {
                // Called when rewarded video is clicked
            }
            override fun onRewardedVideoFinished(amount: Double, currency: String?) {
                // Called when rewarded video is viewed until the end
            }
            override fun onRewardedVideoClosed(finished: Boolean) {
                // Called when rewarded video is closed
            }
            override fun onRewardedVideoExpired() {
                // Called when rewarded video is expired
            }
        })
    }

    fun showInterstitialAds(activity: Activity){
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            onAdsNotReadyListener?.invoke(1)
        }
    }

    fun initializeBanner(adView: AdView){
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {}
            override fun onAdClosed() {}
            override fun onAdFailedToLoad(adError : LoadAdError) {}
            override fun onAdImpression() {}
            override fun onAdLoaded() {}
            override fun onAdOpened() {}
        }
    }

    fun showAppodealAds(activity: Activity){
        if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
            Appodeal.show(activity, Appodeal.REWARDED_VIDEO)
        }else{
            onAdsNotReadyListener?.invoke(2)
        }
    }
}