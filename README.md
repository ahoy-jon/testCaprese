# testCaprese


Exploring **Capture Checking** in Scala.

Direct style is proposed as an alternative to the traditional monadic approach for effectful programming in Scala.  
This project demonstrates:

- How context functions can lead to unsafe behaviors.
- How **Capture Checking** detects and prevents these issues.

## run tests
With Scala-CLI:
```bash
scala test .
```

## unsafe behaviours

Context functions behave similarly to implicits, they can capture context values *without explicit tracking*.  
This can result in unsafe abstractions like:

```scala
def swapRight(f: C ?=> A  => B): 
                 A  => C ?=> B = 
  a => f(a)

def swapLeft(f: A  => C ?=> B): 
                C ?=> A  => B = 
  a => f(a)
```

`swapLeft` can create issues:

| Case                               | Safe? | Reason                                |
|------------------------------------|-------| ------------------------------------- |
| `C` is a pure value                | Yes   | No side-effects or lifetime issues    |
| `C` is a resource (e.g., file, DB) | Leaky | May leak or escape its intended scope |
| `C` controls flow/errors           | No    | Can break safety and semantics        |

Caprese solve those issues, and track the need/use of `C`.

## tests

### Escape
In [Escape.test.scala](./Escape.test.scala):
It shows the problem with Context Functions and control flow.

### captureAwareFuncs
In [captureAwareFuncs](./captureAwareFuncs.scala):
It show how to manipulate functions like:
```scala
val f1: Context ?-> A -> B
val f2: A -> Context ?-> B
```
And show some interesting results. For example, it's safe to write:
```scala
def f(a: A): B

inline def g: A -> Context ?-> B = f

val h: (A -> B) | Null = Context.run(a => g(a))
```

# misc

The project is written with explicit Nulls, instead of Options, to explore if inlining can be more precise.
```scala
//> using option -language:strictEquality
//> using option -language:experimental.captureChecking
////> using option -Xprint:cc Prints
//> using option -Yexplicit-nulls
```
```scala
def f(a: A): B

inline def g: A -> Context ?-> B = f

val h: A -> B = Context.run(a => g(a))
```

# links
* [YAES, RT](https://github.com/rcardin/yaes/discussions/33)
* [Capture Checking Documentation](https://docs.scala-lang.org/scala3/reference/experimental/cc.html)
