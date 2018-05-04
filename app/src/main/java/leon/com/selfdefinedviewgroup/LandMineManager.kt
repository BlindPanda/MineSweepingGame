package leon.com.selfdefinedviewgroup

import android.util.Log
import android.util.SparseIntArray
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by liyl9 on 2018/5/4.
 */
class LandMineManager(val count:Int,val diffcultStep:Int) {
    var bomecontainer = SparseIntArray(count/diffcultStep)
    var surroundBomeCount = SparseIntArray(count/diffcultStep)

    companion object {
        private var instance: LandMineManager? = null
        fun getInstance(count: Int, diffcultStep: Int): LandMineManager {
            if (instance == null) {
                synchronized(LandMineManager::class.java) {
                    if (instance == null) {
                        instance = LandMineManager(count, diffcultStep)
                    }
                }
            }
            if (instance!!.diffcultStep == diffcultStep) {
                return instance!!
            } else {
                synchronized(LandMineManager::class.java) {
                    instance = LandMineManager(count, diffcultStep)
                }

            }
            return instance!!
        }
    }
    init{
        createBome()
    }
    fun createBome() {
        var random = Random(System.currentTimeMillis())
        var index = 0
        Log.d("yanlonglong","bomecontainer.size():"+bomecontainer.size())
        while (index < ((count/diffcultStep))) {
            var bomeIndex = random.nextDouble() * count
            while(bomecontainer.indexOfValue(bomeIndex .toInt())>=0){
                // 已经存在则重新生成，直到没有存在过
                bomeIndex = random.nextDouble() * count
            }
            bomecontainer.put(index,bomeIndex.toInt())
            index ++
        }
    }
    fun getBomeContainer():SparseIntArray{
        return bomecontainer
    }

    fun setEachLineCount(eachLineCount: Int,totalLineCount:Int) {
        //caculate childview around bome count
        thread (start = true){
            var index = 0
            while (index<count){
                val leftBomeCount = if(index%eachLineCount==0)0 else (if(bomecontainer.indexOfValue(index-1)>=0)1 else 0)
                val rightBomeCount = if((index+1)%(eachLineCount)==0)0 else (if(bomecontainer.indexOfValue(index+1)>=0)1 else 0)
                val topBomeCount = if(index/eachLineCount>0)(if(bomecontainer.indexOfValue(index-eachLineCount)>=0)1 else 0)else 0
                val bottomBomeCount = if(index+eachLineCount<count)(if(bomecontainer.indexOfValue(index+eachLineCount)>=0)1 else 0)else 0
                Log.d("yanlonglong","index:"+index+"lefBomeCount: "+leftBomeCount+" rightBomeCount:"+rightBomeCount+" topBomeCount:"+topBomeCount+" bottomBomeCount"+bottomBomeCount+" surround bomecount:"+(leftBomeCount+rightBomeCount+topBomeCount+bottomBomeCount))
                surroundBomeCount.put(index,leftBomeCount+rightBomeCount+topBomeCount+bottomBomeCount)
                index++
            }

        }
    }

}