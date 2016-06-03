// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.neueda.jetbrains.plugin.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.neueda.jetbrains.plugin.cypher.psi.*;

public class CypherWithImpl extends ASTWrapperPsiElement implements CypherWith {

  public CypherWithImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitWith(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherReturnBody getReturnBody() {
    return findNotNullChildByClass(CypherReturnBody.class);
  }

  @Override
  @Nullable
  public CypherWhere getWhere() {
    return findChildByClass(CypherWhere.class);
  }

  @Override
  @Nullable
  public PsiElement getKDistinct() {
    return findChildByType(K_DISTINCT);
  }

  @Override
  @NotNull
  public PsiElement getKWith() {
    return findNotNullChildByType(K_WITH);
  }

}
