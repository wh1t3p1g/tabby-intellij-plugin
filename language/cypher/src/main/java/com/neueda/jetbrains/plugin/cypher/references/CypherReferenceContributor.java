package com.neueda.jetbrains.plugin.cypher.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import com.neueda.jetbrains.plugin.cypher.CypherLanguage;
import com.neueda.jetbrains.plugin.cypher.psi.CypherLabelName;
import com.neueda.jetbrains.plugin.cypher.psi.CypherPropertyKeyName;
import com.neueda.jetbrains.plugin.cypher.psi.CypherRelTypeName;
import com.neueda.jetbrains.plugin.cypher.psi.CypherVariable;
import com.neueda.jetbrains.plugin.cypher.references.impl.CypherLabelNameReference;
import com.neueda.jetbrains.plugin.cypher.references.impl.CypherProperyKeyNameReference;
import com.neueda.jetbrains.plugin.cypher.references.impl.CypherRelTypeNameReference;
import com.neueda.jetbrains.plugin.cypher.references.impl.CypherVariableReference;
import org.jetbrains.annotations.NotNull;

import static com.neueda.jetbrains.plugin.cypher.util.PsiUtil.rangeFrom;

/**
 * Contribute references for specified PSI elements.
 *
 * @author dmitry@vrublevsky.me
 */
public class CypherReferenceContributor extends PsiReferenceContributor {

    public static final PsiReference[] NO_REFERENCES = new PsiReference[0];

    public CypherReferenceContributor() {
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        register(registrar,
                PlatformPatterns.psiElement().withLanguage(CypherLanguage.INSTANCE),
                CypherReferenceContributionPriority.VARIABLE,
                (element, context) ->
                        element instanceof CypherVariable ? single(new CypherVariableReference(element, rangeFrom(element))) : null);
        register(registrar,
                PlatformPatterns.psiElement().withLanguage(CypherLanguage.INSTANCE),
                CypherReferenceContributionPriority.LABEL_NAME,
                (element, context) ->
                        element instanceof CypherLabelName ? single(new CypherLabelNameReference(element, rangeFrom(element))) : null);
        register(registrar,
                PlatformPatterns.psiElement().withLanguage(CypherLanguage.INSTANCE),
                CypherReferenceContributionPriority.REL_TYPE_NAME,
                (element, context) ->
                        element instanceof CypherRelTypeName ? single(new CypherRelTypeNameReference(element, rangeFrom(element))) : null);
        register(registrar,
                PlatformPatterns.psiElement().withLanguage(CypherLanguage.INSTANCE),
                CypherReferenceContributionPriority.PROPERTY_KEY_NAME,
                (element, context) ->
                        element instanceof CypherPropertyKeyName ? single(new CypherProperyKeyNameReference(element, rangeFrom(element))) : null);
    }

    private void register(PsiReferenceRegistrar registrar,
                          @NotNull ElementPattern pattern,
                          CypherReferenceContributionPriority contributionPriority,
                          ReferenceFactory referenceFactory) {
        registrar.registerReferenceProvider(
                pattern,
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        PsiReference[] psiReferences = referenceFactory.run(element, context);
                        return psiReferences != null ? psiReferences : NO_REFERENCES;
                    }
                },
                contributionPriority.getPriority());
    }

    private PsiReference[] single(PsiReference reference) {
        return new PsiReference[]{reference};
    }

    private interface ReferenceFactory {
        PsiReference[] run(@NotNull PsiElement element, @NotNull ProcessingContext context);
    }
}
