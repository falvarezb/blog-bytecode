# Notes on FP

## Type constructors

A type constructor is an n-ary operator that takes as argument n types and
returns a new type

The different types of type constructors are called "kinds":

- `*`: nullary (0-arity) type constructor, represents the type of all types in the language (proper types)
- `* -> *`: unary (1-arity) type constructor, e.g. [] is the list type constructor
- `* -> * -> *`: binary (2-arity) type constructor (via currying), e.g. (_,_) is the pair type constructor
- `(* -> *) -> *`: higher-order type constructor: takes a unary type constructor and creates a proper type


Examples in Scala:

- Int: `*`
- List[Int]: `*`
- List: `* -> *`
- Either: `(*,*) -> *`
- Monad: `(* -> *) -> *`
- Monad[List]: `*`

Scala notation:

- A: `*`
- F[_]: `* -> *`
- B[_,_]: `(*,*) -> *`
- TC[_[_]]: `(* -> *) -> *`


## ADT

ADTs (Algebraic Data Types) are the foundation of data modelling in functional programming:

- product types, e.g. case classes
- sum (coproduct) types, e.g. sealed traits and their subclasses
- compound types, e.g. A with B


## Type classes

Type class pattern components:

- type class itself: interface the represents some functionality for a generic type
- instances of a type class for particular types
- interface methods that accept instances of the type class as implicit parameters: interface objects and interface syntax

### Some examples

__Semigroup__ -> represents data structures that can be combined

__Monoid__ -> Semigroup with identity element

__Group__ -> Monoid with inverse element

__Functor__ -> evaluates an effectful computation (a functor  "lifts" a function from one that cannot operate on an effect
to one that can work on a single effect, leaving the effect intact after the function is applied)

__Applicative__ -> evaluates multiple computations separately (they can fail without affecting the others)

__Monad__ -> chains multiple computations so that if one fails, the process stops

The last 3 examples are a way to model effects using only pure functions (the effectful part is taken
care of by the type classes)

##  Effects

Effects are interactions of a program with the outside world. Effects are unsafe operations as:

- files may be missing
- internet connections may fail
- database servers may be unavailable
- etc

Imperative programming does not address the subject of effects explicitly and does not distinguish between
safe and unsafe operations. For instance, you never know when a Java method may throw an exception or return a null
value.

On the other hand, functional programming encodes the existence of effects in the type system, e.g.

- `Optional` for missing values
- `Try` for exceptions
- `Future` for latency


[Functional effects](https://blog.softwaremill.com/final-tagless-seen-alive-79a8d884691d) are immutable (tree-like) data structures that merely describe
a sequence of operations.

At the end of the world, the data structure has to be impurely "interpreted"
to real world effects.

Effectul results are not memoized so that a single effect may be run multiple times in a
referentially transparent manner. Note that Scala Futures do not comply with this rule.

`IO[A]` is a description of an effect that when unsafely interpreted, will
succeed with a value of type A.

Type classes define common structure across types through lawful operations,
enabling abstraction

### Cats IO

A data type for encoding side effects as pure values, capable of expressing both synchronous
and asynchronous computations.

An IO is a data structure that represents just a description of a side effectful computation.

Effects described via this abstraction are not evaluated until the “end of the world”, which is
to say, when one of the “unsafe” methods are used.
Effectful results are not memoized, meaning that memory overhead is minimal (and no leaks),
and also that a single effect may be run multiple times in a referentially-transparent manner.




