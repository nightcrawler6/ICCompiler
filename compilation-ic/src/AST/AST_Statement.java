package AST;

import IR.IR_Statement;
import SEMANTIC.SEMANTIC_NoSuchClassInTableException;
import SEMANTIC.SEMANTIC_NoSuchMethodInTableException;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;

public abstract class AST_Statement extends AST_Node{
	public SEMANTIC_ICTypeInfo expectedReturnType = null;
	public boolean doesAlwaysReturnValue = false;
	
	public abstract IR_Statement createIR() throws SEMANTIC_SemanticErrorException;
	
	protected SEMANTIC_FunctionSymbolInfo getFunctionSymbolInfo() throws SEMANTIC_NoSuchMethodInTableException, SEMANTIC_NoSuchClassInTableException{
		SEMANTIC_SymbolInfo functionInfo = SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(currentClassName, currentFunctionName);
		if ((functionInfo == null) || (!(functionInfo instanceof SEMANTIC_FunctionSymbolInfo))){
			throw new SEMANTIC_NoSuchMethodInTableException(currentClassName, currentFunctionName);
		}
		
		return (SEMANTIC_FunctionSymbolInfo)functionInfo;
	}
}