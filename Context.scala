package captureAware

import scala.annotation.publicInBinary
import scala.util.control.{ControlThrowable, NoStackTrace}


class Context @publicInBinary protected extends caps.Capability:
  inline def none: Nothing = throw Context.ControlNone

object Context:
  inline def none(using context: Context): Nothing = context.none

  private object ControlNone extends ControlThrowable("None") with NoStackTrace:
    given CanEqual[ControlNone.type, Throwable] = CanEqual.derived

  transparent inline def run[A](inline body: Context ?=> A): Option[A] =
    try
      Option(body(using new Context()))
    catch
      case Context.ControlNone => None