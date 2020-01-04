import java.util.Properties
import java.io.File

rootProject.name = "MyHomeIot"
include(":app", ":core")
injectLocalProperties("local.properties")


/**
 * Load properties from file and add/override properties for all project
 *
 * @param propertiesPath - path to properties
 */
fun injectLocalProperties(propertiesPath: String, skipIfNotExist: Boolean = true) {
    //TODO add logs
    val file = File(propertiesPath)
    if (skipIfNotExist && !file.exists()) return
    val reader = file.reader()
    val properties = Properties()
    reader.use { properties.load(it) }
    gradle.beforeProject {
        properties.stringPropertyNames().forEach { this.extra.set(it, properties[it]) }
    }
}
