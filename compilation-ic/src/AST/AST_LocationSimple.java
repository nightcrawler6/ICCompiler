package AST;

import IR.IR_Binop;
import IR.IR_EXP;
import IR.IR_ExpBinop;
import IR.IR_LiteralConstant;
import IR.IR_Temporary;
import IR.IR_TemporaryType;
import SEMANTIC.SEMANTIC_ClassIsNotInSymbolTableException;
import SEMANTIC.SEMANTIC_ClassOrFunctionNamesNotInitializedExecption;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;

public class AST_LocationSimple extends AST_Location{
	public String name;
	
	public AST_LocationSimple(String name){
		System.out.println("AST_LOCATION SIMPLE: "+name);
		this.name = name;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		SEMANTIC_SymbolInfo symbolFound = SEMANTIC_SymbolTable.searchSymbolInfoLocallyOrInCurrentClassAndUp(className,name);
		
		if(symbolFound==null){
			return null;
		}
				
		if(symbolFound instanceof SEMANTIC_VariableSymbolInfo){
		return ((SEMANTIC_VariableSymbolInfo) symbolFound).variableType;
		}
		else{
			return null;
		}
	}
	
	private IR_EXP getLocationStartingOffset(SEMANTIC_VariableSymbolInfo symbolFound){
		if(symbolFound.isField){
			return getThisObjectHeapAddress();
		}
		
		return new IR_Temporary(IR_TemporaryType.fp);
	
	}
	
	@Override
	public IR_ExpBinop createIR() throws SEMANTIC_ClassIsNotInSymbolTableException, SEMANTIC_ClassOrFunctionNamesNotInitializedExecption{
		assertClassAndFunctionNamesInitialized();
		SEMANTIC_VariableSymbolInfo symbolFound = (SEMANTIC_VariableSymbolInfo)SEMANTIC_SymbolTable.searchSymbolInfoLocallyOrInCurrentClassAndUp(this.currentClassName,name);
	
		return new IR_ExpBinop(getLocationStartingOffset(symbolFound),
								new IR_LiteralConstant(symbolFound.offset),
								IR_Binop.PLUS,
								false);
		
	}
}