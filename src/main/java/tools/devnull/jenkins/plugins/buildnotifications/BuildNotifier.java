/*
 * The MIT License
 *
 * Copyright (c) 2015 Marcelo "Ataxexe" Guimarães <ataxexe@devnull.tools>
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

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.scm.ChangeLogSet;
import jenkins.model.JenkinsLocationConfiguration;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * A class that sends notifications based on a build.
 *
 * @author Ataxexe
 */
public class BuildNotifier {

  private static final Logger LOGGER = Logger.getLogger(BuildNotifier.class.getName());

  private final Message message;
  private final AbstractBuild build;
  private final Result result;
  private final String baseUrl;
  private final boolean sendIfSuccess;

  /**
   * Constructs a new BuildNotifier based on the given objects
   *
   * @param message the message to populate and send
   * @param build   the target build
   */
  public BuildNotifier(Message message, AbstractBuild build, boolean sendIfSuccess) {
    this.message = message;
    this.build = build;
    this.result = build.getResult();
    this.baseUrl = JenkinsLocationConfiguration.get().getUrl();
    this.sendIfSuccess = sendIfSuccess;
  }

  /**
   * Sends the notification through the given message object.
   */
  public void sendNotification() {
    if (result.ordinal == 0) {
      if (sendIfSuccess) {
        sendMessage();
      }
    } else {
      sendMessage();
    }
  }

  private void sendMessage() {
    LOGGER.info("Sending push notification...");

    setPriority();
    setContent();
    setTitle();
    setUrl();

    message.send();
  }

  private void setUrl() {
    message.setUrl(String.format("%s%s", baseUrl, build.getUrl()), "Go to build");
  }

  private void setContent() {
    if(build.getChangeSet().getItems().length == 0){
      message.setContent(result.toString());
    } else {
      StringBuilder changes = new StringBuilder();

      for (Iterator<? extends ChangeLogSet.Entry> i = build.getChangeSet().iterator(); i.hasNext();) {
        ChangeLogSet.Entry change = i.next();
        changes.append("\n");
        changes.append(change.getMsg());
        changes.append(" - ");
        changes.append(change.getAuthor());
      }

      message.setContent(String.format("%s%n%s", result.toString(), changes.toString()));
    }
  }

  private void setTitle() {
    message.setTitle(String.format(
        "Build #%d of %s",
        build.getNumber(),
        build.getProject().getName()
    ));
  }

  private void setPriority() {
    switch (result.ordinal) {
      case 0: //SUCCESS
        message.lowPriority();
        break;
      case 2: //FAILURE
        message.highPriority();
        break;
    }
  }

}
