package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public abstract class AST_Type extends AST_Node{

	public abstract SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException;

}