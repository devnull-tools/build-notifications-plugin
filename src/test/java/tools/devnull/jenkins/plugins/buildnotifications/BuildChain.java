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
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.scm.ChangeLogSet;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility class for creating a build history for testing purposes
 */
public class BuildChain {

  private final AbstractBuild build;

  private BuildChain(AbstractBuild actual, AbstractBuild next, Result result) {
    this.build = next;
    when(build.getResult()).thenReturn(result);
    when(next.getPreviousBuild()).thenReturn(actual);
    when(actual.getNextBuild()).thenReturn(next);
    initializeMock();
  }

  private BuildChain(Result result) {
    this.build = mock(AbstractBuild.class);
    when(build.getResult()).thenReturn(result);
    initializeMock();
  }

  private void initializeMock() {
    ChangeLogSet changeLogSet = mock(ChangeLogSet.class);
    when(changeLogSet.iterator()).thenReturn(Collections.emptyIterator());
    when(build.getChangeSet()).thenReturn(changeLogSet);
    when(build.getProject()).thenReturn(mock(AbstractProject.class));
  }

  public BuildChain thenFailed() {
    return new BuildChain(this.build, mock(AbstractBuild.class), Result.FAILURE);
  }

  public BuildChain thenSucceeded() {
    return new BuildChain(this.build, mock(AbstractBuild.class), Result.SUCCESS);
  }

  public AbstractBuild get() {
    return this.build;
  }

  public static BuildChain failed() {
    return new BuildChain(Result.FAILURE);
  }

  public static BuildChain succeeded() {
    return new BuildChain(Result.SUCCESS);
  }

  public static AbstractBuild successful() {
    return succeeded().get();
  }

  public static AbstractBuild broken() {
    return failed().get();
  }

  public static AbstractBuild stillBroken() {
    return failed().thenFailed().get();
  }

  public static AbstractBuild fixed() {
    return failed().thenSucceeded().get();
  }

}
