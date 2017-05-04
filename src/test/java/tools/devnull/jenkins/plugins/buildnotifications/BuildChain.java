package tools.devnull.jenkins.plugins.buildnotifications;

import hudson.model.AbstractBuild;
import hudson.model.Result;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility class for creating a build history for testing purposes
 */
public class BuildChain {

  private final AbstractBuild build;

  private BuildChain(AbstractBuild actual, AbstractBuild next, Result result) {
    this.build = next;
    when(build.getResult()).thenReturn(result);
    when(next.getPreviousBuild()).thenReturn(actual);
    when(actual.getNextBuild()).thenReturn(next);
  }

  private BuildChain(Result result) {
    this.build = mock(AbstractBuild.class);
    when(build.getResult()).thenReturn(result);
  }

  public BuildChain thenFailed() {
    return new BuildChain(this.build, mock(AbstractBuild.class), Result.FAILURE);
  }

  public BuildChain thenSucceeded() {
    return new BuildChain(this.build, mock(AbstractBuild.class), Result.SUCCESS);
  }

  public AbstractBuild get() {
    return this.build;
  }

  public static BuildChain failed() {
    return new BuildChain(Result.FAILURE);
  }

  public static BuildChain succeeded() {
    return new BuildChain(Result.SUCCESS);
  }

}
