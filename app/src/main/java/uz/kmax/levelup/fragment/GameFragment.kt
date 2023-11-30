package uz.kmax.levelup.fragment

import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.levelup.R
import uz.kmax.levelup.databinding.FragmentGameBinding
import uz.kmax.levelup.dialog.DialogNext
import uz.kmax.levelup.tools.SharedHelper
import uz.kmax.levelup.tools.AdsManager


class GameFragment : BaseFragmentWC<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private var adsManager = AdsManager()
    private lateinit var shared: SharedHelper
    private var dialogNext = DialogNext()
    private var count: Int = 0
    private var maxCount = 100
    private var level: Int = 1
    private var step: Int = 1
    private var adType = 0

    override fun onViewCreated() {
        shared = SharedHelper(requireContext())
        adsManager.initialize(requireContext())
        adsManager.initializeBanner(binding.adView)

        adsManager.setOnAdDismissClickListener {
            when (adType) {
                0 -> {
                    Toast.makeText(requireContext(), "Thanks you !", Toast.LENGTH_SHORT).show()
                }

                1 -> {
                    auto()
                }

                2 -> {
                    nextLevel()
                }

                else -> {
                    Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adsManager.setOnAdsClickListener {
            Toast.makeText(requireContext(), "Thank you !", Toast.LENGTH_SHORT).show()
        }

        adsManager.setOnAdsNotReadyListener {
            Toast.makeText(requireContext(), "Ads not ready", Toast.LENGTH_SHORT).show()
        }

        adsManager.setOnAdsNullListener {
            Toast.makeText(requireContext(), "Ads not Ready !", Toast.LENGTH_SHORT).show()
        }


        level = shared.getLevelCount()
        count = shared.getLevelViewCount()
        binding.level.text = "LEVEL $level"
        binding.countProgress.text = count.toString()
        binding.progressBar.progress = count

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.tapTap.setOnClickListener {
            onClick()
            check()
        }

        boostClick()
    }

    private fun boostClick() {
        binding.autoClicker.setOnClickListener {
            adType = 1
            adsManager.showInterstitialAds(requireActivity())
        }

        binding.boost1.setOnClickListener {
            adType = 2
            adsManager.showInterstitialAds(requireActivity())
        }

        binding.boost2.setOnClickListener {
            adType = 2
            adsManager.showInterstitialAds(requireActivity())
        }

        binding.boost3.setOnClickListener {
            adType = 2
            adsManager.showInterstitialAds(requireActivity())
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
            nextLevel()
            dialogNext.show(requireContext())
            dialogNext.setOkListener {
                adType = 0
                adsManager.showInterstitialAds(requireActivity())
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

    private fun nextLevel() {
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
                binding.tapTap.setImageResource(R.drawable.clicker)
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

    override fun onResume() {
        super.onResume()
        adsManager.initializeInterstitialAds(requireContext(), getString(R.string.adInterstitialId))
    }

    private fun auto() {
        Toast.makeText(requireContext(), "AutoClicker Started !", Toast.LENGTH_SHORT)
            .show()
        binding.autoClicker.visibility = View.INVISIBLE
        binding.timerAutoClicker.visibility = View.VISIBLE
        binding.tapTap.setImageResource(R.drawable.auto_clicker_on)
        autoClicker()
    }
}