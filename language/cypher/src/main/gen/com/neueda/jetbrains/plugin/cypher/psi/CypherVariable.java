// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.neueda.jetbrains.plugin.cypher.references.CypherNamedElement;
import com.intellij.psi.PsiReference;

public interface CypherVariable extends CypherNamedElement {

  @NotNull
  CypherSymbolicNameString getSymbolicNameString();

  String getName();

  CypherVariable setName(String newName);

  PsiElement getNameIdentifier();

  @NotNull
  PsiReference[] getReferences();

}
