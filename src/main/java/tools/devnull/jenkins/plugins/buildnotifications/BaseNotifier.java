/*
 * The MIT License
 *
 * Copyright (c) 2016-2017 Marcelo "Ataxexe" Guimarães
 * <ataxexe@devnull.tools>
 *
 * ----------------------------------------------------------------------
 * Permission  is hereby granted, free of charge, to any person obtaining
 * a  copy  of  this  software  and  associated  documentation files (the
 * "Software"),  to  deal  in the Software without restriction, including
 * without  limitation  the  rights to use, copy, modify, merge, publish,
 * distribute,  sublicense,  and/or  sell  copies of the Software, and to
 * permit  persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this  permission  notice  shall be
 * included  in  all  copies  or  substantial  portions  of the Software.
 *                        -----------------------
 * THE  SOFTWARE  IS  PROVIDED  "AS  IS",  WITHOUT  WARRANTY OF ANY KIND,
 * EXPRESS  OR  IMPLIED,  INCLUDING  BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN  NO  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM,  DAMAGES  OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT  OR  OTHERWISE,  ARISING  FROM,  OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE   OR   THE   USE   OR   OTHER   DEALINGS  IN  THE  SOFTWARE.
 */

package tools.devnull.jenkins.plugins.buildnotifications;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.util.LogTaskListener;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * A base class for all notifiers.
 *
 * @author Ataxexe
 */
public abstract class BaseNotifier extends Notifier {

  private static final Logger logger = Logger.getLogger(TelegramNotifier.class.getName());

  private final String globalTarget;
  private final String successfulTarget;
  private final String brokenTarget;
  private final String stillBrokenTarget;
  private final String fixedTarget;
  private final boolean sendIfSuccess;
  private final String extraMessage;

  /**
   * Creates a new notifier based on the given parameters
   *
   * @param globalTarget      the target for all notifications
   * @param successfulTarget  the target for build success notifications
   * @param brokenTarget      the target for broken build notifications
   * @param stillBrokenTarget the target for still broken build notifications
   * @param fixedTarget       the target for fixed build notifications
   * @param sendIfSuccess     if the notification should be sent if the build succeed
   * @param extraMessage      the extra message
   */
  public BaseNotifier(String globalTarget,
                      String successfulTarget,
                      String brokenTarget,
                      String stillBrokenTarget,
                      String fixedTarget,
                      boolean sendIfSuccess,
                      String extraMessage) {
    this.globalTarget = globalTarget;
    this.successfulTarget = successfulTarget;
    this.brokenTarget = brokenTarget;
    this.stillBrokenTarget = stillBrokenTarget;
    this.fixedTarget = fixedTarget;
    this.sendIfSuccess = sendIfSuccess;
    this.extraMessage = extraMessage;
  }

  /**
   * Replace Env variables to value.
   * @param build
   * @param message
   * @return
   */
  public String replaceEnvString(AbstractBuild build, String message) {
    EnvVars envVars = new EnvVars();

    try {
      envVars = build.getEnvironment(new LogTaskListener(logger, Level.INFO));
    } catch (IOException | InterruptedException e) {
      logger.log(SEVERE, e.getMessage(), e);
    }

    return envVars.expand(message);
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

  public String getExtraMessage() {
    return extraMessage;
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
    // allow to send to null target
    if (target != null && target.trim().isEmpty()) {
      target = null;
    }
    // but check if message was builded
    Message message = createMessage(target, build, launcher, listener);
    if(null != message){
      BuildNotifier notifier = createNotifier(build, message);
      notifier.sendNotification();
    }

    return true;
  }

  protected BuildNotifier createNotifier(AbstractBuild<?, ?> build, Message message) {
    return new BuildNotifier(message, build, Jenkins.getInstance().getRootUrl());
  }

  /**
   * Creates the message for notifying users about the build. The parameters are the same passed to
   * {@link #perform(AbstractBuild, Launcher, BuildListener)} plus the {@code target} of the message.
   */
  protected abstract Message createMessage(String target,
                                           AbstractBuild<?, ?> build,
                                           Launcher launcher,
                                           BuildListener listener);

}
