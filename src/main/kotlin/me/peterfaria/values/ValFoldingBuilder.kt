package me.peterfaria.values

import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.PluginUtil
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDeclarationStatement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.PsiModifier
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class ValFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        var regions = 0;
        val descriptors = ArrayList<FoldingDescriptor>()
        val declarationStatements = PsiTreeUtil.findChildrenOfType(root, PsiDeclarationStatement::class.java)
        for (declarationStatement in declarationStatements) {
            for (child in declarationStatement.children) {
                if (child is PsiLocalVariable) {
                    val isFinal = child.modifierList?.hasModifierProperty(PsiModifier.FINAL)
                    val isVar = child.typeElement.isInferredType
                    if (isFinal != null && isFinal && isVar) {

                        PluginManager.getLogger().debug("Wrapping line ")
                        descriptors.add(
                            FoldingDescriptor(
                                declarationStatement.node,
                                TextRange(child.startOffset, child.typeElement.endOffset),
                                FoldingGroup.newGroup("val" + regions++)
                            )
                        )
                    }
                }
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? = "val"

    override fun isCollapsedByDefault(node: ASTNode): Boolean = true
}