/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2022 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.jetbrains.plugins.ideavim.action.motion.gn;

import com.intellij.idea.TestFor;
import com.maddyhome.idea.vim.VimPlugin;
import com.maddyhome.idea.vim.action.motion.search.SearchWholeWordForwardAction;
import com.maddyhome.idea.vim.api.VimInjectorKt;
import com.maddyhome.idea.vim.command.VimStateMachine;
import com.maddyhome.idea.vim.common.Direction;
import org.jetbrains.plugins.ideavim.SkipNeovimReason;
import org.jetbrains.plugins.ideavim.TestWithoutNeovim;
import org.jetbrains.plugins.ideavim.VimTestCase;

public class VisualSelectNextSearchTest extends VimTestCase {
  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearch() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "b" + "gn"), "h<caret>ello world\nhello world hello world");

    assertOffset(16);
    assertSelection("hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearchMulticaret() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "b" + "gn"), "h<caret>ello world\nh<caret>ello world hello world");

    assertEquals(1, myFixture.getEditor().getCaretModel().getCaretCount());
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearchFordAndBack() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "2b" + "gn" + "gN"), "h<caret>ello world\nhello world hello world");

    assertOffset(0);
    assertSelection("h");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.UNCLEAR)
  public void testWithoutSpaces() {
    configureByText("test<caret>test");
    VimPlugin.getSearch().setLastSearchState(myFixture.getEditor(), "test", "", Direction.FORWARDS);
    typeText(VimInjectorKt.getInjector().getParser().parseKeys("gn"));

    assertOffset(7);
    assertSelection("test");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearchCurrentlyInOne() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn"), "h<caret>ello world\nhello world hello world");

    assertOffset(16);
    assertSelection("hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearchTwice() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "2gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello");
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearchTwiceInVisual() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn" + "2gn"), "h<caret>ello world\nhello world hello, hello hello");

    assertOffset(35);
    assertSelection("hello world hello, hello");
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testTwoSearchesStayInVisualMode() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn" + "gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello world hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testCanExitVisualMode() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn" + "gn" + "<Esc>"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection(null);
    assertMode(VimStateMachine.Mode.COMMAND);
  }

  public void testNullSelectionDoesNothing() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("/bye<CR>" + "gn"), "h<caret>ello world\nhello world hello world");

    assertOffset(1);
    assertSelection(null);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testIfInLastPositionOfSearchAndInNormalModeThenSelectCurrent() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*0e" + "gn"), "h<caret>ello hello");

    assertOffset(4);
    assertSelection("hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testIfInMiddlePositionOfSearchAndInVisualModeThenSelectCurrent() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*0llv" + "gn"), "h<caret>ello hello");

    assertOffset(4);
    assertSelection("llo");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testIfInLastPositionOfSearchAndInVisualModeThenSelectNext() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*0ev" + "gn"), "h<caret>ello hello");

    assertOffset(10);
    assertSelection("o hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testMixWithN() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn" + "n" + "gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello world hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testMixWithPreviousSearch() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn" + "gn" + "gN" + "gn"), "h<caret>ello world\nhello world hello, hello");

    assertOffset(28);
    assertSelection("hello world hello");
    assertMode(VimStateMachine.Mode.VISUAL);
  }

  @TestFor(classes = {SearchWholeWordForwardAction.class})
  public void testSearchWithTabs() {
    typeTextInFile(VimInjectorKt.getInjector().getParser().parseKeys("*" + "gn"), "\tf<caret>oo");
    assertSelection("foo");
  }
}
