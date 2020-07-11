package fjab;


import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static fjab.Poset.buildPosetFromFile;
import static org.junit.Assert.assertArrayEquals;

public class PosetTest {

  @Test
  public void buildPoset_success() throws IOException, PosetException {
    Poset poset = buildPosetFromFile(Paths.get("src/test/resources/poset_success.txt"));
    assertArrayEquals(poset.getArrayRepresentation(), new int[][]{{1,1,1,0},{0,1,1,0},{0,0,1,0},{1,1,1,1}});
  }

  @Test(expected = AntiSymmetryException.class)
  public void buildPoset_antisymmetric_rule_violation() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule.txt"));
  }

  @Test(expected = TransitivityException.class)
  public void buildPoset_failure_to_apply_transitivity_rule() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_antisymmetry_rule_after_transitive_rule.txt"));
  }

  @Test(expected = ReflexitivityException.class)
  public void buildPoset_reflexitivity_rule_violation() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_reflexitivity_rule.txt"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildPoset_illegal_element() throws IOException, PosetException {
    buildPosetFromFile(Paths.get("src/test/resources/poset_illegal_element.txt"));
  }

}