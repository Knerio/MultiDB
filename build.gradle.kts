import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.8"
}



allprojects {
    plugins.apply("java")
    plugins.apply("maven-publish")
    plugins.apply("java-library")
    plugins.apply("io.github.goooler.shadow")

    group = "de.derioo.multidb"
    version = "${project.property("project_version")}"

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.mongodb:bson:5.5.0")

        implementation("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")
        compileOnly("org.projectlombok:lombok:1.18.38")
        testImplementation("org.projectlombok:lombok:1.18.38")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

        implementation("org.jetbrains:annotations:26.0.2")

        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
        testImplementation("org.slf4j:slf4j-jdk14:2.0.17")
        testImplementation("org.assertj:assertj-core:3.27.3")
        testImplementation("org.mongodb:bson:5.5.0")
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
    }

    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allJava)
    }

    publishing {
        repositories {
            maven {
                name = "Reposilite"
                url = uri("https://repo.derioo.de/releases")
                credentials {
                    username = System.getenv("REPOSILITE_USER")
                    password = System.getenv("REPOSILITE_TOKEN")
                }
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                groupId = "$group"
                version = "$version"
                artifact(tasks.named<ShadowJar>("shadowJar").get())
                artifact(tasks.named<Jar>("sourcesJar").get())
            }
        }
    }

    tasks.named("publishGprPublicationToMavenLocal") {
        dependsOn(tasks.named("jar"))
        mustRunAfter(tasks.named("jar"))
        dependsOn(tasks.named("shadowJar"))
        mustRunAfter(tasks.named("shadowJar"))
    }

    tasks {
        shadowJar {
            archiveClassifier.set("")
        }
        test {
            useJUnitPlatform()
        }
        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(21)
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

}