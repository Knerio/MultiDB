dependencies {
    api(project(":api"))

    implementation("org.hibernate:hibernate-core:6.6.4.Final")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.6.4.Final")
    testImplementation("org.hibernate.orm:hibernate-community-dialects:6.6.4.Final")

    implementation("org.xerial:sqlite-jdbc:3.47.2.0")
    testImplementation("org.xerial:sqlite-jdbc:3.47.2.0")

}

tasks {
    jar {
        dependsOn(project(":api").tasks.shadowJar)
    }
    compileJava {
        dependsOn(project(":api").tasks.shadowJar)
    }
}