package lgbt.princess.v.semver.mapped

import lgbt.princess.v.semver.Identifiers
import lgbt.princess.v.semver.Identifiers._

trait Mappable[A, I <: Identifiers] {
  def map(identifiers: I): Option[A]

  /**
   * @param identifiers the identifiers to be mapped
   * @return the result type equivalent to the given identifiers
   * @throws scala.UnsupportedOperationException if the identifiers cannot be mapped
   */
  @throws[UnsupportedOperationException]
  def unsafeMap(identifiers: I): A
}

object Mappable {
  trait ForwardingUnsafeMap[A, I <: Identifiers] extends Mappable[A, I] {
    protected[this] def resultType: String

    final def unsafeMap(identifiers: I): A =
      map(identifiers)
        .getOrElse(throw new UnsupportedOperationException(s"cannot map '$identifiers' to $resultType"))
  }

  private final class NothingIsMappable[I <: Identifiers] extends Mappable[Nothing, I] {
    def map(identifiers: I): Option[Nothing] = None
    def unsafeMap(identifiers: I): Nothing =
      throw new UnsupportedOperationException("cannot map identifiers to nothing")
  }

  implicit val nothingIsMappableToPreRelease: Mappable[Nothing, PreRelease] = new NothingIsMappable[PreRelease]
  implicit val nothingIsMappableToBuild: Mappable[Nothing, Build]           = new NothingIsMappable[Build]
}
