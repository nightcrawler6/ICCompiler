package AST;

import IR.IR_Statement;
import SEMANTIC.SEMANTIC_ClassIsNotInSymbolTableException;
import SEMANTIC.SEMANTIC_FunctionNotInSymbolTableException;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;

public abstract class AST_Statement extends AST_Node{
	public SEMANTIC_ICTypeInfo expectedReturnType = null;
	public boolean doesAlwaysReturnValue = false;
	
	public abstract IR_Statement createIR() throws SEMANTIC_SemanticAnalysisException;
	
	protected SEMANTIC_FunctionSymbolInfo getFunctionSymbolInfo() throws SEMANTIC_FunctionNotInSymbolTableException, SEMANTIC_ClassIsNotInSymbolTableException{
		SEMANTIC_SymbolInfo functionInfo = SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(currentClassName, currentFunctionName);
		if ((functionInfo == null) || (!(functionInfo instanceof SEMANTIC_FunctionSymbolInfo))){
			throw new SEMANTIC_FunctionNotInSymbolTableException(currentClassName, currentFunctionName);
		}
		
		return (SEMANTIC_FunctionSymbolInfo)functionInfo;
	}
}