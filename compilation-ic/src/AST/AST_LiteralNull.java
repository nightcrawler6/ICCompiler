package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;

public class AST_LiteralNull extends AST_Literal{
	
	public AST_LiteralNull(){
		
	}
	public SEMANTIC_ICTypeInfo validate(String className){
		return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_NULL, 0);
	}
}
