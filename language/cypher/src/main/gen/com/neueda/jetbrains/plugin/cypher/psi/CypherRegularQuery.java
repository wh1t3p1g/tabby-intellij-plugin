// This is a generated file. Not intended for manual editing.
package com.neueda.jetbrains.plugin.cypher.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CypherRegularQuery extends PsiElement {

  @NotNull
  CypherSingleQuery getSingleQuery();

  @NotNull
  List<CypherUnion> getUnionList();

}