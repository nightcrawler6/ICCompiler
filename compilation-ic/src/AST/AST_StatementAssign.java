package AST;

import IR.IR_StatementStoreCommand;
import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_NullFieldException;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;

public class AST_StatementAssign extends AST_Statement{
	public AST_Location location;
	public AST_Exp expression;

	public AST_StatementAssign(AST_Location location, AST_Exp exp) throws SEMANTIC_NullFieldException{
		if (location == null){
			throw new SEMANTIC_NullFieldException("AST_STMT_ASSIGN.location");
		}
		if (exp == null){
			throw new SEMANTIC_NullFieldException("AST_STMT_ASSIGN.exp");
		}
		
		this.location = location;
		this.expression = exp;
	}
	
	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_ICTypeInfo locationType = location.validate(className);
		if (locationType == null){
			return null;
		}
		
		SEMANTIC_ICTypeInfo expressionType = expression.validate(className);
		if (expressionType == null){
			return null;
		}
		
		if (!SEMANTIC_SymbolTable.validatePredeccessor(locationType, expressionType)){
			return null;
		}
		
		return new SEMANTIC_ICTypeInfo();
	}
	
	private void bequeathClassAndFunctionNamesToChildren() throws SEMANTIC_NoInitForMethodException{
		assertClassAndFunctionNamesInitialized();
		
		location.currentClassName = this.currentClassName;
		location.currentFunctionName = this.currentFunctionName;
		
		expression.currentClassName = this.currentClassName;
		expression.currentFunctionName = this.currentFunctionName;
	}
	
	@Override
	public IR_StatementStoreCommand createIR() throws SEMANTIC_SemanticErrorException{
		bequeathClassAndFunctionNamesToChildren();
		return new IR_StatementStoreCommand(location.createIR(), expression.createIR());
	}
	
}