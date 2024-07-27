plugins {
    java
    shared
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "api"
version = "1.0"

// aah i really don't like this, but refactoring this would be quite tedious
javafx {
    version = "22"
    modules = listOf("javafx.graphics")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    api("org.joml", "joml", "1.10.4")
    api("io.netty", "netty-buffer", "4.1.79.Final")

}

tasks.test {
    useJUnitPlatform()
}