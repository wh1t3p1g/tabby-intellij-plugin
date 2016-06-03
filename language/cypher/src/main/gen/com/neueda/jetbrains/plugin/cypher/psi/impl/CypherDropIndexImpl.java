// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.neueda.jetbrains.plugin.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.neueda.jetbrains.plugin.cypher.psi.*;

public class CypherDropIndexImpl extends ASTWrapperPsiElement implements CypherDropIndex {

  public CypherDropIndexImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitDropIndex(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherNodeLabel getNodeLabel() {
    return findNotNullChildByClass(CypherNodeLabel.class);
  }

  @Override
  @NotNull
  public CypherPropertyKeyName getPropertyKeyName() {
    return findNotNullChildByClass(CypherPropertyKeyName.class);
  }

  @Override
  @NotNull
  public PsiElement getKDrop() {
    return findNotNullChildByType(K_DROP);
  }

  @Override
  @NotNull
  public PsiElement getKIndex() {
    return findNotNullChildByType(K_INDEX);
  }

  @Override
  @NotNull
  public PsiElement getKOn() {
    return findNotNullChildByType(K_ON);
  }

}
