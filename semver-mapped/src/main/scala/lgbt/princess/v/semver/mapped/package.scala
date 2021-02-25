package lgbt.princess.v.semver

import scala.language.implicitConversions

package object mapped {
  implicit def mappingSyntax(sv: SemVer): SemVerMappingOps = new SemVerMappingOps(sv)
}
