package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;

public class AST_LiteralQuote  extends AST_Literal{
	public String str;
	
	public AST_LiteralQuote(String str){
		this.str = str;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className){
		return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_STRING,0);
	}
}
