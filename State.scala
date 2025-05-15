package state

class State[X] private(private var _x: X) extends caps.Capability:
  //@return : previous value
  inline def set(x: X): X =
    val prev = _x
    _x = x
    prev

  inline def setDiscard(x: X): Unit =
    _x = x

  inline def get: X = _x

  inline def update(f: X -> X): X =
    _x = f(_x)
    _x

  inline def updateDiscard(f: X -> X): Unit =
    _x = f(_x)

object State:
  inline def get[X](using state: State[X]): X = state.get

  inline def set[X](x: X)(using state: State[X]): X = state.set(x)

  inline def setDiscard[X](x: X)(using state: State[X]): Unit = state.setDiscard(x)

  inline def update[X](f: X -> X)(using state: State[X]): X = state.update(f)

  inline def updateDiscard[X](f: X -> X)(using state: State[X]): Unit = state.updateDiscard(f)

  def run[X, A](state: X)(body: State[X] ?-> A): A =
    given State[X] = new State(state)
    body
