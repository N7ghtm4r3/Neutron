plugins {
    id("java")
    id("org.springframework.boot") version "3.2.3"
}

group = "com.tecknobit"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("com.github.N7ghtm4r3:APIManager:2.2.3")
    implementation("com.github.N7ghtm4r3:Equinox:1.0.0")
    implementation("com.tecknobit.neutroncore:Neutron-core:1.0.0")
    implementation("org.json:json:20231013")
}

configurations.all {
    exclude("commons-logging", "commons-logging")
}