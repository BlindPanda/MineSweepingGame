package leon.com.selfdefinedviewgroup

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

/**
 * Created by liyl9 on 2018/5/3.
 */
class SelfView(context: Context?) : View(context) {
    var _index: Int = -1
    var _childClickedListener: ChildViewClicked? = null
    var isClickedChildView = false
    var isLongClickedChildView = false
    var isBome = false
    var hasClicked = false
    var _landMine:LandMineManager?=null
    var _numberTextSize:Float = 18.toFloat()
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d("yanlonglong", "SelfView dispatchTouchEvent _" + _index)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("yanlonglong", "SelfView dispatchTouchEvent down ")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("yanlonglong", "SelfView dispatchTouchEvent down ")
            }
            MotionEvent.ACTION_UP -> {
                Log.d("yanlonglong", "SelfView dispatchTouchEvent up ")
            }
        }
        _childClickedListener!!.onChildClicked(this)
        return false
    }
    fun callOnClick(clickTime:Long){
        Log.d("yanlonglong", "SelfView callOnClick _index: " + _index+" clickTime"+clickTime)
        if(!hasClicked) {//只让点一次
            if (clickTime > 350) {
                isLongClickedChildView = true
                isClickedChildView = false
            } else {
                isLongClickedChildView = false
                isClickedChildView = true
            }
            if(isBome&&isClickedChildView){
                Toast.makeText(context,"啊哦~，你踩到雷了！！",Toast.LENGTH_SHORT).show()
            }
            if(isBome&&isLongClickedChildView){
                Toast.makeText(context,"牛掰~，成功扫掉一颗雷！！",Toast.LENGTH_SHORT).show()
            }
            invalidate()
            callOnClick()
            hasClicked = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        if(isClickedChildView) {
            paint.color = Color.RED
           // val rect = Rect(0,0,width,height)
            //canvas!!.drawRect(rect,paint)
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = _numberTextSize
            if(_landMine!=null){
                val surroundBome = _landMine!!.surroundBomeCount.get(_index)
                canvas!!.drawText(surroundBome.toString(),(width/2).toFloat(),((height)/2+_numberTextSize/2).toFloat(),paint)
            }
        }

        if(isLongClickedChildView){
            val bomeIcon = BitmapFactory.decodeResource(resources,R.drawable.saoleiwin)
            val srcRect = Rect(0,0,bomeIcon.width,bomeIcon.height)
            val dstRect = Rect(0,0,width,height)
            canvas!!.drawBitmap(bomeIcon,srcRect,dstRect,paint)
        }
        if(isBome&&isClickedChildView){
          val bomeIcon = BitmapFactory.decodeResource(resources,R.drawable.saoleifail)
            val srcRect = Rect(0,0,bomeIcon.width,bomeIcon.height)
            val dstRect = Rect(0,0,width,height)
            canvas!!.drawBitmap(bomeIcon,srcRect,dstRect,paint)
        }
    }

    constructor(context: Context, index: Int) : this(context) {
        _index = index
    }

    constructor(context: Context, index: Int, listener: ChildViewClicked?) : this(context) {
        _index = index
        _childClickedListener = listener
    }

    constructor(context: Context?, index: Int, bome: Boolean, listener: ChildViewClicked,landMine: LandMineManager) : this(context){
        _index = index
        _childClickedListener = listener
        isBome = bome
        _landMine = landMine
        val mScale = context!!.getResources().displayMetrics.density
        if (mScale != 1f) {
            _numberTextSize *= mScale.toInt()
        }
    }

}
