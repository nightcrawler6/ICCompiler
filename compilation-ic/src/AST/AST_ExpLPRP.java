package AST;

import IR.IR_EXP;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class AST_ExpLPRP extends AST_Exp{ 
	public AST_Exp e;
	
	public AST_ExpLPRP(AST_Exp e){
		this.e = e;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		return e.validate(className);
	}

	@Override
	public IR_EXP createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		this.e.currentFunctionName=this.currentFunctionName;
		this.e.currentClassName=this.currentClassName;
		return this.e.createIR();
	}
}
