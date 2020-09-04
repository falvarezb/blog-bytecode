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
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PosetTest {

  @Nested
  @DisplayName("invalid poset")
  class InvalidPosetTest {
    @Test
    @DisplayName("violation of antisymmetry rule in the representation supplied")
    public void testAntisymmetryRuleViolation() {
      assertThrows(AntiSymmetryException.class, () -> new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule.txt"))));
    }

    @Test
    @DisplayName("violation of antisymmetry rule after transitive expansion")
    public void testTransitivityExpansionFailure() {
      assertThrows(InvalidPosetException.class, () -> new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule_after_transitive_rule.txt"))));
    }

    @Test
    @DisplayName("violation of reflexivity rule")
    public void testReflexivityRuleViolation() {
      assertThrows(ReflexivityException.class, () -> new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get("src/test/resources/poset_reflexivity_rule.txt"))));
    }
  }

  @Nested
  @DisplayName("poset construction failure")
  class PosetConstructionFailureTest {
    @Test
    @DisplayName("illegal chars in file: non-numeric char")
    public void testBuildPosetFromFileWithIllegalChars() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get("src/test/resources/illegal_non_numeric_in_file.txt"))));
      assertEquals("The file cannot have non-numeric chars", e.getMessage());
    }

    @Test
    @DisplayName("illegal chars in file: number other than 0 or 1")
    public void testBuildPosetFromFileWithIllegalChars_2() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get("src/test/resources/illegal_number_in_file.txt"))));
      assertEquals("The binary relations representation must contain the numbers 1 and 0 only", e.getMessage());
    }

    @Test
    @DisplayName("illegal elements in array: number other than 0 or 1")
    public void testBuildPosetFromArrayWithIllegalChars() {

      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset(new int[][]{
        {5, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }));
      assertEquals("The binary relations representation must contain the numbers 1 and 0 only", e.getMessage());
    }

    @Test
    @DisplayName("illegal array dimensions: it must be a square matrix")
    public void testBuildPosetFromArrayWithIllegalDimensions() {

      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset(new int[][]{
        {1, 1, 0}
      }));
      assertEquals("the array must be a NxN matrix, where N is the number of elements", e.getMessage());
    }
  }

  @Nested
  @DisplayName("equality test")
  class EqualityTest {
    @Test
    @DisplayName("2 Posets with the same constructor parameters are equal")
    public void testEqualPosetsWithSameConstructorParameters() {
      Poset poset1 = new Poset(new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      });
      Poset poset2 = new Poset(new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      });
      assertEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets are equal if they have the same transitive expansion")
    public void testEqualPosetsWithSameTransitiveExpansion() {
      Poset poset1 = new Poset(new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      });
      Poset poset2 = new Poset(new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      });
      assertEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets with different transitive expansion are not equal")
    public void testPosetsWithDifferentTransitiveExpansion() {
      Poset poset1 = new Poset(new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      });
      Poset poset2 = new Poset(new int[][]{
        {1, 1, 1, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      });
      assertNotEquals(poset1, poset2);
    }
  }


  @ParameterizedTest
  @MethodSource("sortSupplier")
  @DisplayName("topological sort of poset")
  public void testSort(String fileName, int[] labelsArray) throws IOException, PosetException {
    int[] labels = new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get(fileName))).sort();
    assertArrayEquals(labels, labelsArray);
  }

  @ParameterizedTest
  @MethodSource("transitiveExpansionSupplier")
  @DisplayName("transitive expansion")
  public void testTransitiveExpansion(String fileName, int[][] array, int numBinaryRelations) throws IOException, PosetException {
    Poset poset = new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get(fileName)));
    assertArrayEquals(array, poset.getExpandedArray());
    assertEquals(numBinaryRelations, poset.getNumberOfExpandedBinaryRelations());
  }

  @ParameterizedTest
  @MethodSource("transitiveReductionSupplier")
  @DisplayName("transitive reduction")
  public void testTransitiveReduction(String fileName, int[][] array, int numBinaryRelations) throws IOException, PosetException {
    Poset poset = new Poset(Poset2.buildBinaryRelationsFromFile(Paths.get(fileName)));
    assertArrayEquals(array, poset.getReducedArray());
    assertEquals(numBinaryRelations, poset.getNumberOfReducedBinaryRelations());
  }

  static Stream<Arguments> transitiveExpansionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }, 10),
      arguments("src/test/resources/poset_success_2.txt", new int[][]{
        {1, 0, 0, 1, 0, 1, 1, 1},
        {0, 1, 0, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 1, 0, 1, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }, 24),
      arguments("src/test/resources/poset_success_3.txt", new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {1, 1, 1, 0, 0},
        {1, 1, 1, 1, 0},
        {1, 1, 1, 1, 1}
      }, 15)
    );
  }

  static Stream<Arguments> transitiveReductionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      }, 7),
      arguments("src/test/resources/poset_success_2.txt", new int[][]{
        {1, 0, 0, 1, 0, 0, 0, 0},
        {0, 1, 0, 1, 1, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 0, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }, 17),
      arguments("src/test/resources/poset_success_3.txt", new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {0, 1, 1, 0, 0},
        {0, 0, 1, 1, 0},
        {0, 0, 0, 1, 1}
      }, 9)
    );
  }

  static Stream<Arguments> sortSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", new int[]{3, 0, 1, 2}),
      arguments("src/test/resources/poset_success_2.txt", new int[]{1, 0, 2, 3, 4, 5, 6, 7}),
      arguments("src/test/resources/poset_success_3.txt", new int[]{4, 3, 2, 1, 0})
    );
  }


  //===================================================//
  //============== PROPERTY-BASED TESTS ===============//
  //===================================================//

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("TE(TE(arr)) == TE(arr)")
  void transitiveExpansionIsIdempotent(@ForAll("posetGenerator") Poset poset) {
    assertArrayEquals(new Poset(poset.getExpandedArray()).getExpandedArray(), poset.getExpandedArray());
    Statistics.collect(poset.getExpandedArray().length);
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("TE(TR(TE(arr))) == TE(arr)")
  void transitiveExpansionAndReductionAreInverseOfEachOther(@ForAll("posetGenerator") Poset poset) {
    assertArrayEquals(new Poset(new Poset(poset.getExpandedArray()).getReducedArray()).getExpandedArray(), poset.getExpandedArray());
    Statistics.collect(poset.getExpandedArray().length);
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("Num(TR(arr)) <= Num(TE(arr))")
  void numberOfBinaryRelationsOfTransitiveReductionIsEqualOrLessThanTransitiveExpansion(@ForAll("posetGenerator") Poset poset) {
    assertTrue(poset.getNumberOfReducedBinaryRelations() <= poset.getNumberOfExpandedBinaryRelations());
    Statistics.collect(poset.getExpandedArray().length);
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("The transitive expansion contains the maximum possible number of binary relations")
  /*
    Demonstration: for a given Poset, add binary relations one at a time. If the resulting Poset after expansion
    equals the original one, then we have found a representation of the Poset with 1 more binary relation than the
    transitive expansion.
   */
  boolean transitiveExpansionContainsTheMaximumNumberOfBinaryRelations(@ForAll("posetGenerator") Poset poset) {
    int[][] transitiveExpansionArray = poset.getExpandedArray();
    for (int i = 0; i < transitiveExpansionArray.length; i++) {
      for (int j = 0; j < transitiveExpansionArray.length; j++) {
        if (transitiveExpansionArray[i][j] == 0) {
          //add 1 binary relation
          transitiveExpansionArray[i][j] = 1;
          try {
            //check if the resulting Poset equals the original one
            Poset newPoset = new Poset(transitiveExpansionArray);
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
    Statistics.collect(poset.getExpandedArray().length);
    return true;
  }

  @Property(edgeCases = EdgeCasesMode.NONE)
  @Label("The transitive reduction contains the minimum possible number of binary relations")
  /*
    Demonstration: for a given Poset, remove its binary relations one at a time. If the resulting Poset after expansion
    equals the original one, then we have found a representation of the Poset with 1 fewer binary relation than the
    transitive reduction.
   */
  boolean transitiveReductionContainsTheMinimumNumberOfBinaryRelations(@ForAll("posetGenerator") Poset poset) {
    int[][] transitiveReductionArray = poset.getReducedArray();
    for (int i = 0; i < transitiveReductionArray.length; i++) {
      for (int j = 0; j < transitiveReductionArray.length; j++) {
        if (transitiveReductionArray[i][j] == 1) {
          //remove 1 binary relation
          transitiveReductionArray[i][j] = 0;
          try {
            //check if the resulting Poset equals the original one
            Poset newPoset = new Poset(transitiveReductionArray);
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
    Statistics.collect(poset.getExpandedArray().length);
    return true;
  }

  /*
    3x3 -> 2+1 degrees of freedom
    1 f f
    o 1 f -->
    o o 1

    4x4 -> 3+2+1 degrees of freedom
    1 f f f
    o 1 f f
    o o 1 f
    o o o 1

    In general, NxN has 1+2+...+N-1 degrees of freedom, and by the formula of the arithmetic series:
    1+2+...+N-1 = N*(N-1)/2
   */
  @Provide
  Arbitrary<Poset> posetGenerator() throws PosetException {
    return Arbitraries.of(3,4,5,6)
      .flatMap(n -> {
        int degreesOfFreedom = n * (n - 1) / 2;
        return new PermutationWithRepetitionArbitrary<>(java.util.Arrays.asList(0, 1), degreesOfFreedom).map(permutation -> {
          int[][] poset = new int[n][n];
          int counter = 0;
          for (int i = 0; i < n; i++) {
            poset[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
              poset[i][j] = permutation.get(counter++);
              poset[j][i] = poset[i][j] == 1 ? 0 : 1;
            }
          }

          try {
            return new Poset(poset);
          } catch (PosetException e) {
            return null;
          }
        })//Discard invalid posets
          .filter(Objects::nonNull)
          ;
      });
  }
}