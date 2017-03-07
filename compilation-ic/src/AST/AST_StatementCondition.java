package AST;

import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import UTILS.DebugPrint;

public abstract class AST_StatementCondition extends AST_Statement{
	public AST_Exp cond;
	public AST_Statement body;

	public AST_StatementCondition(AST_Exp cond,AST_Statement body){
		this.cond = cond;
		this.body = body;
	}

	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_ICTypeInfo conditionTypeInfo = cond.validate(className);
		if (conditionTypeInfo == null){
			DebugPrint.print("AST_STMT_COND.validate: The condition isn't valid");
			return null;
		}
		if (!conditionTypeInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT)){
			DebugPrint.print("AST_STMT_COND.validate: The condition's type isn't int.");
			return null;
		}
		
		SEMANTIC_SymbolTable.createNewScope();
		body.expectedReturnType = expectedReturnType;
		SEMANTIC_ICTypeInfo bodyTypeInfo = body.validate(className);
		SEMANTIC_SymbolTable.closeCurrentScope();
		if (bodyTypeInfo == null){
			DebugPrint.print("AST_STMT_COND.validate: The body isn't valid");
			return null;
		}
		
		doesAlwaysReturnValue = false;
		
		return new SEMANTIC_ICTypeInfo();
	}

	protected void bequeathClassAndFunctionNamesToChildren() throws SEMANTIC_NoInitForMethodException{
		assertClassAndFunctionNamesInitialized();
		
		cond.currentClassName = this.currentClassName;
		cond.currentFunctionName = this.currentFunctionName;
		
		body.currentClassName = this.currentClassName;
		body.currentFunctionName = this.currentFunctionName;
	}
	
	protected String getLabelPrefix(){
		return String.format("%s_%s", currentClassName, currentFunctionName);
	}
}