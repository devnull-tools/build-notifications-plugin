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

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.scm.ChangeLogSet;

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
  private final BuildStatus status;
  private final Result result;
  private final String baseUrl;

  /**
   * Constructs a new BuildNotifier based on the given objects
   *
   * @param message the message to populate and send
   * @param build   the target build
   */
  public BuildNotifier(Message message, AbstractBuild build, String baseUrl) {
    this.message = message;
    this.build = build;
    this.status = BuildStatus.of(build);
    this.result = build.getResult();
    this.baseUrl = baseUrl;
  }

  /**
   * Sends the notification through the given message object.
   */
  public void sendNotification() {
    LOGGER.info("Sending notification...");

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
    if (build.getChangeSet().getItems().length == 0) {
      message.setContent(this.getResultString());
    } else {
      StringBuilder changes = new StringBuilder();

      for (Iterator<? extends ChangeLogSet.Entry> i = build.getChangeSet().iterator(); i.hasNext(); ) {
        ChangeLogSet.Entry change = i.next();
        changes.append("\n");
        changes.append(change.getMsg());
        changes.append(" - ");
        changes.append(change.getAuthor());
      }

      message.setContent(String.format("%s%n%s", this.getResultString(), changes.toString()));
    }
  }

  private void setTitle() {
    message.setTitle(String.format(
        "%s - Build #%d of %s",
        status.tag(),
        build.getNumber(),
        build.getProject().getName()
    ));
  }

  private void setPriority() {
    switch (status) {
      case FIXED:
        message.normalPriority();
        break;
      case BROKEN:
      case STILL_BROKEN:
        message.highPriority();
        break;
      case SUCCESSFUL:
        message.lowPriority();
        break;
    }
  }

  private String getResultString(){
    String result = NotifierSettings.alternativeResult(this.result);
    if(result != null && result.length() > 0){
      return result;
    }else{
      return this.result.toString();
    }
  }

}
