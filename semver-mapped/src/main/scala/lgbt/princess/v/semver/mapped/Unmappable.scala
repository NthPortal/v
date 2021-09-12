package lgbt.princess.v.semver
package mapped

import lgbt.princess.v.semver.Identifiers._

// Can be `-A` because, unlike `Mappable`, an instance of this trait guarantees
//   the ability to unmap all values of type `A`, so being able to unmap all
//   values of a supertype of `A` is fine too.
// Can be `+I`, but it's not useful because we never require implicit
//   instances for the more general `Identifiers` (only for its two subtypes),
//   and even if we did, it could cause ambiguous implicits.
trait Unmappable[-A, I <: Identifiers] {
  def unmap(a: A): I
}

object Unmappable {
  private final class NothingIsUnmappable[I <: Identifiers] extends Unmappable[Nothing, I] {
    def unmap(a: Nothing): I =
      throw new UnsupportedOperationException("nothing to unmap (should be unreachable)")
  }

  implicit val nothingIsUnmappableFromPreRelease: Unmappable[Nothing, PreRelease] = new NothingIsUnmappable[PreRelease]
  implicit val nothingIsUnmappableFromBuild: Unmappable[Nothing, Build]           = new NothingIsUnmappable[Build]
}
