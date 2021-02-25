package lgbt.princess.v.semver
package mapped
package mvn_simple

import scala.annotation.unused

sealed abstract class Snapshot {
  override def toString: String = Snapshot.tag
}

object Snapshot extends Snapshot {
  private final val tag = "SNAPSHOT"
  private final val identifiers = Identifiers.PreRelease(tag)

  implicit object SnapshotIsMappable extends Mappable.ForwardingUnsafeMap[Snapshot, Identifiers.PreRelease] {
    protected[this] def resultType: String = "Maven Snapshot"

    def map(identifiers: Identifiers.PreRelease): Option[Snapshot] = identifiers.values match {
      case Seq(`tag`) => Some(Snapshot.this)
      case _          => None
    }
  }

  implicit object SnapshotIsUnmappable extends Unmappable[Snapshot, Identifiers.PreRelease] {
    def unmap(@unused snapshot: Snapshot): Identifiers.PreRelease = identifiers
  }
}
