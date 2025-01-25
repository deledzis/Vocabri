import com.vocabri.applyKover
import org.gradle.api.Plugin
import org.gradle.api.Project

class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyKover()
        }
    }
}
