//> using scala 3.7.0

//> using option -Wconf:msg=(unused.*value|discarded.*value|pure.*statement):error
//> using option -Wnonunit-statement
//> using option -Wvalue-discard
//> using option -language:strictEquality
//> using option -language:experimental.captureChecking
////> using option -Xprint:cc Prints
//> using option -Yexplicit-nulls
//> using option -Xkind-projector:underscores


//> using compileOnly.dependencies org.scala-lang::scala3-compiler:3.7.0
//> using dependency io.getkyo::kyo-core::0.18.0
//> using dependency io.getkyo::kyo-direct::0.18.0
//> using dependency io.getkyo::kyo-prelude::0.18.0
//> using dependency io.getkyo::kyo-combinators::0.18.0

//> using dependency io.github.iltotore::iron:3.0.1
//> using dependency io.scalaland::chimney:2.0.0-M1

//> using test.dependency org.scalameta::munit::1.1.1