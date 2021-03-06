package com.ambiata.ivory.core

import argonaut._, Argonaut._
import scalaz._, Scalaz._

case class Sized[A](value: A, bytes: Bytes) {
  def map[B](f: A => B): Sized[B] =
    Sized(f(value), bytes)
}

object Sized {
  implicit def SizedFunctor: Functor[Sized] = new Functor[Sized] {
    def map[A, B](v: Sized[A])(f: A => B) = v map f
  }

  implicit def SizedEqual[A: Equal]: Equal[Sized[A]] =
    Equal.equal((a, b) =>  a.value === b.value && a.bytes === b.bytes)

  implicit def SizedOrder[A: Order]: Order[Sized[A]] =
    Order.order((a, b) => (a.value -> a.bytes) ?|? (b.value -> b.bytes))

  implicit def SizedOrdering[A: Order]: scala.Ordering[Sized[A]] =
    SizedOrder[A].toScalaOrdering

  implicit def SizedEncodeJson[A: EncodeJson]: EncodeJson[Sized[A]] =
    jencode2L((s: Sized[A]) => s.value -> s.bytes)("value", "size")

  implicit def SizedDecodeJson[A: DecodeJson]: DecodeJson[Sized[A]] =
    jdecode2L(Sized.apply[A])("value", "size")
}
