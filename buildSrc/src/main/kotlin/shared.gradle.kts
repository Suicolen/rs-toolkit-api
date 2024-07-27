import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.module

plugins {
    `java-library`
}

repositories {
    gradlePluginPortal()

   /* maven {
        url = uri("https://repo.openrs2.org/repository/openrs2-snapshots/")
    }*/
}

group = "editor"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

configurations
    .matching { it.name.contains("downloadSources") }
    .configureEach {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
            attribute(
                OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE,
                objects.named(OperatingSystemFamily::class, OperatingSystemFamily.WINDOWS)
            )
            attribute(
                MachineArchitecture.ARCHITECTURE_ATTRIBUTE,
                objects.named(MachineArchitecture::class, MachineArchitecture.X86_64)
            )
        }
    }

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    jvmArgs = listOf("--enable-preview");
}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    val lombok = module("org.projectlombok", "lombok", "1.18.32")

    compileOnly(lombok)
    annotationProcessor(lombok)

    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)

    implementation("one.util:streamex:0.8.1")
    testImplementation("one.util:streamex:0.8.1")
    implementation("com.displee:rs-cache-library:7.1.5")
    implementation("org.apache.commons", "commons-compress", "1.20")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.17.1")
    implementation("com.fasterxml.jackson.module:jackson-module-paranamer:2.17.2")
    implementation("org.rauschig:jarchivelib:1.2.0")
    implementation("org.apache.commons:commons-lang3:3.15.0")

    // implementation("org.openrs2:openrs2-cache:0.1.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
