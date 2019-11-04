package io.dume.dume.teacher.dashboard.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.dume.dume.R
import io.dume.dume.teacher.dashboard.DashboardCompatActivity
import io.dume.dume.teacher.dashboard.DashboardContact
import io.dume.dume.teacher.dashboard.DashboardPresenter
import io.dume.dume.teacher.dashboard.adapters.TutionAdapter
import kotlinx.android.synthetic.main.activity_my_tution.*

class MyTutionActivity : DashboardCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, DashboardContact.View {


    private val presenter = DashboardPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_my_tution)
        super.onCreate(savedInstanceState)
        presenter.enqueue()

    }

    override fun init() {
        setDarkStatusBarIcon()
        bottom_menu.setOnNavigationItemSelectedListener(this)
        bottom_menu.selectedItemId = R.id.my_tution
    }

    override fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        presenter.onBottomMenuClicked(item = item)
        return true
    }

    override fun onResume() {
        super.onResume()
        bottom_menu.setOnNavigationItemSelectedListener(null)
        bottom_menu.selectedItemId = R.id.my_tution
        bottom_menu.setOnNavigationItemSelectedListener(this)

    }

    override fun setupRecycler() {
        tution_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        tution_rv.adapter = TutionAdapter()
    }
}
