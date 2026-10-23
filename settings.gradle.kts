pluginManagement {
    repositories {
        google()
        // Maven Central often returns 403 in some regions (PK/ISP filters).
        // Prefer Google's public Maven Central mirror + Huawei mirror as fallbacks.
        maven {
            name = "GoogleMavenCentralMirror"
            url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
            content {
                excludeGroupByRegex("com\\.android.*")
                excludeGroupByRegex("com\\.google\\.android.*")
                excludeGroupByRegex("androidx\\..*")
            }
        }
        maven {
            name = "HuaweiMaven"
            url = uri("https://repo.huaweicloud.com/repository/maven/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            name = "GoogleMavenCentralMirror"
            url = uri("https://maven-central.storage-download.googleapis.com/maven2/")
        }
        maven {
            name = "HuaweiMaven"
            url = uri("https://repo.huaweicloud.com/repository/maven/")
        }
        mavenCentral()
    }
}

rootProject.name = "VitalSyncWear"
include(":app")
