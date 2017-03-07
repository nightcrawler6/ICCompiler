package AST;

import IR.IR_EXP;
import IR.IR_ExpNewClass;
import SEMANTIC.SEMANTIC_ClassOrFunctionNamesNotInitializedExecption;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class AST_ExpNewClass extends AST_Exp{
	public String newExpClassName;
	
	public AST_ExpNewClass(String newExpClassName){
		this.newExpClassName = newExpClassName;
	}
	
	public SEMANTIC_ICTypeInfo validate(String receivedClassName) throws SEMANTIC_SemanticAnalysisException{
		if(SEMANTIC.SEMANTIC_SymbolTable.doesClassExist(newExpClassName)){
			return new SEMANTIC_ICTypeInfo(newExpClassName, 0);
		}
		else{
			return null;
		}
	}

	@Override
	public IR_EXP createIR() throws SEMANTIC_ClassOrFunctionNamesNotInitializedExecption{
		assertClassAndFunctionNamesInitialized();
		return new IR_ExpNewClass(this.newExpClassName);
	}

}
