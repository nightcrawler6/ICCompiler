package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;

public class AST_LiteralInteger extends AST_Literal{
	public int i;
	
	public AST_LiteralInteger(Integer i){
		this.i = i;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className){
		return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_INT,0);
	}
}
