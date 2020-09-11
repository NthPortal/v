package lgbt.princess.v.semver

import lgbt.princess.v.semver.Identifiers._

import scala.language.implicitConversions

object StringsAsUnsafeIdentifiers {
  implicit def asPreRelease(s: String): PreRelease = PreRelease.unsafeParse(s)
  implicit def asBuild(s: String): Build           = Build.unsafeParse(s)

  implicit final class VersionOpsExtra(private val self: Core) extends AnyVal {
    def -(preRelease: String): SemVerPreReleaseIntermediate =
      new SemVerPreReleaseIntermediate(SemVer(self, Some(preRelease: PreRelease), None))

    def +(build: String): SemVer = SemVer(self, None, Some(build: Build))
  }
}
