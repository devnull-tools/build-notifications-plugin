/*
 * The MIT License
 *
 * Copyright (c) 2016 Marcelo "Ataxexe" Guimarães <ataxexe@devnull.tools>
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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * A notifier that uses Boteco to delivery messages
 *
 * @author Ataxexe
 */
public class BotecoNotifier extends Notifier {

  private final String eventId;
  private final boolean sendIfSuccess;

  /**
   * Creates a new notifier based on the given parameters
   *
   * @param eventId       the eventId to broadcast
   * @param sendIfSuccess if the notification should be sent if the build succeed
   */
  @DataBoundConstructor
  public BotecoNotifier(String eventId, boolean sendIfSuccess) {
    this.eventId = eventId;
    this.sendIfSuccess = sendIfSuccess;
  }

  public boolean isSendIfSuccess() {
    return sendIfSuccess;
  }

  public String getEventId() {
    return eventId;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    BotecoDescriptor descriptor = (BotecoDescriptor) Jenkins.getInstance()
        .getDescriptor(BotecoNotifier.class);
    Message message = new BotecoMessage(eventId, descriptor.endpoint);
    BuildNotifier notifier = new BuildNotifier(message, build, sendIfSuccess);
    notifier.sendNotification();
    return true;
  }

  /**
   * The descriptor for the BotecoNotifier plugin
   */
  @Extension
  public static class BotecoDescriptor extends BuildStepDescriptor<Publisher> {

    private String endpoint;

    public BotecoDescriptor() {
      load();
    }

    public String getEndpoint() {
      return endpoint;
    }

    public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
      JSONObject config = json.getJSONObject("boteco");
      this.endpoint = config.getString("endpoint");
      save();
      return true;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Boteco Notification";
    }

  }

}

