package th.co.cdgs.mobile.switchbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import th.co.cdgs.mobile.lib.SwitchButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<SwitchButton>(R.id.sw_activitymain).setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener{
            override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
                Toast.makeText(this@MainActivity, isChecked.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}