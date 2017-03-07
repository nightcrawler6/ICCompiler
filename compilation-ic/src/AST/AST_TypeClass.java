package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class AST_TypeClass extends AST_Type{
	public String typeClassName;
	
	public AST_TypeClass(String typeClassName){
		this.typeClassName = typeClassName;
	}
	
	public SEMANTIC_ICTypeInfo validate(String receivedClassName) throws SEMANTIC_SemanticAnalysisException{
		if(SEMANTIC.SEMANTIC_SymbolTable.doesClassExist(typeClassName)){
			return new SEMANTIC_ICTypeInfo(typeClassName, 0);
		}
		else{
			return null;
		}
	}
	
}
