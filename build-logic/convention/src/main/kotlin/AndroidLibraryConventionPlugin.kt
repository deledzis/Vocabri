import com.vocabri.applyCommonPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("com.android.library")
            applyCommonPlugins()
        }
    }
}
