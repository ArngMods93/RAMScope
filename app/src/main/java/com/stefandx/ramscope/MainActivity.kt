package com.stefandx.ramscope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.stefandx.ramscope.data.MemoryInfoProvider
import com.stefandx.ramscope.ui.navigation.RAMScopeNavHost
import com.stefandx.ramscope.ui.theme.RAMScopeTheme

/**
 * Single-activity host. All navigation happens inside Compose via
 * [RAMScopeNavHost] — MainActivity itself holds no business logic, per the
 * project's architecture guidelines.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val memoryInfoProvider = MemoryInfoProvider(applicationContext)

        setContent {
            RAMScopeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RAMScopeNavHost(memoryInfoProvider)
                }
            }
        }
    }
}
