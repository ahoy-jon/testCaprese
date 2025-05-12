package escape

import scala.util.control.{ControlThrowable, NoStackTrace}
import captureAware.given

object WithoutCC:
  class Context private:
    def none: Nothing = throw Context.Control

  object Context:
    object Control extends ControlThrowable("Context Control") with NoStackTrace:
      given CanEqual[Control.type, Throwable] = CanEqual.derived

    inline def none(using escape: Context): Nothing = escape.none

    def run[A](body: Context ?=> A): A | Null =
      try
        body(using new Context)
      catch
        case Control => null


class Escape extends munit.FunSuite:
  type F1 = WithoutCC.Context ?-> Int -> String
  type F2 = Int -> WithoutCC.Context ?-> String

  def f1ToF2(f1: F1): F2 = a => f1(a)

  def f2ToF1(f2: F2): F1 = a => f2(a)

  val noF1: F1 = WithoutCC.Context.none
  val noF2: F2 = _ => WithoutCC.Context.none

  test("easy"):
    val r1: (Int -> String) | Null = WithoutCC.Context.run(noF1)
    val r2: Int -> (String | Null) = a => WithoutCC.Context.run(noF2(a))

    assertEquals(r1, null)
    assertEquals(r2(1), null)

  test("escaping"):
    val r1: (Int -> String) | Null = WithoutCC.Context.run(f2ToF1(noF2))

    if (r1 == null)
      fail("not supposed to be null")
    else
      try
        val s: String = r1(1)
        fail("not supposed to execute fully")
      catch
        case _: ControlThrowable => //OUPS:escaped exception


class NoEscapeWithCC extends munit.FunSuite:

  import captureAware.*

  type F1 = Context ?-> Int -> String
  type F1cap = Context ?-> (Int -> String)^
  type F2 = Int -> Context ?-> String

  def f1ToF2(f1: F1): F2 = a => f1(a)

  //do not compile
  //def f2ToF1(f2: F2): F1 = a => f2(a)
  def f2ToF1cap(f2: F2): F1cap = a => f2(a)

  val noF1: F1 = Context.none
  val noF2: F2 = _ => Context.none

  test("easy"):
    val r1: (Int -> String) | Null = Context.run(noF1)
    val r2: Int -> (String | Null) = a => Context.run(noF2(a))

    assertEquals(r1, null)
    assertEquals(r2(1), null)

  test("escaping"):
    val r1: (Int => String) | Null = Context.run(f2ToF1cap(noF2))

    if (r1 == null)
      fail("not supposed to be null")
    else
      try
        val s: String = r1(1)
        fail("not supposed to execute fully")
      catch
        case _: ControlThrowable => //OUPS:escaped exception

  test("no escape"):
    val r1 = Context.run(f2ToF1cap(noF2))
    if (r1 == null)
      fail("not supposed to be null")
    else
      compileErrors("val s:String = r1(1)")
      assertEquals(Context.run(r1(1)), null)