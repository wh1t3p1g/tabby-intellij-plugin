// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import static com.neueda.jetbrains.plugin.cypher.psi.CypherTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.neueda.jetbrains.plugin.cypher.psi.*;

public class CypherCallImpl extends ASTWrapperPsiElement implements CypherCall {

  public CypherCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CypherVisitor visitor) {
    visitor.visitCall(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CypherVisitor) accept((CypherVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CypherProcedureArguments getProcedureArguments() {
    return findNotNullChildByClass(CypherProcedureArguments.class);
  }

  @Override
  @NotNull
  public CypherProcedureName getProcedureName() {
    return findNotNullChildByClass(CypherProcedureName.class);
  }

  @Override
  @NotNull
  public CypherProcedureNamespace getProcedureNamespace() {
    return findNotNullChildByClass(CypherProcedureNamespace.class);
  }

  @Override
  @Nullable
  public CypherProcedureResults getProcedureResults() {
    return findChildByClass(CypherProcedureResults.class);
  }

  @Override
  @NotNull
  public PsiElement getKCall() {
    return findNotNullChildByType(K_CALL);
  }

}
