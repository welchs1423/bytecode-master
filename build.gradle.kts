plugins {
    java
}

group = "com.extreme.java"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.14.12")
}

tasks.jar {
    manifest {
        attributes(
            "Premain-Class" to "com.extreme.java.AgentLauncher",
            "Can-Retransform-Classes" to "true",
            "Can-Redefine-Classes" to "true"
        )
    }
}