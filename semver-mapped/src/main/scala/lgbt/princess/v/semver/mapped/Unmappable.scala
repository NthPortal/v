package lgbt.princess.v.semver
package mapped

import lgbt.princess.v.semver.Identifiers._

trait Unmappable[A, I <: Identifiers] {
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
