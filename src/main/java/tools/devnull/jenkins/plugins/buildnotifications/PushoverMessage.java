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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

  private String extraMessage;
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
   */
  public PushoverMessage(String userToken, String appToken, String extraMessage) {
    this.userToken = userToken;
    this.appToken = appToken;
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
  public boolean send() {
    try {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod("https://api.pushover.net/1/messages.json");
      String data = "token=" + appToken + "&user=" + userToken + "&message=" + encode(content + "\n\n" + extraMessage) +
          "&title=" + encode(title) + "&priority=" + priority + "&url=" + url + "&url_title=" + urlTitle;
      post.setRequestEntity(new StringRequestEntity(data, "application/x-www-form-urlencoded", "UTF-8"));
      client.executeMethod(post);
      return true;
    } catch (IOException e) {
      LOGGER.severe("Error while sending notification: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  private String encode(String value) throws UnsupportedEncodingException {
    return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
  }

}
