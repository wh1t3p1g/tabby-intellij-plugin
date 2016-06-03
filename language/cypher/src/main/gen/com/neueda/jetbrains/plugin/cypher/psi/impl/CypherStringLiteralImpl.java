// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.neueda.jetbrains.plugin.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.neueda.jetbrains.plugin.cypher.psi.*;

public class CypherStringLiteralImpl extends ASTWrapperPsiElement implements CypherStringLiteral {

  public CypherStringLiteralImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitStringLiteral(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getLString() {
    return findNotNullChildByType(L_STRING);
  }

}
