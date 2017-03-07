package AST;

import IR.IR_EXP;
import IR.IR_ExpNewArray;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class AST_ExpNewArray extends AST_Exp{
	public AST_Type type;
	public AST_Exp size;
	
	public AST_ExpNewArray(AST_Type type, AST_Exp size){
		this.type = type;
		this.size = size;
	}
	
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_ICTypeInfo typeInfo = type.validate(className);
		SEMANTIC_ICTypeInfo sizeInfo = size.validate(className);
		
		if(!sizeInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT))
			return null;
		
		if(typeInfo == null)
			return null;
		
		return new SEMANTIC_ICTypeInfo(typeInfo.ICType,typeInfo.pointerDepth+1);
	}


	@Override
	public IR_EXP createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		this.size.currentFunctionName=this.currentFunctionName;
		this.size.currentClassName=this.currentClassName;
		return new IR_ExpNewArray(this.size.createIR());
	}
	
}
