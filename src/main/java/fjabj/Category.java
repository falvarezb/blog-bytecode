package fjabj;

import java.util.function.Function;

public class Category {

  public static void main(String[] args) {

    var x = 4;

    //Composition
    Function<Integer,Integer> f = i -> i*2;
    Function<Integer,Integer> g = i -> i+1;

    assert((f.andThen(g)).apply(x) != (g.andThen(f)).apply(x));

    //Properties of composition
    //1.associative
    Function<Integer,Integer> h = i -> i+2;

    assert(((f.andThen(g)).andThen(h)).apply(x) == (f.andThen(g.andThen(h))).apply(x));

    //2.identity
    Function id = i -> i;
    assert((f.andThen(id)).apply(x) == id.andThen(f).apply(x));

    //(Java already has a predefined identity function)
    assert(Function.<Integer>identity().apply(x) == x);

    Function<Integer,Integer> functionDerivedFromMethod = Category::myMethod;
    assert(functionDerivedFromMethod.apply(x) == x);

  }

  static int myMethod(int x){
    return x;
  }
}
