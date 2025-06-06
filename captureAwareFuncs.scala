package captureAware


object WithContext:
  //safe
  def pushRight[A, B](f: Context ?-> A -> B): A -> Context ?-> B =
    a => f(a)

  //unsafe without CaptureChecking
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
     * (using ct:Context): A ->{ct} B //correct
     * Context ?-> A -> Context ?-> B //verbose
     *
     * invalid:
     * Context ?-> A -> B // Context^ is not allowed capture set
     * (ct: Context) ?-> A -> B^{ct} // Same
     * `Context ?-> A -> B^`// Same
     */
    a => f(a)

  def pushLeftGeneric[A, B](f: Context ?-> A -> Context ?-> B): Context ?-> A => B =
    a => f(a)

object WithMonad:
  def pushRight[A, B](f: Option[A -> B]): A -> Option[B] =
    a => f.map(_(a))

  def pushLeft[A, B](f: A -> Option[B]): Option[A -> B] =
    None // not possible to implement



object Usage:
  type PrgL = Context ?-> Int -> String
  type PrgG = (Context ?-> (Int -> String)^)
  type PrgR = Int -> Context ?-> String

  val prgL_0: PrgL = Context.none
  //val prgL_1: PrgL = _ => Context.none // do not compile
  val prgL_2: PrgL = i => "toto".take(i)
  //val prgL_3: PrgL = i => if(i == 0) Context.none else "toto" //do not compile

  //val prgR_0: PrgR = Context.none //do not compile
  val prgR_1: PrgR = _ => Context.none

  val prgR_3: PrgR = i => if (i == 0) Context.none else "toto"

  val prgG_0: PrgG = Context.none
  val prgG_1: PrgG = _ => Context.none
  val prgG_2: PrgG = i => "toto".take(i)
  val prgG_3: PrgG = i => if (i == 0) Context.none else "toto"

  val pushed_1: PrgG = WithContext.pushLeft(prgR_1)
  val pushed_2: PrgG = WithContext.pushLeftGeneric(prgR_1)
  val pushed_3: PrgG = prgL_0
  val pushed_4: PrgG = WithContext.pushLeftGeneric(a => prgL_0(a)) //doesn't work directly

  val res_1: OrNull[Int -> String] = Context.run(prgL_0)

  val res_2: OrNull[Int -> Context ?-> String] = Context.run(prgR_1) //Cannot escape
  val res_3: Int -> OrNull[String] = a => Context.run(prgR_1(a))
  val res_4: Int -> OrNull[String] = a => Context.run(prgG_1(a))

  // INLINING
  inline def def_prgG_0: PrgG = Context.none

  inline def def_prgG_1: PrgG = _ => Context.none

  inline def def_prgG_2: PrgG = i => "toto".take(1)

  inline def def_prgG_3: PrgG = i => if (i == 0) Context.none else "toto"


  // In this case, prgG_d0 (Context ?-> (A -> B)^) is turned into (Context ?=> (A -> B))
  val res_5: OrNull[Int -> String] = Context.run(def_prgG_0)
  //val res_6: Option[Int -> String] = Context.run(prgG_d1) // do not compile
  val res_7: Int -> OrNull[String] = a => Context.run(def_prgG_1(a))
  val res_8: OrNull[Int -> String] = Context.run(def_prgG_2)
  //val res_9: Option[Int -> String] = Context.run(prgG_d3) // do not compile
  val res_9: Int -> OrNull[String] = a => Context.run(def_prgG_3(a))

  inline def def_prgR_2: PrgR = i => "toto".take(i)

  //There are probably better inlining possible, to Int -> String
  //might be possible to work this case with a macro
  //val res_10: Int -> String = prgR_d2 // could work, this definition is not using the Capability
  val res_10: OrNull[Int -> String] = Context.run(i => def_prgR_2(i))

  trait inlineShowCase[A, B]:
    def f(a: A): B

    inline def g: A -> Context ?-> B = f

    val h: (A -> B) | Null = Context.run(a => g(a))