package lgbt.princess.v.semver
package mapped
package ext.scala_builds

final case class Nightly(kind: Nightly.Kind, commitHashPrefix: String) {
  import Nightly._

  require(commitHashPrefixRegex matches commitHashPrefix, "commit hash prefix must be exactly 7 hex digits")

  def tag: String = s"${kind.signifier}-$commitHashPrefix"

  override def toString: String = tag
}

object Nightly {
  private final val commitHashPrefixRegex = "[0-9a-fA-F]{7}".r

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

  private object StringKind {
    def unapply(arg: String): Option[Kind] = {
      if (arg.equalsIgnoreCase(bin.signifier)) Some(bin)
      else if (arg.equalsIgnoreCase(pre.signifier)) Some(pre)
      else None
    }
  }

  implicit object NightlyIsMappable extends Mappable.ForwardingUnsafeMap[Nightly, Identifiers.PreRelease] {
    protected[this] def resultType: String = "Scala nightly"

    def map(identifiers: Identifiers.PreRelease): Option[Nightly] =
      identifiers.values match {
        case Seq(single) =>
          single.split("-", 3) match {
            case Array(StringKind(kind), hash) if commitHashPrefixRegex matches hash => Some(Nightly(kind, hash))
            case _                                                                   => None
          }
        case _ => None
      }
  }

  implicit object NightlyIsUnmappable extends Unmappable[Nightly, Identifiers.PreRelease] {
    def unmap(nightly: Nightly): Identifiers.PreRelease = Identifiers.PreRelease(nightly.tag)
  }
}
