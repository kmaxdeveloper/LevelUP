package uz.kmax.levelup.fragment

import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.levelup.databinding.FragmentGameBinding
import uz.kmax.levelup.dialog.DialogNext
import uz.kmax.levelup.memory.SharedHelper
import java.util.Objects

class GameFragment : BaseFragmentWC<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private var count: Int = 0
    private var maxCount = 100
    private var level: Int = 1
    private var step: Int = 1
    var adType = false
    private var mInterstitialAd: InterstitialAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var adRequest: AdRequest? = null
    private var dialogNext = DialogNext()
    private lateinit var shared: SharedHelper
    private var rewardedAd: RewardedAd? = null
    var isLoading: Boolean = true
    var rewardedIsLoading = true

    override fun onViewCreated() {
        shared = SharedHelper(requireContext())

        level = shared.getLevelCount()
        count = shared.getLevelViewCount()
        binding.level.text = "LEVEL $level"
        binding.countProgress.text = count.toString()
        binding.progressBar.progress = count

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        MobileAds.initialize(requireContext()) {}

        val adView = AdView(requireContext())
        adRequest = AdRequest.Builder().build()
        adView.adUnitId = "ca-app-pub-4664801446868642/3547674791"
        binding.adView.loadAd(adRequest!!)

        binding.tapTap.setOnClickListener {
            onClick()
            check()
        }

        boostClick()
    }

    private fun boostClick() {
        binding.autoClicker.setOnClickListener {
            if (!rewardedIsLoading) {
                rewardedInterstitialAd?.let { ad ->
                    ad.show(
                        requireActivity(),
                        OnUserEarnedRewardListener {
                            auto()
                        }
                    )
                }
            } else if (!isLoading) {
                rewardedAd?.let { ad ->
                    adType = true
                    ad.show(requireActivity(), OnUserEarnedRewardListener { rewardItem ->
                        auto()
                    })
                } ?: run {}
            }
        }

        binding.boost1.setOnClickListener {
            if (!rewardedIsLoading) {
                rewardedInterstitialAd?.let { ad ->
                    adType = false
                    ad.show(requireActivity(),
                        OnUserEarnedRewardListener {})
                }
            } else if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
            } else {
                Toast.makeText(requireContext(), "Ads Not Loaded !", Toast.LENGTH_SHORT).show()
            }
        }

        binding.boost2.setOnClickListener {
            if (!isLoading) {
                rewardedAd?.let { ad ->
                    adType = false
                    ad.show(requireActivity(), OnUserEarnedRewardListener {})
                } ?: run {}
            } else if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
            } else {
                Toast.makeText(requireContext(), "Ads Not Loaded !", Toast.LENGTH_SHORT).show()
            }
        }

        binding.boost3.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
            } else {
                Toast.makeText(requireContext(), "Ads Not Loaded !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClick() {
        binding.countView.visibility = View.VISIBLE
        count += step
        binding.progressBar.progress = count
        binding.countView.visibility = View.INVISIBLE
        binding.countProgress.text = "$count"
    }

    private fun check() {
        if (count >= maxCount) {
            ++level
            binding.level.text = "Level $level"
            memoryLeak()
            dialogNext.show(requireContext())
            dialogNext.setOkListener {
                if (!rewardedIsLoading) {
                    rewardedInterstitialAd?.let { ad ->
                        adType = false
                        ad.show(
                            requireActivity(),
                            OnUserEarnedRewardListener {
                                adType = false
                            })
                    }
                } else if (!isLoading) {
                    rewardedAd?.let { ad ->
                        adType = false
                        ad.show(requireActivity(), OnUserEarnedRewardListener {
                            adType = false
                        })
                    } ?: run {}
                } else if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Toast.makeText(requireContext(), "Ads Not Loaded !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val options = ServerSideVerificationOptions.Builder()
            .setCustomData("SAMPLE_CUSTOM_DATA_STRING")
            .build()
        RewardedInterstitialAd.load(requireContext(), "ca-app-pub-3940256099942544/5354046379",
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    rewardedInterstitialAd!!.setServerSideVerificationOptions(options)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedInterstitialAd = null
                }
            })
        /////////////////////////////////////////////////////////////////////////////////
        InterstitialAd.load(requireContext(), "ca-app-pub-4664801446868642/9346796384",
            adRequest!!,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
        /////////////////////////////////////////////////////////////////////////////////
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(),
            "ca-app-pub-4664801446868642/1030886732",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                    rewardedAd!!.setServerSideVerificationOptions(options)
                }
            })

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {}
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                memoryLeak()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                rewardedAd = null
            }

            override fun onAdImpression() {}
            override fun onAdShowedFullScreenContent() {}
        }

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {}
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                memoryLeak()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                mInterstitialAd = null
            }

            override fun onAdImpression() {}
            override fun onAdShowedFullScreenContent() {}
        }

        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {}
            override fun onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null
                memoryLeak()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedInterstitialAd = null
            }

            override fun onAdImpression() {}
            override fun onAdShowedFullScreenContent() {}
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

    fun memoryLeak() {
        count = 0
        binding.progressBar.progress = count
        binding.countProgress.text = count.toString()
        ++level
        binding.level.text = "Level $level"
    }

    private fun autoClicker() {
        object : CountDownTimer(10000, 100) {
            override fun onFinish() {
                binding.autoClicker.visibility = View.VISIBLE
                binding.timerAutoClicker.visibility = View.INVISIBLE
                check()
            }

            override fun onTick(value: Long) {
                val second = value / 1000
                if (second < 10) {
                    binding.timerAutoClicker.text = "00:0$second"
                } else {
                    binding.timerAutoClicker.text = "00:$second"
                }
                onClick()
                check()
            }
        }.start()
    }

    fun auto() {
        Toast.makeText(requireContext(), "AutoClicker On !", Toast.LENGTH_SHORT)
            .show()
        binding.autoClicker.visibility = View.INVISIBLE
        binding.timerAutoClicker.visibility = View.VISIBLE
        autoClicker()
    }
}