package tools.devnull.jenkins.plugins.buildnotifications;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.JenkinsLocationConfiguration;

import java.io.IOException;

/**
 * A base class for all notifiers
 */
public abstract class BaseNotifier extends Notifier {

  private final String globalTarget;
  private final String successfulTarget;
  private final String brokenTarget;
  private final String stillBrokenTarget;
  private final String fixedTarget;
  private final boolean sendIfSuccess;

  /**
   * Creates a new notifier based on the given parameters
   *
   * @param globalTarget      the target for all notifications
   * @param successfulTarget  the target for build success notifications
   * @param brokenTarget      the target for broken build notifications
   * @param stillBrokenTarget the target for still broken build notifications
   * @param fixedTarget       the target for fixed build notifications
   * @param sendIfSuccess     if the notification should be sent if the build succeed
   */
  public BaseNotifier(String globalTarget,
                      String successfulTarget,
                      String brokenTarget,
                      String stillBrokenTarget,
                      String fixedTarget,
                      boolean sendIfSuccess) {
    this.globalTarget = globalTarget;
    this.successfulTarget = successfulTarget;
    this.brokenTarget = brokenTarget;
    this.stillBrokenTarget = stillBrokenTarget;
    this.fixedTarget = fixedTarget;
    this.sendIfSuccess = sendIfSuccess;
  }


  public String getGlobalTarget() {
    return globalTarget;
  }

  public String getSuccessfulTarget() {
    return successfulTarget;
  }

  public String getBrokenTarget() {
    return brokenTarget;
  }

  public String getStillBrokenTarget() {
    return stillBrokenTarget;
  }

  public String getFixedTarget() {
    return fixedTarget;
  }

  public boolean isSendIfSuccess() {
    return sendIfSuccess;
  }

  protected String getBaseUrl() {
    return JenkinsLocationConfiguration.get().getUrl();
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    String target = null;
    BuildStatus status = BuildStatus.of(build);
    switch (status) {
      case BROKEN:
        target = this.brokenTarget;
        break;
      case STILL_BROKEN:
        target = this.stillBrokenTarget;
        break;
      case FIXED:
        target = this.fixedTarget;
        break;
      case SUCCESSFUL:
        target = this.successfulTarget;
        break;
    }
    if (target == null || target.isEmpty()) {
      if (status != BuildStatus.SUCCESSFUL || sendIfSuccess) {
        target = this.globalTarget;
      }
    }
    if (target != null && !target.isEmpty()) {
      Message message = createMessage(target, build, launcher, listener);
      BuildNotifier notifier = new BuildNotifier(message, build, getBaseUrl());
      notifier.sendNotification();
    }
    return true;
  }

  protected abstract Message createMessage(String target,
                                           AbstractBuild<?, ?> build,
                                           Launcher launcher,
                                           BuildListener listener);

}
