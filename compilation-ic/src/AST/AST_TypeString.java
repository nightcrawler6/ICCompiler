package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;

public class AST_TypeString extends AST_Type{
	public SEMANTIC_ICTypeInfo validate(String className){
		return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_STRING,0);
	}
}
