/*
 * Copyright 2000-2015 JetBrains s.r.o.
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

package com.intellij.ide.impl;

import com.intellij.ide.CompositeSelectInTarget;
import com.intellij.ide.SelectInContext;
import com.intellij.ide.SelectInTarget;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.ui.IdeUICustomization;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author yole
 */
public class ProjectViewSelectInGroupTarget implements CompositeSelectInTarget, DumbAware {
  @Override
  @NotNull
  public Collection<SelectInTarget> getSubTargets(@NotNull SelectInContext context) {
    return ProjectView.getInstance(context.getProject()).getSelectInTargets();
  }

  @Override
  public boolean canSelect(SelectInContext context) {
    ProjectView projectView = ProjectView.getInstance(context.getProject());
    Collection<SelectInTarget> targets = projectView.getSelectInTargets();
    for (SelectInTarget projectViewTarget : targets) {
      if (projectViewTarget.canSelect(context)) return true;
    }
    return false;
  }

  @Override
  public void selectIn(final SelectInContext context, final boolean requestFocus) {
    ProjectView projectView = ProjectView.getInstance(context.getProject());
    Collection<SelectInTarget> targets = projectView.getSelectInTargets();
    Collection<SelectInTarget> targetsToCheck = new LinkedHashSet<>();
    String currentId = projectView.getCurrentViewId();
    for (SelectInTarget projectViewTarget : targets) {
      if (Comparing.equal(currentId, projectViewTarget.getMinorViewId())) {
        targetsToCheck.add(projectViewTarget);
        break;
      }
    }
    targetsToCheck.addAll(targets);
    targetsToCheck.stream().filter(t -> t.canSelect(context)).findFirst().ifPresent(target -> {
      target.selectIn(context, requestFocus);
    });
  }

  @Override
  public String getToolWindowId() {
    return ToolWindowId.PROJECT_VIEW;
  }

  @Override
  public String getMinorViewId() {
    return null;
  }

  @Override
  public String toString() {
    return StringUtil.capitalize(IdeUICustomization.getInstance().getProjectConceptName()) + " View";
  }
}
