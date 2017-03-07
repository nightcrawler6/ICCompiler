package AST;

import IR.IR_EXP;
import IR.IR_ExpLocationSub;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import UTILS.DebugPrint;

public class AST_LocationSub extends AST_Location{
	public AST_Exp var;
	public AST_Exp subscript;
	
	public AST_LocationSub(AST_Exp var,AST_Exp subscript){
		this.var = var;
		this.subscript = subscript;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		SEMANTIC_ICTypeInfo varInfo = var.validate(className);
		SEMANTIC_ICTypeInfo subscriptInfo = subscript.validate(className);
		
		if(!subscriptInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT)){
			DebugPrint.print("AST_LOCATION_SUBSCRIPT.validate: the array index is not an integer. index : " + subscriptInfo);
			return null;			
		}

		if(varInfo.pointerDepth<1){
			DebugPrint.print("AST_LOCATION_SUBSCRIPT.validate: the expression is not an array. exp : " + varInfo);
			return null;
		}
			
		return new SEMANTIC_ICTypeInfo(varInfo.ICType,varInfo.pointerDepth -1);
	}
	
	@Override
	public IR_EXP createIR() throws SEMANTIC_SemanticAnalysisException{
		assertClassAndFunctionNamesInitialized();
		this.var.currentClassName=this.currentClassName;
		this.var.currentFunctionName=this.currentFunctionName;
		this.subscript.currentClassName=this.currentClassName;
		this.subscript.currentFunctionName=this.currentFunctionName;
		
		return new IR_ExpLocationSub(this.var.createIR(),this.subscript.createIR());
	}
	
}