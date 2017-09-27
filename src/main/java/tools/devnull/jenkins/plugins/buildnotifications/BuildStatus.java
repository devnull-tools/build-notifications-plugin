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

/**
 * Enumeration of the possible build status for notification purposes.
 *
 * @author Ataxexe
 */
public enum BuildStatus {

  /**
   * Represents a build that has failed
   */
  BROKEN("Broken"),
  /**
   * Represents a build that has failed after a failed build
   */
  STILL_BROKEN("Still Broken"),
  /**
   * Represents a build that has succeeded after a failed build
   */
  FIXED("Fixed"),
  /**
   * Represents a build that has succeeded (if there is a previous build, it has been succeeded too)
   */
  SUCCESSFUL("Successful");

  private final String tag;

  BuildStatus(String tag) {
    this.tag = tag;
  }

  public String tag() {
    String tag = NotifierSettings.alternativeMSG(this);
    if(tag!=null && tag.length()>0) {
        return tag;
    } else {
        return this.tag;
    }
  }

  /**
   * Returns the {@code BuildStatus} that represents the given build. If the given build has a previous build, it
   * must be finished.
   *
   * @param build the build to analyze
   * @return the status that represents the given build
   */
  public static BuildStatus of(AbstractBuild build) {
    AbstractBuild previousBuild = build.getPreviousBuild();
    if (build.getResult() == Result.SUCCESS) {
      if (previousBuild != null) {
        return previousBuild.getResult() == Result.SUCCESS ? SUCCESSFUL : FIXED;
      } else {
        return SUCCESSFUL;
      }
    } else {
      if (previousBuild != null) {
        return previousBuild.getResult() != Result.SUCCESS ? STILL_BROKEN : BROKEN;
      } else {
        return BROKEN;
      }
    }
  }
}
