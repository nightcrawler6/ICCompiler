package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;

public class AST_BinopTimes extends AST_Binop{
	
	public SEMANTIC_ICTypeInfo validate(String className){
		return new SEMANTIC_ICTypeInfo();
	}
}
