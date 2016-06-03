// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.neueda.jetbrains.plugin.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.neueda.jetbrains.plugin.cypher.psi.*;

public class CypherUnionImpl extends ASTWrapperPsiElement implements CypherUnion {

  public CypherUnionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitUnion(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherSingleQuery getSingleQuery() {
    return findNotNullChildByClass(CypherSingleQuery.class);
  }

  @Override
  @Nullable
  public PsiElement getKAll() {
    return findChildByType(K_ALL);
  }

  @Override
  @NotNull
  public PsiElement getKUnion() {
    return findNotNullChildByType(K_UNION);
  }

}
