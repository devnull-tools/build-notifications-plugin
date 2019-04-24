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

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A class that represents a boteco message.
 */
public class BotecoMessage implements Message {

  private static final Logger LOGGER = Logger.getLogger(BotecoMessage.class.getName());

  private final String endpoint;

  private String extraMessage;
  private String content;
  private String title;
  private String url;
  private String priority = "normal";

  public BotecoMessage(String eventId, String endpoint, String extraMessage) {
    if (endpoint.endsWith("/")) {
      this.endpoint = endpoint + eventId;
    } else {
      this.endpoint = endpoint + "/" + eventId;
    }

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
  }

  @Override
  public void highPriority() {
    this.priority = "high";
  }

  @Override
  public void normalPriority() {
    this.priority = "normal";
  }

  @Override
  public void lowPriority() {
    this.priority = "low";
  }

  @Override
  public boolean send() {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(endpoint);
    post.setRequestHeader("Content-Type", "application/json; charset=utf-8");
    Map<String, String> values = new HashMap<String, String>();
    values.put("title", title);
    values.put("text", content + "\n\n" + extraMessage);
    values.put("url", url);
    values.put("priority", priority);
    try {
      post.setRequestEntity(new StringRequestEntity(JSONObject.fromObject(values).toString(),
          "application/json", "UTF-8"));
      client.executeMethod(post);
      return true;
    } catch (IOException e) {
      LOGGER.severe("Error while sending notification: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

}
