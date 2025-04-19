dependencies {
    api(project(":api"))

    implementation("org.hibernate:hibernate-core:6.6.5.Final")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.6.13.Final")
    testImplementation("org.hibernate.orm:hibernate-community-dialects:6.6.13.Final")

    implementation("org.postgresql:postgresql:42.7.1")


    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    testImplementation("org.xerial:sqlite-jdbc:3.49.1.0")

}

tasks {
    jar {
        dependsOn(project(":api").tasks.shadowJar)
    }
    compileJava {
        dependsOn(project(":api").tasks.shadowJar)
    }
}