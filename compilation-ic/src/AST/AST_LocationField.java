package AST;

import IR.IR_ExpLocationField;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;
import UTILS.DebugPrint;

public class AST_LocationField extends AST_Location{
	public String varClass;
	public AST_Exp var;
	public String fieldName;
	
	public AST_LocationField(AST_Exp var,String fieldName){
		this.var = var;
		this.fieldName = fieldName;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		SEMANTIC_ICTypeInfo varInfo = var.validate(className);
		if(varInfo==null){
			return null;
		}
		
		if(!varInfo.isICClass()){
			String debugMessage = String.format("AST_LOCATION_FIELD.validate: The expression is not an object, so it doesn't have the field '%s'. exp : %s",
					fieldName, varInfo);
			DebugPrint.print(debugMessage);
			return null;
		}
		
		this.varClass = varInfo.ICType;
		SEMANTIC_SymbolInfo fieldFound = SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(varClass,fieldName);

		if(fieldFound==null  ||  (!(fieldFound instanceof SEMANTIC_VariableSymbolInfo))){
			String debugMessage = String.format("AST_LOCATION_FIELD.validate: The class '%s' doesn't have a field '%s'.",
					varInfo, fieldName);
			DebugPrint.print(debugMessage);
			return null;			
		}

		return ((SEMANTIC_VariableSymbolInfo) fieldFound).variableType;
	}
	
	@Override
	public IR_ExpLocationField createIR() throws SEMANTIC_SemanticAnalysisException{
		assertClassAndFunctionNamesInitialized();
		SEMANTIC_VariableSymbolInfo fieldFound = (SEMANTIC_VariableSymbolInfo)SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(varClass,fieldName);
		this.var.currentClassName=this.currentClassName;
		this.var.currentFunctionName=this.currentFunctionName;
		return new IR_ExpLocationField(var.createIR(), fieldFound.offset);
	}
}