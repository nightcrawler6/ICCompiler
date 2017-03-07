package AST;

import IR.IR_EXP;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public abstract class AST_Location extends AST_Node{
	public abstract IR_EXP createIR() throws SEMANTIC_SemanticErrorException;
}