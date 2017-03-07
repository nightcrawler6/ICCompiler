package AST;

import IR.IR_ExpMemory;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class AST_ExpLocation extends AST_Exp{
	public AST_Location location;
	
	public AST_ExpLocation(AST_Location location){
		this.location = location;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		return location.validate(className);
	}
	
	@Override
	public IR_ExpMemory createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		
		this.location.currentClassName = this.currentClassName;
		this.location.currentFunctionName = this.currentFunctionName;
		
		return new IR_ExpMemory(location.createIR());
	}
	
}