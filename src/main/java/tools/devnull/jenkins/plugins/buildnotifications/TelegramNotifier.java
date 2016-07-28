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
 * A notifier that uses Telegram to delivery messages
 *
 * @author Ataxexe
 */
public class TelegramNotifier extends Notifier {

  private final String chatId;
  private final boolean sendIfSuccess;

  /**
   * Creates a new notifier based on the given parameters
   *
   * @param chatId        the telegram chat id
   * @param sendIfSuccess if the notification should be sent if the build succeed
   */
  @DataBoundConstructor
  public TelegramNotifier(String chatId, boolean sendIfSuccess) {
    this.chatId = chatId;
    this.sendIfSuccess = sendIfSuccess;
  }

  public String getChatId() {
    return chatId;
  }

  public boolean isSendIfSuccess() {
    return sendIfSuccess;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    TelegramDescriptor descriptor = (TelegramDescriptor) Jenkins.getInstance()
        .getDescriptor(TelegramNotifier.class);
    Message message = new TelegramMessage(descriptor.getBotToken(), chatId);
    BuildNotifier notifier = new BuildNotifier(message, build, sendIfSuccess);
    notifier.sendNotification();
    return true;
  }

  /**
   * The descriptor for the TelegramNotifier plugin
   */
  @Extension
  public static class TelegramDescriptor extends BuildStepDescriptor<Publisher> {

    private String botToken;

    public TelegramDescriptor() {
      load();
    }

    public String getBotToken() {
      return botToken;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
      JSONObject config = json.getJSONObject("telegram");
      this.botToken = config.getString("botToken");
      save();
      return true;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Telegram Notification";
    }

  }

}

