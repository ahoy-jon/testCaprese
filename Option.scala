package captureAware

//with explicit null activated '-Yexplicit-nulls'
type Option[A] = A | Null
inline def None: Null = null

given [X]: CanEqual[X | Null, Null] = CanEqual.derived

extension [A](a: A | Null)
  inline def map[B](f: A => B): B | Null =
    if (a == null) null else f(a)


def Option[A](a: A | Null): A | Null = a