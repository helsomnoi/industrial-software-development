plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.library"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // PostgreSQL JDBC драйвер
    implementation("org.postgresql:postgresql:42.7.3")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

application {
    mainClass = "com.library.Main"
}

tasks.shadowJar {
    archiveBaseName.set("library")
    archiveClassifier.set("")
    archiveVersion.set("1.0")
    manifest {
        attributes["Main-Class"] = "com.library.Main"
    }
}

tasks.named("run") {
    dependsOn(tasks.named("classes"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec> {
    if (System.getProperty("os.name").startsWith("Windows")) {
        systemProperty("file.encoding", "UTF-8")
        systemProperty("sun.stdout.encoding", "UTF-8")
        systemProperty("sun.stderr.encoding", "UTF-8")
    }
}

tasks.named<JavaExec>("run") {
    systemProperty("file.encoding", "cp866")
}

tasks.test {
    useJUnitPlatform()
}