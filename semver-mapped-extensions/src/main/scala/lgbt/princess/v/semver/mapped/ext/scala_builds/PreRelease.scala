package lgbt.princess.v.semver
package mapped
package ext.scala_builds

sealed trait PreRelease

object PreRelease {
  import Nightly._

  private[scala_builds] object KindString {
    def unapply(arg: String): Option[Kind] = {
      if (arg equalsIgnoreCase bin.signifier) Some(bin)
      else if (arg equalsIgnoreCase pre.signifier) Some(pre)
      else None
    }
  }

  private object PositiveNumber {
    def unapply(arg: String): Option[Int] =
      arg.toIntOption.filter(_ > 0)
  }

  implicit object PreReleaseMapper extends Mapper.ForwardingUnsafeMap[PreRelease, Identifiers.PreRelease] {
    protected[this] def resultType: String = "Scala pre-release or nightly"

    def map(identifiers: Identifiers.PreRelease): Option[PreRelease] =
      identifiers.values match {
        case Seq(single) =>
          single.split("-", 3) match {
            case Array(KindString(kind), hash) if commitHashPrefixRegex matches hash => Some(Nightly(kind, hash))
            case Array(str) =>
              str match {
                case s"M${PositiveNumber(num)}"  => Some(Milestone(num))
                case s"RC${PositiveNumber(num)}" => Some(ReleaseCandidate(num))
                case _                           => None
              }
            case _ => None
          }
        case _ => None
      }
  }

  implicit object PreReleaseUnmapper extends Unmapper[PreRelease, Identifiers.PreRelease] {
    def unmap(preRelease: PreRelease): Identifiers.PreRelease = {
      case Milestone(number)        => Identifiers.PreRelease(s"M$number")
      case ReleaseCandidate(number) => Identifiers.PreRelease(s"RC$number")
      case nightly: Nightly         => Identifiers.PreRelease(nightly.tag)
    }
  }
}

final case class Milestone(number: Int) extends PreRelease {
  require(number > 0, "milestone number must be positive")

  override def toString: String = s"M$number"
}

final case class ReleaseCandidate(number: Int) extends PreRelease {
  require(number > 0, "release candidate number must be positive")

  override def toString: String = s"RC$number"
}

final case class Nightly(kind: Nightly.Kind, commitHashPrefix: String) extends PreRelease {
  import Nightly._

  require(commitHashPrefixRegex matches commitHashPrefix, "commit hash prefix must be exactly 7 hex digits")

  def tag: String = s"${kind.signifier}-$commitHashPrefix"

  override def toString: String = tag
}

object Nightly {
  private[scala_builds] final val commitHashPrefixRegex = "[0-9a-fA-F]{7}".r

  sealed trait Kind {
    def signifier: String
    final override def toString: String = signifier
  }
  object Kind {
    object BinaryCompatible extends Kind {
      def signifier: String = "bin"
    }
    object PreRelease extends Kind {
      def signifier: String = "pre"
    }
  }

  @inline def bin: Kind = Kind.BinaryCompatible

  @inline def pre: Kind = Kind.PreRelease
}
