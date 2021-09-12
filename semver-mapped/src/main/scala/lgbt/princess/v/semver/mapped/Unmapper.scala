package lgbt.princess.v.semver
package mapped

import lgbt.princess.v.semver.Identifiers._

/**
 * Something that can unmap some type into a type of [[Identifiers]].
 *
 * @tparam A the type being unmapped from
 * @tparam I the type of identifiers being unmapped to
 */
// Can be `-A` because, unlike `Mapper`, an instance of this trait guarantees
//   the ability to unmap all values of type `A`, so being able to unmap all
//   values of a supertype of `A` is fine too.
// Can be `+I`, but it's not useful because we never require implicit
//   instances for the more general `Identifiers` (only for its two subtypes),
//   and even if we did, it could cause ambiguous implicits.
trait Unmapper[-A, I <: Identifiers] {
  /**
   * Unmaps a value of the other type to identifiers.
   *
   * @param a the value to be unmapped
   * @return the identifiers equivalent to the given value
   */
  def unmap(a: A): I
}

object Unmapper {
  private final class NothingUnmapper[I <: Identifiers] extends Unmapper[Nothing, I] {
    def unmap(a: Nothing): I =
      throw new UnsupportedOperationException("nothing to unmap (should be unreachable)")
  }

  implicit val nothingIsUnmappableFromPreRelease: Unmapper[Nothing, PreRelease] = new NothingUnmapper[PreRelease]
  implicit val nothingIsUnmappableFromBuild: Unmapper[Nothing, Build]           = new NothingUnmapper[Build]
}
