package com.tencent.tga.liveplugin.poptv.utilk

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import com.tencent.tga.liveplugin.base.util.DeviceUtils

class AnimUtilK{
    companion object {
        fun showOperateAnimation(iv: ImageView, context: Context): Unit {
            val anim_period = 1000
            val anim_reverse_delay = 9000

            var alphaAnimation = AlphaAnimation (0f, 1.0f)
            alphaAnimation.duration = anim_period.toLong()

            val translateAnimation = TranslateAnimation(DeviceUtils.getScreenWidth(context).toFloat(), 0f, 0f, 0f)
            translateAnimation.duration = anim_period.toLong()

            var alphaAnimation2 = AlphaAnimation(1.0f,0f)
            alphaAnimation2.startOffset = anim_reverse_delay.toLong()
            alphaAnimation2.duration = anim_period.toLong()

            var translateAnimation2 = TranslateAnimation(0f,-DeviceUtils.getScreenWidth(context).toFloat(),  0f, 0f)
            translateAnimation2.startOffset = anim_reverse_delay.toLong()
            translateAnimation2.duration = anim_period.toLong()

            var animationSet = AnimationSet(true)
            animationSet.addAnimation(alphaAnimation)
            animationSet.addAnimation(translateAnimation)
            animationSet.addAnimation(alphaAnimation2)
            animationSet.addAnimation(translateAnimation2)
            animationSet.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {
                    Log.e("showOperateAnimation","onAnimationRepeat......")
                }

                override fun onAnimationEnd(p0: Animation?) {
                    Log.e("showOperateAnimation","onAnimationEnd......")
                    iv.visibility = View.GONE
                }

                override fun onAnimationStart(p0: Animation?) {
                    Log.e("showOperateAnimation","onAnimationStart......")
                }
            })
            iv.startAnimation(animationSet)
        }
    }
}