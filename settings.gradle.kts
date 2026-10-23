pluginManagement {
    repositories {
        google()
        // Maven Central often returns 403 in some regions (PK/ISP filters).
        // Prefer Google's public Maven Central mirror + Aliyun as fallback.
        // Do NOT use Huawei here — it hangs and causes "Connection timed out: getsockopt".
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
            name = "AliyunCentral"
            url = uri("https://maven.aliyun.com/repository/central")
            content {
                excludeGroupByRegex("com\\.android.*")
                excludeGroupByRegex("com\\.google\\.android.*")
                excludeGroupByRegex("androidx\\..*")
            }
        }
        maven {
            name = "AliyunPublic"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        gradlePluginPortal()
        // Keep last: often 403 in PK; only used if mirrors miss an artifact.
        mavenCentral()
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
            name = "AliyunCentral"
            url = uri("https://maven.aliyun.com/repository/central")
        }
        maven {
            name = "AliyunPublic"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        mavenCentral()
    }
}

rootProject.name = "VitalSyncWear"
include(":app")
