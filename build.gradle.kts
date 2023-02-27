plugins {
    `java-library`
    jacoco
    id 'maven-publish'
    id 'signing'
}

group = "io.github.neilwangweili"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
    testRuntimeOnly("org.junit.platform:junit-platform-runner:1.8.2")
    testImplementation("org.mockito:mockito-core:4.6.1")
    testImplementation("org.mockito:mockito-junit-jupiter:4.6.1")
    testImplementation("org.assertj:assertj-core:3.23.1")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

javaDoc {
    options.addStringOption("charset", "UTF-8")
    if (JavaVersion.current().isJava9Compatible()) {
        options.addBooleanOption('html5', true)
    }
}


tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            element = "PACKAGE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = BigDecimal(1.0)
            }
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = BigDecimal(1.0)
            }
            limit {
                counter = "CLASS"
                value = "COVEREDRATIO"
                minimum = BigDecimal(1.0)
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = BigDecimal(1.0)
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestCoverageVerification)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            artifactId = 'epiphany'
            from components.java
                    pom {
                        name = 'epiphany'
                        description = 'A lightweight dependency injection framework.'
                        url = 'https://github.com/neilwangweili/epiphany'
                        licenses {
                            license {
                                name = '许可证名称'
                                url = '许可证地址'
                            }
                        }
                        developers {
                            developer {
                                id = 'neilwangweili'
                                name = 'Neil Wang'
                                email = 'wangweili457@gmail.com'
                            }
                        }
                        scm {
                            connection = 'https://github.com/neilwangweili/epiphany.git'
                            developerConnection = 'https://github.com/neilwangweili/epiphany.git'
                            url = 'https://github.com/neilwangweili/epiphany'
                        }
                    }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            if (project.version.toString().endsWith("-SNAPSHOT")) {
                url = "https://s01.oss.sonatype.org/content/repositories/snapshots"
            } else {
                url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }
            credentials {
                username = findProperty("ossrhUsername") ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword") ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications.mavenJava)
}
