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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class that represents a Pushover message
 *
 * @author Ataxexe
 */
public class PushoverMessage implements Message {

  private static final Logger LOGGER = Logger.getLogger(PushoverMessage.class.getName());

  private final String userToken;
  private final String appToken;
  private final String device;

  private String content;
  private String title;
  private Integer priority = 0;
  private String url;
  private String urlTitle;

  /**
   * Creates a new Pushover message based on the given parameters
   *
   * @param userToken the user token (the target to send the message)
   * @param appToken  the application token
   * @param device    the device to send the message (optional)
   */
  public PushoverMessage(String userToken, String appToken, String device) {
    this.userToken = userToken;
    this.appToken = appToken;
    this.device = device;
  }

  /**
   * Creates a new Pushover message based on the given parameters
   *
   * @param userToken the user token (the target to send the message)
   * @param appToken  the application token
   */
  public PushoverMessage(String userToken, String appToken) {
    this.userToken = userToken;
    this.appToken = appToken;
    this.device = null;
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
    this.priority = 1;
  }

  @Override
  public void normalPriority() {
    this.priority = 0;
  }

  public void lowPriority() {
    this.priority = -1;
  }

  @Override
  public void send() {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod("https://api.pushover.net/1/messages.json");
    post.setRequestBody(new NameValuePair[]{
        new NameValuePair("token", appToken),
        new NameValuePair("user", userToken),
        new NameValuePair("message", content),
        new NameValuePair("title", title),
        new NameValuePair("device", device),
        new NameValuePair("priority", priority.toString()),
        new NameValuePair("url", url),
        new NameValuePair("url_title", urlTitle)
    });
    try {
      client.executeMethod(post);
    } catch (IOException e) {
      LOGGER.severe("Error while sending notification: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
