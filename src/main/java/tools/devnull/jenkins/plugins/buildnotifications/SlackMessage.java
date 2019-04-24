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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class that represents a Slack message
 *
 * @author cs8898
 */
public class SlackMessage implements Message {

  private static final Logger LOGGER = Logger.getLogger(SlackMessage.class.getName());

  private final String botToken;
  private final String channelIds;

  private String extraMessage;
  private String content;
  private String title;
  private String url;
  private String urlTitle;

  /**
   * Creates a new Slack message based on the given parameters
   *
   * @param botToken the bot token
   * @param channelIds  the target ids separated by commas (a group conversation id or a contact id)
   */
  public SlackMessage(String botToken, String channelIds, String extraMessage) {
    this.botToken = botToken;
    this.channelIds = channelIds;
    this.extraMessage = extraMessage;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public void setUrl(String url, String title) {
    this.url = url;
    this.urlTitle = title;
  }

  @Override
  public void highPriority() {
    // Not possible with Slack
  }

  @Override
  public void normalPriority() {
    // Not possible with Slack
  }

  @Override
  public void lowPriority() {
    // Not possible with Slack
  }

  @Override
  public boolean send() {
    String[] ids = channelIds.split("\\s*,\\s*");
    HttpClient client = new HttpClient();
    boolean result = true;
    for (String channelId : ids) {
      PostMethod post = new PostMethod(
          "https://slack.com/api/chat.postMessage"
      );

      post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

      post.setRequestBody(new NameValuePair[]{
          new NameValuePair("token",botToken),
          new NameValuePair("as_user","true"),
          new NameValuePair("channel", channelId),
          new NameValuePair("text", getMessage())
      });
      try {
        client.executeMethod(post);
      } catch (IOException e) {
        LOGGER.severe("Error while sending notification: " + e.getMessage());
        result = false;
      }
    }
    return result;
  }

  private String getMessage() {
    return String.format(
        "%s%n%n%s%n%n%s <%s>%n%n%s",
        title,
        content,
        urlTitle,
        url,
        extraMessage
    );
  }

}
