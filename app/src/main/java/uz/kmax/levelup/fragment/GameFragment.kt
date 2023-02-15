package uz.kmax.levelup.fragment

import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.levelup.databinding.FragmentGameBinding
import uz.kmax.levelup.dialog.DialogNext
import uz.kmax.levelup.memory.SharedHelper

class GameFragment : BaseFragmentWC<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private var count : Int = 0
    private var maxCount = 100
    private var level : Int = 1
    private var step : Int = 1
    private var mInterstitialAd: InterstitialAd? = null
    private var adRequest : AdRequest? = null
    private var dialogNext = DialogNext()
    private lateinit var shared : SharedHelper

    override fun onViewCreated() {
        shared = SharedHelper(requireContext())

        level = shared.getLevelCount()
        count = shared.getLevelViewCount()
        binding.level.text = "LEVEL $level"
        binding.countProgress.text = count.toString()
        binding.progressBar.progress = count

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MobileAds.initialize(requireContext()) {}
        val adView = AdView(requireContext())
        adRequest = AdRequest.Builder().build()
        adView.adUnitId = "ca-app-pub-4664801446868642/3547674791"
        binding.adView.loadAd(adRequest!!)

        binding.tapTap.setOnClickListener {
            onClick()
            check()
        }
    }

    private fun onClick(){
        binding.countView.visibility = View.VISIBLE
        count += step
        binding.progressBar.progress = count
        binding.countView.visibility = View.INVISIBLE
        binding.countProgress.text = "$count"
    }

    private fun check(){
        if (count >= maxCount){
            ++level
            count = 0
            binding.progressBar.progress = count
            binding.level.text = "Level $level"
            binding.countProgress.text = count.toString()
            dialogNext.show(requireContext())
            dialogNext.setOkListener {
                if (mInterstitialAd != null){
                    mInterstitialAd?.show(requireActivity())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        InterstitialAd.load(requireContext(),"ca-app-pub-4664801446868642/9346796384", adRequest!!, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                //Toast.makeText(requireContext(), "Ad Not Loaded !", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                //Toast.makeText(requireContext(), "Ad Loaded !", Toast.LENGTH_SHORT).show()
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Toast.makeText(requireContext(), "Thank You !!!", Toast.LENGTH_SHORT).show()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
                count = 0
                binding.progressBar.progress = count
                binding.countProgress.text = count.toString()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.

            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        shared.setLeveCount(level)
        shared.setLeveViewCount(count)
    }

    override fun onPause() {
        super.onPause()
        shared.setLeveCount(level)
        shared.setLeveViewCount(count)
    }
}