plugins {
    java
}

group = "com.github.zEssentialsXConverter"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
}

sourceSets {
    main {
        java.srcDirs("src/main/java")
        resources.srcDirs("srcs/main/resources")
    }
}
