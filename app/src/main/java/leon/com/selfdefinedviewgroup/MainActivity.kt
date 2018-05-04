package leon.com.selfdefinedviewgroup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val diffLevel = resources.getIntArray(R.array.spingarrvalue)

        restart.setOnClickListener { self_viewgroup.reInitial() }

        diff_spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("yanlonglong","select:"+position+" id:"+id)
                self_viewgroup.setDiffcultLevel(diffLevel[position])
            }

        }


    }
}
