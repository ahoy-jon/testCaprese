package state


class StateTest extends munit.FunSuite:
  test("get"):
    val r = State.run(1):
      State.get[Int] + 1

    assertEquals(r, 2)

  test("setDiscard, get"):
    val r = State.run(1):
      State.setDiscard(3)
      State.get[Int]
      
    assertEquals(r, 3)
  


