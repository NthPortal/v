ThisBuild / scalaVersion := "2.13.3"
ThisBuild / autoAPIMappings := true

// publishing info
inThisBuild(
  Seq(
    organization := "lgbt.princess",
    homepage := Some(url("https://github.com/NthPortal/v")),
    licenses := Seq("The Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    developers := List(
      Developer(
        "NthPortal",
        "April | Princess",
        "dev@princess.lgbt",
        url("https://nthportal.com"),
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/NthPortal/v"),
        "scm:git:git@github.com:NthPortal/v.git",
        "scm:git:git@github.com:NthPortal/v.git",
      )
    ),
  )
)

lazy val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.3" % Test
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Xlint",
    "-Werror",
  ),
  scalacOptions ++= {
    if (isSnapshot.value) Nil
    else Seq("-opt:l:inline", "-opt-inline-from:lgbt.princess.v.**")
  },
)

lazy val defaultMimaSettings = Seq(
  mimaPreviousArtifacts := Set().map(organization.value %% name.value % _),
)

lazy val core = project
  .in(file("core"))
  .settings(sharedSettings)
  .settings(defaultMimaSettings)
  .settings(
    name := "v-core",
  )
lazy val coreTest = core % "test->test"

lazy val semver = project
  .in(file("semver"))
  .dependsOn(
    core,
    coreTest,
  )
  .settings(sharedSettings)
  .settings(defaultMimaSettings)
  .settings(
    name := "v-semver",
  )

lazy val semverMapped = project
  .in(file("semver-mapped"))
  .dependsOn(semver)
  .settings(sharedSettings)
  .settings(defaultMimaSettings)
  .settings(
    name := "v-semver-mapped",
  )

lazy val semverMappedExt = project
  .in(file("semver-mapped-extensions"))
  .dependsOn(semverMapped)
  .settings(sharedSettings)
  .settings(defaultMimaSettings)
  .settings(
    name := "v-semver-mapped-extensions",
  )

lazy val root = project
  .in(file("."))
  .aggregate(
    core,
    semver,
    semverMapped,
    semverMappedExt,
  )
  .dependsOn(
    core,
    semver,
    semverMapped,
    semverMappedExt,
  )
  .settings(
    name := "v",
    mimaPreviousArtifacts := Set.empty,
    Compile / doc / sources :=
      (Compile / doc / sources).value ++
        (core / Compile / doc / sources).value ++
        (semver / Compile / doc / sources).value ++
        (semverMapped / Compile / doc / sources).value ++
        (semverMappedExt / Compile / doc / sources).value,
  )
