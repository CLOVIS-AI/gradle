plugins {
    id("gradlebuild.distribution.api-java")
}

description = "Base plugin for the maven and ivy publish plugins. Defines the publishing extension."

errorprone {
    disabledChecks.addAll(
        "MixedMutabilityReturnType", // 5 occurrences
        "StringCaseLocaleUsage", // 1 occurrences
    )
}

dependencies {
    api(project(":base-services"))
    api(project(":core"))
    api(project(":core-api"))
    api(project(":dependency-management"))
    api(project(":file-collections"))
    api(project(":hashing"))
    api(project(":logging"))
    api(project(":logging-api"))
    api(project(":model-core"))

    api(libs.inject)
    api(libs.jsr305)

    implementation(project(":base-annotations"))
    implementation(project(":base-services-groovy")) {
        because("Required for Specs")
    }
    implementation(project(":functional"))

    implementation(libs.commonsLang)
    implementation(libs.gson)
    implementation(libs.guava)

    testImplementation(testFixtures(project(":core")))

    testRuntimeOnly(project(":distributions-core")) {
        because("Tests instantiate DefaultClassLoaderRegistry which requires a 'gradle-plugins.properties' through DefaultPluginModuleRegistry")
    }
    integTestDistributionRuntimeOnly(project(":distributions-core"))
}

integTest.usesJavadocCodeSnippets = true
