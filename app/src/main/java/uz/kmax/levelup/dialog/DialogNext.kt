package uz.kmax.levelup.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import uz.kmax.levelup.databinding.DialogLevelUpBinding

class DialogNext {

    private var okClickListener : (()-> Unit)? = null
    fun setOkListener(f: ()-> Unit){ okClickListener = f }

    fun show(context : Context){
        val dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val binding = DialogLevelUpBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        binding.next.setOnClickListener {
            dialog.dismiss()
            okClickListener?.invoke()
        }
        dialog.show()
    }
}