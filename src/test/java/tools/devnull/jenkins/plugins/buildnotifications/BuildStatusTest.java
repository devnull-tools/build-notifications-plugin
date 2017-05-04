package tools.devnull.jenkins.plugins.buildnotifications;

import org.junit.Test;
import tools.devnull.kodo.Spec;

import java.util.function.Supplier;

import static tools.devnull.jenkins.plugins.buildnotifications.BuildChain.*;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.BROKEN;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.FIXED;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.STILL_BROKEN;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.SUCCESSFUL;
import static tools.devnull.kodo.Expectation.to;

public class BuildStatusTest {

  @Test
  public void test() {
    Spec.begin()
        .expect(statusOf(failed().thenFailed()), to().be(STILL_BROKEN))
        .expect(statusOf(succeeded().thenFailed()), to().be(BROKEN))
        .expect(statusOf(failed()), to().be(BROKEN))

        .expect(statusOf(succeeded()), to().be(SUCCESSFUL))
        .expect(statusOf(succeeded().thenSucceeded()), to().be(SUCCESSFUL))
        .expect(statusOf(failed().thenSucceeded()), to().be(FIXED));
  }

  private Supplier<BuildStatus> statusOf(BuildChain buildChain) {
    return () -> BuildStatus.of(buildChain.get());
  }

}
