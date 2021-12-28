# Type classes

Type class pattern components:

- type class itself: interface the represents some functionality for a generic type
- instances of a type class for particular types
- interface methods that accept instances of the type class as implicit parameters: interface objects and interface sintax


# Functional effects

https://blog.softwaremill.com/final-tagless-seen-alive-79a8d884691d

Functional effects are immutable (tree-like) data structures that merely describe
a sequence of operations.

At the end of the world, the data structure has to be impurely "interpreted"
to real world effects.

`IO[A]` is a description of an effect that when unsafely interpreted, will
succeed with a value of type A.

Type classes define common structure across types through lawful operations,
enabling abstraction

## Cats IO

A data type for encoding side effects as pure values, capable of expressing both synchronous
and asynchronous computations.

An IO is a data structure that represents just a description of a side effectful computation.

Effects described via this abstraction are not evaluated until the “end of the world”, which is
to say, when one of the “unsafe” methods are used.
Effectful results are not memoized, meaning that memory overhead is minimal (and no leaks),
and also that a single effect may be run multiple times in a referentially-transparent manner.




