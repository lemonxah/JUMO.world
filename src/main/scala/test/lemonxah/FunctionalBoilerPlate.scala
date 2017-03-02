package test.lemonxah
import language.higherKinds
/**
  * Project: test
  * Package: test.lemonxah
  * Created on 2/3/2017.
  * lemonxah aka lemonxah -
  * https://github.com/lemonxah
  * http://stackoverflow.com/users/2919672/lemon-xah
  */

/**
  * Functional boilerplate
  */

trait Functor[F[_]] {
  def map[A,B](fa: F[A])(f: A ⇒ B): F[B]
}

trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
}

trait Monad[F[_]] extends Applicative[F] {
  def bind[A,B](fa: F[A])(f: A ⇒ F[B]): F[B]
  override def map[A,B](fa: F[A])(f: A ⇒ B): F[B] = bind(fa)(a ⇒ pure(f(a)))
}

/**
  * More Functional boilerplate for syntax
  */
object FunctionalSyntax {
  implicit class FunctorOps[A, F[_]](fa: F[A])(implicit F: Functor[F]) {
    def map[B](f: A ⇒ B): F[B] = F.map(fa)(f)
  }
  implicit class MonadOps[A, F[_]](fa: F[A])(implicit M: Monad[F]) {
    def bind[B](f: A ⇒ F[B]): F[B] = M.bind(fa)(f)
    // flatMap for scala's monadic constructs
    def flatMap[B](f: A ⇒ F[B]): F[B] = M.bind(fa)(f)
  }
}

