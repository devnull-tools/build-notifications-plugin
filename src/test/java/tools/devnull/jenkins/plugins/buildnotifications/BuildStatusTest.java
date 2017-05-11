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

import org.junit.Test;
import tools.devnull.kodo.Spec;

import java.util.function.Supplier;

import static tools.devnull.jenkins.plugins.buildnotifications.BuildChain.*;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.BROKEN;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.FIXED;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.STILL_BROKEN;
import static tools.devnull.jenkins.plugins.buildnotifications.BuildStatus.SUCCESSFUL;
import static tools.devnull.kodo.Expectation.to;

public class BuildStatusTest {

  @Test
  public void test() {
    Spec.begin()
        .expect(statusOf(failed().thenFailed()), to().be(STILL_BROKEN))
        .expect(statusOf(succeeded().thenFailed()), to().be(BROKEN))
        .expect(statusOf(failed()), to().be(BROKEN))

        .expect(statusOf(succeeded()), to().be(SUCCESSFUL))
        .expect(statusOf(succeeded().thenSucceeded()), to().be(SUCCESSFUL))
        .expect(statusOf(failed().thenSucceeded()), to().be(FIXED));
  }

  private Supplier<BuildStatus> statusOf(BuildChain buildChain) {
    return () -> BuildStatus.of(buildChain.get());
  }

}
