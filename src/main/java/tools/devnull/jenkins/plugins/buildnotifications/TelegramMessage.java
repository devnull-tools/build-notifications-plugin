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
 * A class that represents a Telegram message
 *
 * @author Ataxexe
 */
public class TelegramMessage implements Message {

  private static final Logger LOGGER = Logger.getLogger(TelegramMessage.class.getName());

  private final String botToken;
  private final String chatIds;

  private String content;
  private String title;
  private String url;
  private String urlTitle;

  /**
   * Creates a new Telegram message based on the given parameters
   *
   * @param botToken the bot token
   * @param chatIds  the target ids separated by commas (a group conversation id or a contact id)
   */
  public TelegramMessage(String botToken, String chatIds) {
    this.botToken = botToken;
    this.chatIds = chatIds;
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
    // Not possible with Telegram
  }

  @Override
  public void normalPriority() {
    // Not possible with Telegram
  }

  @Override
  public void lowPriority() {
    // Not possible with Telegram
  }

  public void send() {
    String[] ids = chatIds.split("\\s*,\\s*");
    HttpClient client = new HttpClient();
    for (String chatId : ids) {
      PostMethod post = new PostMethod(String.format(
          "https://api.telegram.org/bot%s/sendMessage",
          botToken
      ));

      post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

      post.setRequestBody(new NameValuePair[]{
          new NameValuePair("chat_id", chatId),
          new NameValuePair("text", getMessage())
      });
      try {
        client.executeMethod(post);
      } catch (IOException e) {
        LOGGER.severe("Error while sending notification: " + e.getMessage());
        e.printStackTrace();
      }
    }
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
