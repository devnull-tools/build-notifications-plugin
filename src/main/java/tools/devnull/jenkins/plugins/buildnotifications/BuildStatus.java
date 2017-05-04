package tools.devnull.jenkins.plugins.buildnotifications;

import hudson.model.AbstractBuild;
import hudson.model.Result;

/**
 * Enumeration of the possible build status for notification purposes.
 */
public enum BuildStatus {

  BROKEN("Broken"),
  STILL_BROKEN("Still Broken"),
  FIXED("Fixed"),
  SUCCESSFUL("Successful");

  private final String tag;

  BuildStatus(String tag) {
    this.tag = tag;
  }

  public String tag() {
    return this.tag;
  }

  public static BuildStatus of(AbstractBuild build) {
    AbstractBuild previousBuild = build.getPreviousBuild();
    if (build.getResult().ordinal == Result.SUCCESS.ordinal) {
      if (previousBuild != null) {
        return previousBuild.getResult().ordinal == Result.SUCCESS.ordinal ? SUCCESSFUL : FIXED;
      } else {
        return SUCCESSFUL;
      }
    } else {
      if (previousBuild != null) {
        return previousBuild.getResult().ordinal != Result.SUCCESS.ordinal ? STILL_BROKEN : BROKEN;
      } else {
        return BROKEN;
      }
    }
  }

}
