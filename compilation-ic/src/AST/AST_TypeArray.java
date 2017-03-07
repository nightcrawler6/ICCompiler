package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class AST_TypeArray extends AST_Type{
	public AST_Type type;
	
	
	public AST_TypeArray(AST_Type type){
		this.type = type;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		SEMANTIC_ICTypeInfo temp =type.validate(className);
		if(temp!=null){
			return new SEMANTIC_ICTypeInfo(temp.ICType, temp.pointerDepth+1);
		}
		else{
			return null;
		}
	}
}
