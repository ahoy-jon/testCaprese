package captureAware

class Context extends caps.Capability

object WithContext:
  def pushRight[A, B](f: Context ?-> A -> B): A -> Context ?-> B =
    a => f(a)

  def pushLeft[A, B](f: A -> Context ?-> B): Context ?-> A => B =
    a => f(a)
    //valid:
    //  (ct: Context) ?-> A => B^{ct} //redundant
    //  Context ?-> A => B^ //redundant
    //  Context ?-> A => B
    //  Context ?-> (A -> B)^
    //  (ct: Context) ?-> (A -> B)^{ct}
    //  Context ?-> A -> Context ?-> B //verbose
    //
    //invalid:
    //  Context ?-> A -> B // Context^ is not allowed capture set
    //  (ct: Context) ?-> A -> B^ct
    //  Context ?-> A -> B^

object WithMonad:
  def pushRight[A, B](f: Option[A -> B]): A -> Option[B] =
    a => f.map(_(a))

  def pushLeft[A, B](f: A -> Option[A]): Option[A -> B] =
    ??? // not possible to implement
