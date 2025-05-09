package captureAware

import scala.annotation.publicInBinary
import scala.util.control.{ControlThrowable, NoStackTrace}


object WithContext:
  def pushRight[A, B](f: Context ?-> A -> B): A -> Context ?-> B =
    a => f(a)

  def pushLeft[A, B](f: A -> Context ?-> B): Context ?-> A => B =

    /**
     * valid return types:
     * (ct: Context) ?-> A => B^{ct} //redundant
     * Context ?-> A => B^ //redundant
     * Context ?-> A => B
     * (using Context): A => B
     * Context ?-> (A -> B)^
     * (ct: Context) ?-> (A -> B)^{ct} //redundant
     * (ct: Context) ?-> A ->{ct} B //redundant
     * (using ct:Context): A ->{ct} B
     * Context ?-> A -> Context ?-> B //verbose
     *
     * invalid:
     * Context ?-> A -> B // Context^ is not allowed capture set
     * (ct: Context) ?-> A -> B^{ct}
     * `Context ?-> A -> B^`
     */
    a => f(a)

object WithMonad:
  def pushRight[A, B](f: Option[A -> B]): A -> Option[B] =
    a => f.map(_(a))

  def pushLeft[A, B](f: A -> Option[A]): Option[A -> B] =
    None // not possible to implement


class Context @publicInBinary private extends caps.Capability:
  inline def none: Nothing = throw Context.ControlNone

object Context:
  inline def none(using context: Context): Nothing = context.none

  inline def run[A](inline body: Context ?=> A): Option[A] =
    try
      Option(body(using Context()))
    catch
      case ControlNone => None

  private object ControlNone extends ControlThrowable("None") with NoStackTrace:
    given CanEqual[ControlNone.type, Throwable] = CanEqual.derived
