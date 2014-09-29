/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.packaging;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author vlan
 */
public class PyExecutionException extends ExecutionException {
  private static final Pattern WITH_CR_DELIMITER_PATTERN = Pattern.compile("(?<=\r|\n|\r\n)");

  @NotNull private String myCommand;
  @NotNull private List<String> myArgs;
  private final int myReturnCode;
  @NotNull private String myMessage;
  @NotNull private final List<? extends PyExecutionFix> myFixes;

  public PyExecutionException(@NotNull String message, @NotNull String command, @NotNull List<String> args, int returnCode) {
    this(message, command, args, returnCode, Collections.<PyExecutionFix>emptyList());
  }

  public PyExecutionException(@NotNull String message, @NotNull String command, @NotNull List<String> args,
                              int returnCode, @NotNull List<? extends PyExecutionFix> fixes) {
    super(message);
    myCommand = command;
    myArgs = args;
    myReturnCode = returnCode;
    myMessage = stripLinesWithoutLineFeeds(message);
    myFixes = fixes;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append("The following command was executed:\n\n");
    final String command = getCommand() + " " + StringUtil.join(getArgs(), " ");
    b.append(command);
    b.append("\n\n");
    b.append("The error output of the command:\n\n");
    b.append(getMessage());
    return b.toString();
  }

  @NotNull
  public String getCommand() {
    return myCommand;
  }

  @NotNull
  public List<String> getArgs() {
    return myArgs;
  }

  @NotNull
  public String getMessage() {
    return myMessage;
  }

  @NotNull
  private static String stripLinesWithoutLineFeeds(@NotNull String s) {
    final String[] lines = WITH_CR_DELIMITER_PATTERN.split(s);
    final List<String> result = new ArrayList<String>();
    for (String line : lines) {
      if (!line.endsWith("\r")) {
        result.add(line);
      }
    }
    return StringUtil.join(result, "");
  }

  @NotNull
  public List<? extends PyExecutionFix> getFixes() {
    return myFixes;
  }

  public int getReturnCode() {
    return myReturnCode;
  }
}
