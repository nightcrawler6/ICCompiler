package AST;

import SEMANTIC.SEMANTIC_ICTypeInfo;

public class AST_BinopMinus extends AST_Binop{
	
	public SEMANTIC_ICTypeInfo validate(String className){
		return new SEMANTIC_ICTypeInfo();
	}
}
