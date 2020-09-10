package fjab.poset;

import fjab.poset.error.AntiSymmetryException;
import fjab.poset.error.InvalidPosetException;
import fjab.poset.error.PosetException;
import fjab.poset.error.ReflexivityException;
import net.jqwik.api.*;
import net.jqwik.api.statistics.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static fjab.poset.PosetUtil.buildIncidenceMatrixFromFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PosetTest {

  @Nested
  @DisplayName("poset construction failure")
  class PosetConstructionFailureTest {
    @Test
    @DisplayName("incidence matrix must be NxN, where N is the number of elements")
    public void testIncidenceMatrixDimensions() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset<>(Arrays.asList("a", "b"), new int[][]{new int[]{1}}));
      assertEquals("The incidence matrix must be NxN, where N is the number of elements", e.getMessage());
    }

    @Test
    @DisplayName("the list of elements cannot contain duplicates")
    public void testNotDuplicateElements() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset<>(Arrays.asList("a", "b", "a"), new int[][]{
        {1,1,1},
        {0,1,1},
        {0,0,1}
      }));
      assertEquals("the list of elements cannot contain duplicates", e.getMessage());
    }

    @Test
    @DisplayName("illegal chars in file: non-numeric char")
    public void testBuildPosetFromFileWithIllegalChars() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset<>(Arrays.asList("a", "b", "c", "d"), PosetUtil.buildIncidenceMatrixFromFile(Paths.get("src/test/resources/illegal_non_numeric_in_file.txt"))));
      assertEquals("The file cannot have non-numeric chars", e.getMessage());
    }

    @Test
    @DisplayName("illegal chars in file: number other than 0 or 1")
    public void testBuildPosetFromFileWithIllegalChars_2() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset<>(Arrays.asList("a", "b", "c", "d"), PosetUtil.buildIncidenceMatrixFromFile(Paths.get("src/test/resources/illegal_number_in_file.txt"))));
      assertEquals("The incidence matrix must contain the numbers 1 and 0 only", e.getMessage());
    }

    @Test
    @DisplayName("illegal elements in incidence matrix: number other than 0 or 1")
    public void testBuildPosetFromArrayWithIllegalChars() {

      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset<>(Arrays.asList("a", "b", "c", "d"), new int[][]{
        {5, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }));
      assertEquals("The incidence matrix must contain the numbers 1 and 0 only", e.getMessage());
    }
  }

  @Nested
  @DisplayName("equality test")
  class EqualityTest {
    @Test
    @DisplayName("2 Posets with the same constructor parameters are equal")
    public void testEqualPosetsWithSameConstructorParameters() {
      List<String> elements = Arrays.asList("a", "b", "c", "d");
      int[][] binaryRepresentation = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };

      Poset<String> poset1 = new Poset<>(elements, binaryRepresentation);
      Poset<String> poset2 = new Poset<>(elements, binaryRepresentation);
      assertEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets are equal if they contain the same elements and have the same transitive expansion")
    public void testEqualPosetsWithSameElementsAndSameTransitiveExpansion() {
      List<String> elements1 = Arrays.asList("a", "b", "c", "d");
      List<String> elements2 = Arrays.asList("d", "b", "c", "a");
      int[][] binaryRepresentation1 = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };
      int[][] binaryRepresentation2 = new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      };

      Poset<String> poset1 = new Poset<>(elements1, binaryRepresentation1);
      Poset<String> poset2 = new Poset<>(elements2, binaryRepresentation2);
      assertEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets with different elements are not equal")
    public void testPosetsWithDifferentElements() {
      List<String> elements1 = Arrays.asList("a", "b", "c", "d");
      List<String> elements2 = Arrays.asList("d", "b", "c", "x");
      int[][] binaryRepresentation = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };

      Poset<String> poset1 = new Poset<>(elements1, binaryRepresentation);
      Poset<String> poset2 = new Poset<>(elements2, binaryRepresentation);
      assertNotEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets with different transitive expansion are not equal")
    public void testPosetsWithDifferentTransitiveExpansion() {
      List<String> elements = Arrays.asList("a", "b", "c", "d");
      int[][] binaryRepresentation1 = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };
      int[][] binaryRepresentation2 = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };

      Poset<String> poset1 = new Poset<>(elements, binaryRepresentation1);
      Poset<String> poset2 = new Poset<>(elements, binaryRepresentation2);
      assertNotEquals(poset1, poset2);
    }
  }

  @Nested
  @DisplayName("invalid poset")
  class InvalidPosetTest {
    @Test
    @DisplayName("violation of antisymmetry rule in incidence matrix")
    public void testAntisymmetryRuleViolation() {
      assertThrows(AntiSymmetryException.class, () -> new Poset<>(Arrays.asList("a", "b", "c", "d"), buildIncidenceMatrixFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule.txt"))));
    }

    @Test
    @DisplayName("violation of antisymmetry rule after transitive expansion")
    public void testTransitivityExpansionFailure() {
      assertThrows(InvalidPosetException.class, () -> new Poset<>(Arrays.asList("a", "b", "c", "d"), buildIncidenceMatrixFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule_after_transitive_rule.txt"))));
    }

    @Test
    @DisplayName("violation of reflexivity rule")
    public void testReflexivityRuleViolation() {
      assertThrows(ReflexivityException.class, () -> new Poset<>(Arrays.asList("a", "b", "c", "d"), buildIncidenceMatrixFromFile(Paths.get("src/test/resources/poset_reflexivity_rule.txt"))));
    }
  }


  @ParameterizedTest
  @MethodSource("sortSupplier")
  @DisplayName("topological sort of poset")
  public void testSort(String fileName, List<String> elements, List<String> elementsInExpectedOrder) throws IOException, PosetException {
    assertIterableEquals(elementsInExpectedOrder, new Poset<>(elements, buildIncidenceMatrixFromFile(Paths.get(fileName))));
  }

  static Stream<Arguments> sortSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", Arrays.asList("a", "b", "c", "d"), Arrays.asList("d", "a", "b", "c")),
      arguments("src/test/resources/poset_success_2.txt", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), Arrays.asList("b", "a", "c", "d", "e", "f", "g", "h")),
      arguments("src/test/resources/poset_success_3.txt", Arrays.asList("a", "b", "c", "d", "e"), Arrays.asList("e", "d", "c", "b", "a"))
    );
  }

  @ParameterizedTest
  @MethodSource("transitiveExpansionSupplier")
  @DisplayName("transitive expansion")
  public void testTransitiveExpansion(String fileName, List<String> elements, int[][] array) throws IOException, PosetException {
    Poset<String> poset = new Poset<>(elements, buildIncidenceMatrixFromFile(Paths.get(fileName)));
    assertArrayEquals(array, poset.getTransitiveExpansion());
  }

  static Stream<Arguments> transitiveExpansionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt",
        Arrays.asList("a", "b", "c", "d"),
        new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }),
      arguments("src/test/resources/poset_success_2.txt",
        Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"),
        new int[][]{
        {1, 0, 0, 1, 0, 1, 1, 1},
        {0, 1, 0, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 1, 0, 1, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }),
      arguments("src/test/resources/poset_success_3.txt",
        Arrays.asList("a", "b", "c", "d", "e"),
        new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {1, 1, 1, 0, 0},
        {1, 1, 1, 1, 0},
        {1, 1, 1, 1, 1}
      })
    );
  }

  @ParameterizedTest
  @MethodSource("transitiveReductionSupplier")
  @DisplayName("transitive reduction")
  public void testTransitiveReduction(String fileName, List<String> elements, int[][] array) throws IOException, PosetException {
    Poset<String> poset = new Poset<>(elements, buildIncidenceMatrixFromFile(Paths.get(fileName)));
    assertArrayEquals(array, poset.getTransitiveReduction());
  }

  static Stream<Arguments> transitiveReductionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt",
        Arrays.asList("a", "b", "c", "d"),
        new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      }),
      arguments("src/test/resources/poset_success_2.txt",
        Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"),
        new int[][]{
        {1, 0, 0, 1, 0, 0, 0, 0},
        {0, 1, 0, 1, 1, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 0, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }),
      arguments("src/test/resources/poset_success_3.txt",
        Arrays.asList("a", "b", "c", "d", "e"),
        new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {0, 1, 1, 0, 0},
        {0, 0, 1, 1, 0},
        {0, 0, 0, 1, 1}
      })
    );
  }

  //===================================================//
  //============== PROPERTY-BASED TESTS ===============//
  //===================================================//

  private List<Integer> elements(int n) {
    return IntStream.range(0,n).boxed().collect(Collectors.toList());
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("topological sort is preserved when creating a Poset based on the transitive expansion of the incidence matrix of another")
  void topologicalSortIsPreserved_transitiveExpansion(@ForAll("posetGenerator") Poset<Integer> poset) {
    assertEquals(new ArrayList<>(new Poset<>(elements(poset.getTransitiveExpansion().length), poset.getTransitiveExpansion())), new ArrayList<>(poset));
    Statistics.collect(poset.getTransitiveExpansion().length);
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("topological sort is preserved when creating a Poset based on the transitive reduction of the incidence matrix of another")
  void topologicalSortIsPreserved_transitiveReduction(@ForAll("posetGenerator") Poset<Integer> poset) {
    assertEquals(new ArrayList<>(new Poset<>(elements(poset.getTransitiveExpansion().length), poset.getTransitiveReduction())), new ArrayList<>(poset));
    Statistics.collect(poset.getTransitiveExpansion().length);
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("The transitive expansion contains the maximum possible number of binary relations")
  /*
    Demonstration: for a given Poset, add binary relations one at a time. If the resulting Poset after expansion
    equals the original one, then we have found a representation of the Poset with 1 more binary relation than the
    transitive expansion.
   */
  boolean transitiveExpansionContainsTheMaximumNumberOfBinaryRelations(@ForAll("posetGenerator") Poset<Integer> poset) {
    int[][] transitiveExpansionArray = poset.getTransitiveExpansion();
    for (int i = 0; i < transitiveExpansionArray.length; i++) {
      for (int j = 0; j < transitiveExpansionArray.length; j++) {
        if (transitiveExpansionArray[i][j] == 0) {
          //add 1 binary relation
          transitiveExpansionArray[i][j] = 1;
          try {
            //check if the resulting Poset equals the original one
            Poset<Integer> newPoset = new Poset<>(new ArrayList<>(poset), transitiveExpansionArray);
            if (newPoset.equals(poset)) {
              return false;
            }
            //restore the binary relation
            transitiveExpansionArray[i][j] = 0;
          } catch (PosetException ignored) {
          }
        }
      }
    }
    Statistics.collect(poset.getTransitiveExpansion().length);
    return true;
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("The transitive reduction contains the minimum possible number of binary relations")
  /*
    Demonstration: for a given Poset, remove its binary relations one at a time. If the resulting Poset after expansion
    equals the original one, then we have found a representation of the Poset with 1 fewer binary relation than the
    transitive reduction.
   */
  boolean transitiveReductionContainsTheMinimumNumberOfBinaryRelations(@ForAll("posetGenerator") Poset<Integer> poset) {
    int[][] transitiveReductionArray = poset.getTransitiveReduction();
    for (int i = 0; i < transitiveReductionArray.length; i++) {
      for (int j = 0; j < transitiveReductionArray.length; j++) {
        if (transitiveReductionArray[i][j] == 1) {
          //remove 1 binary relation
          transitiveReductionArray[i][j] = 0;
          try {
            //check if the resulting Poset equals the original one
            Poset<Integer> newPoset = new Poset<>(new ArrayList<>(poset), transitiveReductionArray);
            if (newPoset.equals(poset)) {
              return false;
            }
            //restore the binary relation
            transitiveReductionArray[i][j] = 1;
          } catch (PosetException ignored) {
          }
        }
      }
    }
    Statistics.collect(poset.getTransitiveExpansion().length);
    return true;
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("Topological sort is correct")
  /*
    Demonstration: for a given sort [X1, X2 ... Xn], given any 2 elements Xi,Xj such that i<j, then Xji == 0
   */
  boolean topologicalSort(@ForAll("posetGenerator") Poset<Integer> poset) {
    int[][] incidenceMatrix = poset.getTransitiveExpansion();
    int[] topologicalSort = poset.stream().mapToInt(i -> i).toArray();
    for (int i = 0; i < topologicalSort.length; i++) {
      for (int j = i+1; j < topologicalSort.length; j++) {
        if (incidenceMatrix[topologicalSort[j]][topologicalSort[i]] == 1) {
          return false;
        }
      }
    }
    Statistics.collect(poset.getTransitiveExpansion().length);
    return true;
  }

  /*
    Because of the reflexivity and antisymmetry properties, an incidence matrix of dimensions NxN
    has 1+2+...+N-1 degrees of freedom and by the formula of the arithmetic series:
    1+2+...+N-1 = N*(N-1)/2

    Examples:

    3x3 -> 2+1 degrees of freedom
    1 f f
    o 1 f
    o o 1

    4x4 -> 3+2+1 degrees of freedom
    1 f f f
    o 1 f f
    o o 1 f
    o o o 1

    Therefore, in order to generate random NxN incidence matrices, we need to consider permutations of
    N*(N-1)/2 elements from a population of 2 elements (0 and 1) with replacement.

    Therefore, the total number of NxN incidence matrices is 2^(N*(N-1)/2)

    For simplicity, we will take as elements of the Poset a list of N integers ranging from 0 to N-1
   */
  @Provide
  Arbitrary<Poset<Integer>> posetGenerator() throws PosetException {
    Integer[] posetSizes = new Integer[]{3,4,5,6};
    return Arbitraries.of(posetSizes)
      .flatMap(n -> {
        int degreesOfFreedom = n * (n - 1) / 2;
        return new PermutationWithRepetitionArbitrary<>(java.util.Arrays.asList(0, 1), degreesOfFreedom).map(permutation -> {
          int[][] incidenceMatrix = new int[n][n];
          int counter = 0;
          for (int i = 0; i < n; i++) {
            incidenceMatrix[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
              incidenceMatrix[i][j] = permutation.get(counter++);
              incidenceMatrix[j][i] = incidenceMatrix[i][j] == 1 ? 0 : 1;
            }
          }

          try {
            List<Integer> elements = elements(n);
            return new Poset<>(elements, incidenceMatrix);
          } catch (PosetException e) {
            return null;
          }
        })//Discard invalid posets
          .filter(Objects::nonNull)
          ;
      });
  }

}