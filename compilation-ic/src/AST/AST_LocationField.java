package AST;

import IR.IR_ExpLocationField;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolInfo;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;

public class AST_LocationField extends AST_Location{
	public String varClass;
	public AST_Exp var;
	public String fieldName;
	
	public AST_LocationField(AST_Exp var,String fieldName){
		this.var = var;
		this.fieldName = fieldName;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_ICTypeInfo varInfo = var.validate(className);
		if(varInfo==null){
			return null;
		}
		
		if(!varInfo.isICClass()){
			return null;
		}
		
		this.varClass = varInfo.ICType;
		SEMANTIC_SymbolInfo fieldFound = SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(varClass,fieldName);

		if(fieldFound==null  ||  (!(fieldFound instanceof SEMANTIC_VariableSymbolInfo))){
			return null;			
		}

		return ((SEMANTIC_VariableSymbolInfo) fieldFound).variableType;
	}
	
	@Override
	public IR_ExpLocationField createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		SEMANTIC_VariableSymbolInfo fieldFound = (SEMANTIC_VariableSymbolInfo)SEMANTIC_SymbolTable.searchSymbolInfoInClassAndUp(varClass,fieldName);
		this.var.currentClassName=this.currentClassName;
		this.var.currentFunctionName=this.currentFunctionName;
		return new IR_ExpLocationField(var.createIR(), fieldFound.offset);
	}
}