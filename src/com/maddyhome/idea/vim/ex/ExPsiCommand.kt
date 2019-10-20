package com.maddyhome.idea.vim.ex

import com.intellij.psi.PsiElement

class ExPsiCommand(
  command: String,
  val element: PsiElement
) : ExCommand(Ranges(), command, "")
