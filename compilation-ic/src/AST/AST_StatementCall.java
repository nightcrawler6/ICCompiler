package AST;

import IR.IR_StatementCall;
import SEMANTIC.SEMANTIC_ClassOrFunctionNamesNotInitializedExecption;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_NullFieldException;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import UTILS.DebugPrint;

public class AST_StatementCall extends AST_Statement{
	public AST_Call call;
	
	public AST_StatementCall(AST_Call call) throws SEMANTIC_NullFieldException{
		if (call == null){
			throw new SEMANTIC_NullFieldException("AST_STMT_CALL.call");
		}
		
		this.call = call;
	}
	
	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		if (call.validate(className) == null){
			DebugPrint.print("AST_STMT_CALL.validate: The call isn't valid.");
			return null;
		}
		
		return new SEMANTIC_ICTypeInfo();
	}
	
	private void bequeathClassAndFunctionNamesToChild() throws SEMANTIC_ClassOrFunctionNamesNotInitializedExecption{
		assertClassAndFunctionNamesInitialized();
		
		call.currentClassName = this.currentClassName;
		call.currentFunctionName = this.currentFunctionName;
	}
	
	@Override
	public IR_StatementCall createIR() throws SEMANTIC_SemanticAnalysisException{
		bequeathClassAndFunctionNamesToChild();
		return new IR_StatementCall(call.createIR());
	}
}
