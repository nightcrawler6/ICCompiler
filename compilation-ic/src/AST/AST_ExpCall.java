package AST;

import IR.IR_ExpCall;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class AST_ExpCall extends AST_Exp{ 
	public AST_Call call;
	
	public AST_ExpCall(AST_Call call){
		this.call = call;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		return call.validate(className);
	}
	
	public IR_ExpCall createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		
		this.call.currentClassName = this.currentClassName;
		this.call.currentFunctionName= this.currentFunctionName;
		
		return new IR_ExpCall(call.createIR());
	}
}
