package ai.opencode.mobile

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_chat -> loadFragment(ChatFragment())
                R.id.nav_terminal -> loadFragment(TerminalFragment())
                R.id.nav_files -> loadFragment(FileManagerFragment())
                R.id.nav_editor -> loadFragment(CodeEditorFragment())
            }
            true
        }
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(ChatFragment())
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear sensitive data from memory
        bottomNavigation.setOnItemSelectedListener(null)
    }
}
