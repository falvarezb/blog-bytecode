package fjab;


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
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static fjab.Poset.buildPosetFromFile;
import static fjab.TransitivityMode.TRANSITIVE_EXPANSION;
import static fjab.TransitivityMode.TRANSITIVE_REDUCTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PosetTest {

  @Test
  @DisplayName("poset construction from an array")
  public void testBuildPosetFromArray() throws PosetException {
    int[][] array = new int[][]{
      {1, 1, 0, 0},
      {0, 1, 1, 0},
      {0, 0, 1, 0},
      {1, 0, 0, 1}
    };
    Poset poset = new Poset(array);
    assertArrayEquals(new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }, poset.getArrayRepresentation());

    assertEquals(10, poset.getNumberOfBinaryRelations());
    assertEquals(TRANSITIVE_EXPANSION, poset.getTransitivityMode());
  }

  @Nested
  @DisplayName("invalid poset")
  class InvalidPosetTest {
    @Test
    @DisplayName("violation of antisymmetry rule in the representation supplied")
    public void testAntisymmetryRuleViolation() {
      assertThrows(AntiSymmetryException.class, () -> buildPosetFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule.txt")));
    }

    @Test
    @DisplayName("violation of antisymmetry rule after transitive expansion")
    public void testTransitivityExpansionFailure() {
      assertThrows(InvalidPoset.class, () -> buildPosetFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule_after_transitive_rule.txt")));
    }

    @Test
    @DisplayName("violation of reflexivity rule")
    public void testReflexitivityRuleViolation() {
      assertThrows(ReflexivityException.class, () -> buildPosetFromFile(Paths.get("src/test/resources/poset_reflexivity_rule.txt")));
    }
  }

  @Nested
  @DisplayName("poset construction failure")
  class PosetConstructionFailureTest {
    @Test
    @DisplayName("illegal chars in file: non-numeric char")
    public void testBuildPosetFromFileWithIllegalChars() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> buildPosetFromFile(Paths.get("src/test/resources/illegal_non_numeric_in_file.txt")));
      assertEquals("The Poset representation must contain the numbers 1 and 0 only", e.getMessage());
    }

    @Test
    @DisplayName("illegal chars in file: number other than 0 or 1")
    public void testBuildPosetFromFileWithIllegalChars_2() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> buildPosetFromFile(Paths.get("src/test/resources/illegal_number_in_file.txt")));
      assertEquals("The Poset representation must contain the numbers 1 and 0 only", e.getMessage());
    }

    @Test
    @DisplayName("illegal elements in array: number other than 0 or 1")
    public void testBuildPosetFromArrayWithIllegalChars_2() {

      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset(new int[][]{
        {5, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }));
      assertEquals("The Poset representation must contain the numbers 1 and 0 only", e.getMessage());
    }
  }





  @ParameterizedTest
  @MethodSource("sortSupplier")
  @DisplayName("topological sort of poset")
  public void testSort(String fileName, int[] labelsArray) throws IOException, PosetException {
    int[] labels = buildPosetFromFile(Paths.get(fileName)).sort();
    assertArrayEquals(labels, labelsArray);
  }

  @ParameterizedTest
  @MethodSource("buildPosetFromFileSupplier")
  @DisplayName("poset construction from a file")
  public void testBuildPosetFromFile(String fileName, int[][] array, int numBinaryRelations, TransitivityMode transitivityMode) throws IOException, PosetException {
    Poset poset = buildPosetFromFile(Paths.get(fileName));
    assertArrayEquals(array, poset.getArrayRepresentation());
    assertEquals(numBinaryRelations, poset.getNumberOfBinaryRelations());
    assertEquals(transitivityMode, poset.getTransitivityMode());
  }

  @ParameterizedTest
  @MethodSource("transitiveReductionSupplier")
  @DisplayName("transitive reduction")
  public void testTransitiveReduction(String fileName, int[][] array, int numBinaryRelations, TransitivityMode transitivityMode) throws IOException, PosetException {
    Poset poset = buildPosetFromFile(Paths.get(fileName)).transitiveReduction();
    assertArrayEquals(array, poset.getArrayRepresentation());
    assertEquals(numBinaryRelations, poset.getNumberOfBinaryRelations());
    assertEquals(transitivityMode, poset.getTransitivityMode());
  }

  static Stream<Arguments> buildPosetFromFileSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }, 10, TRANSITIVE_EXPANSION),
      arguments("src/test/resources/poset_success_2.txt", new int[][]{
        {1, 0, 0, 1, 0, 1, 1, 1},
        {0, 1, 0, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 1, 0, 1, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }, 24, TRANSITIVE_EXPANSION),
      arguments("src/test/resources/poset_success_3.txt", new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {1, 1, 1, 0, 0},
        {1, 1, 1, 1, 0},
        {1, 1, 1, 1, 1}
      }, 15, TRANSITIVE_EXPANSION)
    );
  }

  static Stream<Arguments> transitiveReductionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      }, 7, TRANSITIVE_REDUCTION),
      arguments("src/test/resources/poset_success_2.txt", new int[][]{
        {1, 0, 0, 1, 0, 0, 0, 0},
        {0, 1, 0, 1, 1, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 0, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }, 17, TRANSITIVE_REDUCTION),
      arguments("src/test/resources/poset_success_3.txt", new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {0, 1, 1, 0, 0},
        {0, 0, 1, 1, 0},
        {0, 0, 0, 1, 1}
      }, 9, TRANSITIVE_REDUCTION)
    );
  }

  static Stream<Arguments> sortSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", new int[]{3,0,1,2}),
      arguments("src/test/resources/poset_success_2.txt", new int[]{1,0,2,3,4,5,6,7})
    );
  }

  @Property
  void transitiveExpansionAndReductionAreInverseOfEachOther(@ForAll("posetGenerator") Poset poset) {
    assertArrayEquals(new Poset(poset.transitiveReduction().getArrayRepresentation()).getArrayRepresentation(), poset.getArrayRepresentation());
    Statistics.collect(poset.getArrayRepresentation().length);
  }

  @Property
  void numberOfBinaryRelationsOfTransitiveReductionIsEqualOrLessThanTransitiveExpansion(@ForAll("posetGenerator") Poset poset) {
    assertTrue(poset.transitiveReduction().getNumberOfBinaryRelations() <= poset.getNumberOfBinaryRelations());
    Statistics.collect(poset.getArrayRepresentation().length);
  }

  @Property
  boolean transitiveReductionContainsTheMinimumNumberOfBinaryRelations(@ForAll("posetGenerator") Poset poset) {
    int[][] transitiveReductionArray = poset.transitiveReduction().getArrayRepresentation();
    for(int i=0; i<transitiveReductionArray.length; i++){
      for(int j=0; j<transitiveReductionArray.length; j++){
        if(transitiveReductionArray[i][j] == 1){
          transitiveReductionArray[i][j] = 0;
          try{
            Poset newPoset = new Poset(transitiveReductionArray);
            if(Arrays.deepEquals(newPoset.getArrayRepresentation(), poset.getArrayRepresentation())){
              return false;
            }
            transitiveReductionArray[i][j] = 1;
          }
          catch (PosetException e){}
        }
      }
    }
    Statistics.collect(poset.getArrayRepresentation().length);
    return true;
  }

  @Provide
  Arbitrary<Poset> posetGenerator() throws PosetException{
    Random random = new Random();
    return Arbitraries.integers().between(1,1000).flatMap(numRepetitions -> Arbitraries.integers().between(5,10).map(size -> {
      int[][] poset = new int[size][size];
      for(int i=0; i<size; i++) {
        poset[i][i] = 1;
        for(int j=i+1; j<size; j++) {
          poset[i][j] = random.nextBoolean() ? 1 : 0;
          poset[j][i] = poset[i][j] == 1 ? 0 : 1;
        }
      }
      try {
        return new Poset(poset);
      } catch (PosetException e) {
          return new Poset(new int[][]{{1}});
      }
    }).filter(poset -> poset.getArrayRepresentation().length > 1));

  }
}