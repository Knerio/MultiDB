dependencies {
    api(project(":api"))

    api("org.mongodb:mongodb-driver-sync:5.2.1")
    testImplementation("org.mongodb:mongodb-driver-sync:5.2.1")
}

tasks {
    jar {
        dependsOn(project(":api").tasks.shadowJar)
    }
    compileJava {
        dependsOn(project(":api").tasks.shadowJar)
    }
}