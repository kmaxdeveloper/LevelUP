package uz.kmax.levelup.fragment

import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import uz.kmax.base.basefragment.BaseFragmentBinding
import uz.kmax.base.basefragment.BaseFragmentWC
import uz.kmax.levelup.databinding.FragmentSplashBinding

class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    override fun onViewCreated() {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        object : CountDownTimer(4000, 100) {
            override fun onFinish() {
                startMainFragment(GameFragment())
            }
            override fun onTick(value: Long) {}
        }.start()
    }
}