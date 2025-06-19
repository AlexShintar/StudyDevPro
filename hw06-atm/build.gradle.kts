dependencies {
    implementation ("ch.qos.logback:logback-classic")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("org.assertj:assertj-core")
}


