dependencies {
    api(project(":api"))

    implementation("org.hibernate:hibernate-core:6.6.21.Final")
    implementation("org.hibernate.orm:hibernate-hikaricp:6.6.13.Final")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.6.14.Final")
    testImplementation("org.hibernate.orm:hibernate-community-dialects:6.6.14.Final")
    testImplementation("org.hibernate.orm:hibernate-hikaricp:6.6.13.Final")

    implementation("org.postgresql:postgresql:42.7.5")


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