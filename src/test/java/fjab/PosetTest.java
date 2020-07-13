package fjab;


import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static fjab.Poset.buildPosetFromFile;
import static org.junit.Assert.assertArrayEquals;

public class PosetTest {

  @Test
  public void buildPoset_success_1() throws IOException, PosetException {
    Poset poset = buildPosetFromFile(Paths.get("src/test/resources/poset_success_1.txt"));
    assertArrayEquals(poset.getArrayRepresentation(), new int[][]{
      {1,1,1,0},
      {0,1,1,0},
      {0,0,1,0},
      {1,1,1,1}
    });
  }

  @Test
  public void buildPoset_success_2() throws IOException, PosetException {
    Poset poset = buildPosetFromFile(Paths.get("src/test/resources/poset_success_2.txt"));
    assertArrayEquals(poset.getArrayRepresentation(), new int[][]{
      {1,0,0,1,0,1,1,1},
      {0,1,0,1,1,1,1,1},
      {0,0,1,0,1,0,1,1},
      {0,0,0,1,0,1,1,1},
      {0,0,0,0,1,0,1,0},
      {0,0,0,0,0,1,0,0},
      {0,0,0,0,0,0,1,0},
      {0,0,0,0,0,0,0,1}
    });
  }

  @Test(expected = AntiSymmetryException.class)
  public void buildPoset_antisymmetric_rule_violation() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule.txt"));
  }

  @Test(expected = TransitivityException.class)
  public void buildPoset_failure_to_apply_transitivity_rule() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule_after_transitive_rule.txt"));
  }

  @Test(expected = ReflexivityException.class)
  public void buildPoset_reflexitivity_rule_violation() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_reflexitivity_rule.txt"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildPoset_illegal_element() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_illegal_element.txt"));
  }

  @Test
  public void sort1() throws IOException, PosetException {
    int[] labels = buildPosetFromFile(Paths.get("src/test/resources/poset_success_1.txt")).sort();
    assertArrayEquals(labels, new int[]{3,0,1,2});
  }

  @Test
  public void sort2() throws IOException, PosetException {
    int[] labels = buildPosetFromFile(Paths.get("src/test/resources/poset_success_2.txt")).sort();
    assertArrayEquals(labels, new int[]{1,0,2,3,4,5,6,7});
  }

}