package lgbt.princess.v.semver
package mapped

/**
 * Something that can map a type of [[Identifiers]] to some other type.
 *
 * @tparam A the type being mapped to
 * @tparam I the type of identifiers being mapped from
 */
// Can't be `+A` because, while that does return a valid type, the compiler
//   may select an implicit instance for a subtype of `A` that only maps a
//   subset of valid values for `A`. It's especially problematic because
//   `Nothing` is a subtype of `A` and maps an empty subset of valid values
//   for `A`, and we need an instance for `Nothing` for `Mapping`s without one
//   of the two types of `Identifiers`. Additionally, it causes ambiguous implicits.
// Can be `-I` to support instances that can map from arbitrary `Identifiers`
//   rather than only from one of the two subtypes of `Identifiers`.
trait Mapper[A, -I <: Identifiers] {

  /**
   * Maps identifiers to the other type, if possible.
   *
   * @param identifiers the identifiers to be mapped
   * @return the result type equivalent to the given identifiers,
   *         or `None` if the identifiers could not be mapped
   */
  def map(identifiers: I): Option[A]

  /**
   * Maps identifiers to the other type, throwing an exception if unable.
   *
   * @param identifiers the identifiers to be mapped
   * @return the result type equivalent to the given identifiers
   * @throws scala.UnsupportedOperationException if the identifiers could not be mapped
   */
  @throws[UnsupportedOperationException]
  def unsafeMap(identifiers: I): A
}

object Mapper {

  /**
   * A [[Mapper]] that forwards the implementation of
   * [[Mapper.unsafeMap `unsafeMap`]] to [[Mapper.map `map`]].
   */
  trait ForwardingUnsafeMap[A, -I <: Identifiers] extends Mapper[A, I] {

    /** A description of the result type of the mapping operation. */
    protected[this] def resultType: String

    final def unsafeMap(identifiers: I): A =
      map(identifiers)
        .getOrElse(throw new UnsupportedOperationException(s"cannot map '$identifiers' to $resultType"))
  }

  implicit object NothingMapper extends Mapper[Nothing, Identifiers] {
    def map(identifiers: Identifiers): Option[Nothing] = None
    def unsafeMap(identifiers: Identifiers): Nothing =
      throw new UnsupportedOperationException("cannot map identifiers to nothing")
  }
}
