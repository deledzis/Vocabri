import com.vocabri.applySpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applySpotless()
        }
    }
}
