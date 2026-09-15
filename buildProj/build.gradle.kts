plugins {
    id("com.gradleup.shadow").version("9.4.0")
}

buildscript {
    dependencies {
        classpath("org.yaml:snakeyaml:2.0")
    }
}

dependencies {
    implementation(project(":common"))
    // 只打 1.21.9+ 与 26.x：manifest 用 mojang 命名空间，1.21.9 之前的模块即使打进去也用不了
    // （见下方 shadowJar 的注释）。老版本模块已从 settings.gradle.kts 移除。
    implementation(project(":implementation:v1_21_R6"))
    implementation(project(":implementation:v1_21_R7"))
    implementation(project(":implementation:v26_1"))
    implementation(project(":implementation:v26_2"))
    implementation("com.github.AvarionMC:yaml:1.1.7")
	
	if(project.hasProperty("includeSpigot")){
		//Also change the one in shadowJar. Remember to have --remapped in Buildtools.
		implementation(project(":implementation:Spigotv1_21_R6"))
		implementation(project(":implementation:Spigotv1_21_R7"))
        implementation(project(":implementation:Spigotv26_1"))
        implementation(project(":implementation:Spigotv26_2"))
	}
}

tasks.shadowJar {
    //This will break all versions before 1.21.9.
    // Can't do much about that.
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }

	//Make the spigot build shadow itself
	if(project.hasProperty("includeSpigot")){
		dependsOn(":implementation:Spigotv1_21_R6:remap")
		dependsOn(":implementation:Spigotv1_21_R7:remap")
	}
	
    doFirst {
        val yamlFile = file("${rootProject.projectDir}/common/src/main/resources/plugin.yml")
        val yaml = org.yaml.snakeyaml.Yaml()
        val config = yaml.load<Map<String, Any>>(yamlFile.inputStream())

        // Set the archive name and version based on the plugin.yml file
        archiveBaseName.set(config["name"].toString())
        archiveVersion.set(config["version"].toString())
        archiveClassifier.set("") // Don't add the '-all' postfix.
    }

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

    relocate("io.papermc.lib", "org.terraform.lib")
}

tasks.register<Copy>("deploy") {
    dependsOn(tasks.named("shadowJar"))

    from(layout.buildDirectory.dir("libs"))
    include("*.jar")
    into(rootProject.projectDir)

    doNotTrackState("Disable state tracking due to file access issues")
}