package AST;

import IR.IR_EXP;
import IR.IR_Method;
import IR.IR_StatementReturn;
import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_NoInitForReturnTypeException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import UTILS.DebugPrint;

public class AST_StatementReturn extends AST_Statement{
	public AST_Exp returnedExpression;
	
	public AST_StatementReturn(AST_Exp exp){
		this.returnedExpression = exp;
	}
	
	private SEMANTIC_ICTypeInfo validateEmptyReturn(){
		if (returnedExpression != null){
			DebugPrint.print("AST_STMT_RETURN.validateEmptyReturn: The statement returns a value.");
			return null;
		}
		
		return new SEMANTIC_ICTypeInfo();
	}
	
	private SEMANTIC_ICTypeInfo validateValuedReturn(String className) throws SEMANTIC_SemanticErrorException{
		if (returnedExpression == null){
			DebugPrint.print("AST_STMT_RETURN.validateValuedReturn: The statement doesn't return a value.");
			return null;
		}
		
		SEMANTIC_ICTypeInfo expType = returnedExpression.validate(className);
		if (expType == null){
			DebugPrint.print("AST_STMT_RETURN.validateValuedReturn: The returned expression isn't valid.");
			return null;
		}
		
		if (!SEMANTIC_SymbolTable.validatePredeccessor(expectedReturnType, expType)){
			String debugMessage = String.format("AST_STMT_RETURN.validateValuedReturn: return mismatch, expected : %s, found : %s.",
					expectedReturnType, expType);
			DebugPrint.print(debugMessage);
			return null;
		}
		
		doesAlwaysReturnValue = true;
		return new SEMANTIC_ICTypeInfo();
	}

	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		if (expectedReturnType == null){
			throw new SEMANTIC_NoInitForReturnTypeException();
		}
		else if (expectedReturnType.ICType.equals(SEMANTIC_ICTypeInfo.IC_TYPE_VOID)){
			return validateEmptyReturn();
		}
		else{
			return validateValuedReturn(className);
		}
	}
	
	private void bequeathClassAndFunctionNamesToChild() throws SEMANTIC_NoInitForMethodException{
		assertClassAndFunctionNamesInitialized();
		
		if (returnedExpression != null){
			returnedExpression.currentClassName = this.currentClassName;
			returnedExpression.currentFunctionName = this.currentFunctionName;		
		}
	}
	
	@Override
	public IR_StatementReturn createIR() throws SEMANTIC_SemanticErrorException{
		bequeathClassAndFunctionNamesToChild();
		
		IR_EXP returnedExpressionIR = null;
		if (returnedExpression != null){
			returnedExpressionIR = returnedExpression.createIR();	
		}
		
		String methodEpilogLabelName = String.format("%s%s_%s%s", 
													 AST_Method.METHOD_LABEL_PREFIX, 
													 currentClassName, 
													 currentFunctionName, 
													 IR_Method.EPILOG_LABEL_SUFFIX);
		
		return new IR_StatementReturn(returnedExpressionIR, methodEpilogLabelName);
	}
}
