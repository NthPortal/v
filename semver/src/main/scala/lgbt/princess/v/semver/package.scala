package lgbt.princess.v

import scala.language.implicitConversions

package object semver {
  import Identifiers._

  type Core = V3 // TODO: change after constraints are implemented
  final val Core = V3

  /**
   * This extractor is for extracting the version core and pre-release identifiers
   * of a SemVer version. If you want to extract build identifiers as well,
   * use the [[:- `:-`]] extractor instead. Sadly, there is no way to have both
   * functionalities in the same extractor.
   */
  object - {
    def unapply(sv: SemVer): Option[(Core, PreRelease)] = {
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
  object :- {
    def unapply(sv: SemVer): Option[(Core, (PreRelease, Option[Build]))] =
      sv match {
        case SemVer(core, Some(preRelease), build) => Some((core, (preRelease, build)))
        case _                                     => None
      }
  }

  object + {
    def unapply(sv: SemVer): Option[(Core, Build)] =
      sv match {
        case SemVer(core, None, Some(build)) => Some((core, build))
        case _                               => None
      }

    def unapply(arg: (PreRelease, Option[Build])): Option[(PreRelease, Build)] =
      arg match {
        case (preRelease, Some(build)) => Some((preRelease, build))
        case _                         => None
      }
  }

  implicit final class VersionOps(private val self: Core) extends AnyVal {
    def -(preRelease: PreRelease): SemVerPreReleaseIntermediate =
      new SemVerPreReleaseIntermediate(SemVer(self, Some(preRelease), None))

    def +(build: Build): SemVer = SemVer(self, None, Some(build))

    def toSemVer: SemVer = SemVer(self, None, None)
  }

  /**
   * The core and pre-release identifiers of a SemVer version,
   * without build identifiers.
   */
  final class SemVerPreReleaseIntermediate private[semver] (private val self: SemVer) extends AnyVal {
    def +(build: Build): SemVer = self.copy(build = Some(build))

    @inline def toSemVer: SemVer        = self
    @inline def withoutMetadata: SemVer = toSemVer
  }

  object SemVerPreReleaseIntermediate {
    implicit def intermediateToSemVer(intermediate: SemVerPreReleaseIntermediate): SemVer =
      intermediate.toSemVer
  }
}
