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
    implementation("net.bytebuddy:byte-buddy-agent:1.14.12")
}

tasks.jar {
    manifest {
        attributes(
            "Premain-Class" to "com.extreme.java.AgentLauncher",
            "Agent-Class" to "com.extreme.java.AgentLauncher",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}