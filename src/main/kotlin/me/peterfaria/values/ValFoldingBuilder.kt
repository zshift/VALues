package me.peterfaria.values

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.openapi.diagnostic.Logger

class ValFoldingBuilder : FoldingBuilderEx(), DumbAware {
    companion object {
        private val log: Logger = Logger.getInstance(ValFoldingBuilder::class.java)
    }

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        var regions = 0;
        val groupFolds = ValState.getInstance().groupFolds
        val group = FoldingGroup.newGroup("val")

        val descriptors = ArrayList<FoldingDescriptor>()
        val localVariables = PsiTreeUtil.findChildrenOfType(root, PsiLocalVariable::class.java)
        for (localVar in localVariables) {
            val message = StringBuilder()
            message.append("Scanning '${localVar.text}' on line ${document.getLineNumber(localVar.startOffset)}... ")
            val final = findFinal(localVar.modifierList)
            if (final != null && localVar.typeElement.isInferredType) {
                message.append("detected final (offset ${final.startOffset}) and var (offset ${localVar.typeElement.startOffset}) keywords.")
                val startOffset = final.startOffset
                val endOffset = localVar.typeElement.endOffset
                val foldingGroup = if (groupFolds) group else FoldingGroup.newGroup("val" + regions++)

                descriptors.add(
                    FoldingDescriptor(
                        localVar.node,
                        TextRange(startOffset, endOffset),
                        foldingGroup
                    )
                )
            } else {
                message.append("did not find final nor val.")
            }
            log.debug(message.toString())
        }
        return descriptors.toTypedArray()
    }

    private fun findFinal(modifierList: PsiModifierList?): PsiKeyword? {
        val keyword = PsiTreeUtil.findChildOfType(modifierList, PsiKeyword::class.java)
        return if (keyword?.textMatches(PsiKeyword.FINAL) == true) keyword else null
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        val localVar = node as PsiLocalVariable
        val placeholder = StringBuilder()
        val message = StringBuilder().append("Determining placeholder text for ${localVar.text} at offset ${localVar.startOffset}... ")

        var foundFinal = false

        for (modifier in localVar.modifierList?.children ?: arrayOf()) {
            if (foundFinal) {
                placeholder.append(modifier.text)
            }

            if (!foundFinal && modifier is PsiKeyword && modifier.text == PsiKeyword.FINAL) {
                foundFinal = true
            }
        }
        val ph = placeholder.toString()
        log.debug(message.append("placeholder text: $ph").toString())
        return if (ph.isBlank()) "val" else "${ph.trim()} val"
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        val instance = ValState.getInstance()
        return instance.autoFold
    }
}