dependencies {
    api(project(":api"))

    api("org.mongodb:mongodb-driver-sync:5.3.0")
    testImplementation("org.mongodb:mongodb-driver-sync:5.3.0")
}

tasks {
    jar {
        dependsOn(project(":api").tasks.shadowJar)
    }
    compileJava {
        dependsOn(project(":api").tasks.shadowJar)
    }
}