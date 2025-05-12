package captureAware

//with explicit null activated '-Yexplicit-nulls'
type OrNull[A] = A | Null

given [X]: CanEqual[X | Null, Null] = CanEqual.derived
