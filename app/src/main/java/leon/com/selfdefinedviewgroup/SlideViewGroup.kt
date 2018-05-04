package leon.com.selfdefinedviewgroup

import android.content.Context
import android.graphics.Color
import android.support.annotation.IntegerRes
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Scroller


/**
 * Created by liyl9 on 2018/4/20.
 */
class SlideViewGroup @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var mScroller: Scroller? = null
    private var mScreenHeight: Int = 0
    private var _diffcultLevel:Int = 10
    val listener =  object:ChildViewClicked{
        override fun onChildClicked(view: SelfView) {
            clickedChildView = view
        }

    }

    init {
        mScroller =  Scroller(context)
        mScreenHeight = getScreenSize(context).heightPixels
        addChildsInGourpView(200)
    }
    var landMine:LandMineManager? = null
    private fun addChildsInGourpView(count: Int) {
        var index = 0
        //难度系数可调整 10-5 10最简单，5最难
        landMine = LandMineManager.getInstance(count,_diffcultLevel)
        val bomeContainer = landMine!!.getBomeContainer()
        while (index<count) {
            val isBome = bomeContainer.indexOfValue(index)>=0
            val view = SelfView(context,index,isBome,listener, landMine!!)
            view.setBackgroundColor(Color.BLUE)
            addView(view)
            index++
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        Log.d("yanlonglong","onMeasure: widthSize"+widthSize+" heightSize:"+heightSize)
        //子 view 固定高度
        val childWithSpec = MeasureSpec.makeMeasureSpec(100,MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(100,MeasureSpec.EXACTLY)
        val eachLineCount = widthSize/(100+10)  //10px right padding
        val totalLineCount = childCount/eachLineCount+(if(childCount%eachLineCount>0)1 else 0)
        Log.d("yanlonglong","onMeasure : childCount"+childCount+" eachLineCount:"+eachLineCount+" totalLineCount"+totalLineCount)
        var index = 0
        while (index<childCount){
            getChildAt(index).measure(childWithSpec,childHeightSpec)
            index++
        }
        heightSize = 10+totalLineCount*(100+10) //+padding
        Log.d("yanlonglong","onMeasure end: widthSize"+widthSize+" heightSize:"+heightSize)
        setMeasuredDimension(widthSize,heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d("yanlonglong","onLayout: l"+l+" r:"+r+" t:"+t+" b:"+b)
        val eachLineCount = (r-l)/(100+10)
        Log.d("yanlonglong","onLayout: eachLineCount"+eachLineCount)
        val totalLineCount = childCount/eachLineCount+(if(eachLineCount%eachLineCount>0)1 else 0)
        landMine!!.setEachLineCount(eachLineCount,totalLineCount)
        var index = 0
        val leftRightPading = ((r-l)-eachLineCount*(100+10))/2
        var left = leftRightPading
        var top = 10
        while (index<childCount){
            getChildAt(index).layout(left,top,left+100,top+100)
            index++
            left = leftRightPading+(index%eachLineCount)*(100+10)
            top = 10+(index/eachLineCount)*(100+10)
        }

    }

    override fun computeScroll() {
        super.computeScroll()
        Log.d("yanlonglong", "mScroller.getCurrY() " + mScroller!!.getCurrY())
        if (mScroller!!.computeScrollOffset()) {//是否已经滚动完成
            scrollTo(0, mScroller!!.getCurrY())//获取当前值，startScroll（）初始化后，调用就能获取区间值
            postInvalidate()
        }
    }

    private fun canScroll(): Boolean {
        if (childCount != 0) {
            Log.d("yanlonglong", "canScroll = " + height+" measuredHeight:"+measuredHeight)
            return height < measuredHeight
        }
        return false
    }
    private var originDownY: Float = 0.toFloat()
    private var lastDownY: Float = 0.toFloat()
    private var mScrollEnd: Float = 0.toFloat()
    private var downTimeMillis: Long = 0.toLong()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("yanlonglong", "onTouchEvent down ")
                lastDownY = event.y
                originDownY = lastDownY
                downTimeMillis = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("yanlonglong", "onTouchEvent move ")
                val currentY = event.y
                var dy: Float
                dy = lastDownY - currentY
                Log.d("yanlonglong", "onTouchEvent move dy: "+dy)
                Log.d("yanlonglong", "onTouchEvent move scrollY: "+scrollY)
                Log.d("yanlonglong", "onTouchEvent move measuredHeight - mScreenHeight+getHeightLocationInWindow(): "+(measuredHeight - mScreenHeight+getHeightLocationInWindow()))
                if (scrollY < 0) {
                    dy = 0f
                    //最顶端，超过0时，不再下拉，要是不设置这个，getScrollY一直是负数
                    //                    setScrollY(0);
                } else if (scrollY > measuredHeight - mScreenHeight+getHeightLocationInWindow()) {
                    dy = 0f
                    //滑到最底端时，不再滑动，要是不设置这个，getScrollY一直是大于getHeight() - mScreenHeight的数，无法再滑动
                    //                    setScrollY(getHeight() - mScreenHeight);
                }
                Log.d("yanlonglong", "onTouchEvent move scrollby dy: "+dy.toInt())
                scrollBy(0, dy.toInt())
                //不断的设置Y，在滑动的时候子view就会比较顺畅
                lastDownY = event.y
            }
            MotionEvent.ACTION_UP -> {
                Log.d("yanlonglong", "onTouchEvent up ")
                Log.d("yanlonglong", "onTouchEvent move scrollY: "+scrollY)
                Log.d("yanlonglong", "onTouchEvent move measuredHeight - mScreenHeight+getHeightLocationInWindow(): "+(measuredHeight - mScreenHeight+getHeightLocationInWindow()))
                mScrollEnd = scrollY.toFloat()
                if (mScrollEnd < 0) {// 最顶端：手指向下滑动，回到初始位置
                    mScroller!!.startScroll(0, scrollY, 0, -scrollY)
                } else if (mScrollEnd > measuredHeight - mScreenHeight+getHeightLocationInWindow()) {//已经到最底端，手指向上滑动回到底部位置
                    mScroller!!.startScroll(0, scrollY, 0, measuredHeight - mScreenHeight +getHeightLocationInWindow() - mScrollEnd.toInt())
                }
                postInvalidate()// 重绘执行computeScroll()
                if(Math.abs(event.y-originDownY)<5){
                    if(clickedChildView!=null) {
                        if(downTimeMillis!=0.toLong()) {
                            clickedChildView!!.callOnClick(System.currentTimeMillis() - downTimeMillis)
                        }
                        downTimeMillis = -1
                        clickedChildView = null
                    }
                }
            }
        }
        return true//需要返回true否则down后无法执行move和up操作
    }

    /**
     * 获取屏幕大小，这个可以用一个常量不用每次都获取
     *
     * @param context
     * @return
     */
   private fun getScreenSize(context: Context): DisplayMetrics {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        return metrics
    }
    private fun getHeightLocationInScreen():Int{
        var int_array1:IntArray = intArrayOf(0, 0)
        getLocationOnScreen(int_array1)
        return int_array1[1]
    }
    private fun getHeightLocationInWindow():Int{
        var int_array2:IntArray = intArrayOf(0, 0)

        getLocationInWindow(int_array2)
        return int_array2[1]
    }

     var clickedChildView: SelfView? =null
    // 扫雷游戏重新开始一局
    fun reInitial() {
        removeAllViews()
        addChildsInGourpView(200)
    }

    fun setDiffcultLevel(diffcultLevel:Int) {
        _diffcultLevel = diffcultLevel
        reInitial()
    }


}