package lgbt.princess.v

import scala.language.implicitConversions

package object semver {
  type V = V3 // TODO: change after constraints are implemented
  final val V = V3

  /**
   * This extractor is for extracting the version core and pre-release identifiers
   * of a SemVer version. If you want to extract build identifiers as well,
   * use the [[-- `--`]] extractor instead. Sadly, there is no way to have both
   * functionalities in the same extractor.
   */
  object - {
    def unapply(sv: SemVer): Option[(V, Identifiers.PreRelease)] = {
      if (sv.build.isDefined || sv.preRelease.isEmpty) None
      else Some((sv.core, sv.preRelease.get))
    }
  }

  /**
   * This extractor is extracting the version core of a SemVer version,
   * and is for use specifically with the [[+ `+`]] extractor (which
   * extracts the pre-release identifiers and build identifiers). If you
   * only want to extract the core and pre-release identifiers, use the
   * [[- `-`]] extractor instead.
   */
  object -- {
    def unapply(sv: SemVer): Option[(V, (Identifiers.PreRelease, Option[Identifiers.Build]))] =
      sv match {
        case SemVer(core, Some(preRelease), build) => Some((core, (preRelease, build)))
        case _                                     => None
      }
  }

  object + {
    def unapply(sv: SemVer): Option[(V, Identifiers.Build)] =
      sv match {
        case SemVer(core, None, Some(build)) => Some((core, build))
        case _                               => None
      }

    def unapply(
        arg: (Identifiers.PreRelease, Option[Identifiers.Build])
    ): Option[(Identifiers.PreRelease, Identifiers.Build)] =
      arg match {
        case (preRelease, Some(build)) => Some((preRelease, build))
        case _                         => None
      }
  }

  implicit final class VersionOps(private val self: V) extends AnyVal {
    def -(preRelease: Identifiers.PreRelease): SemVerPreReleaseIntermediate =
      new SemVerPreReleaseIntermediate(SemVer(self, Some(preRelease), None))

    def +(build: Identifiers.Build): SemVer = SemVer(self, None, Some(build))

    def toSemVer: SemVer = SemVer(self, None, None)
  }

  /**
   * The version and pre-release portions of a SemVer version,
   * without
   */
  final class SemVerPreReleaseIntermediate private[semver] (private val self: SemVer) extends AnyVal {
    def +(build: Identifiers.Build): SemVer = self.copy(build = Some(build))

    @inline def toSemVer: SemVer        = self
    @inline def withoutMetadata: SemVer = toSemVer
  }

  object SemVerPreReleaseIntermediate {
    implicit def intermediateToSemVer(intermediate: SemVerPreReleaseIntermediate): SemVer =
      intermediate.toSemVer
  }
}
