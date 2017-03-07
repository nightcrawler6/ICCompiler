package AST;

import IR.IR_Node;
import IR.IR_StatementStoreCommand;
import SEMANTIC.SEMANTIC_NoInitForMethodException;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_NullFieldException;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_VariableSymbolInfo;
import UTILS.DebugPrint;

public class AST_StatementVariableDecl extends AST_Statement{
	public AST_Type varType;
	public String varName;
	public AST_Exp exp;
	public SEMANTIC_ICTypeInfo varICTypeInfo;
	
	public AST_StatementVariableDecl(AST_Type varType, String varName){
		this.varType = varType;
		this.varName = varName;
		this.exp = null;
	}
	
	public AST_StatementVariableDecl(AST_Type varType, String varName, AST_Exp exp){
		this.varType = varType;
		this.varName = varName;
		this.exp = exp;
	}
	
	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_ICTypeInfo varICTypeInfo = varType.validate(className);
		if (varICTypeInfo == null){
			String debugMessage = String.format("AST_STMT_VAR_DECL.validate: The type of the variable '%s' isn't valid.", 
					varName);
			DebugPrint.print(debugMessage);
			return null;
		}
		
		if (SEMANTIC_SymbolTable.doesSymbolExistInCurrentScope(varName)){
			String debugMessage = String.format("AST_STMT_VAR_DECL.validate: The variable '%s' is redefined.", 
					varName);
			DebugPrint.print(debugMessage);
			return null;
		}
		
		if (exp != null){
			SEMANTIC_ICTypeInfo expressionICTypeInfo = exp.validate(className);
			if (expressionICTypeInfo == null){
				String debugMessage = String.format("AST_STMT_VAR_DECL.validate: The initial expression of the variable '%s' isn't valid.", 
						varName);
				DebugPrint.print(debugMessage);
				return null;
			}
			
			if (!SEMANTIC_SymbolTable.validatePredeccessor(varICTypeInfo, expressionICTypeInfo)){
				String debugMessage = String.format("AST_STMT_VAR_DECL.validate: The assignment can't be done. var : %s, expression : %s", 
						varICTypeInfo, expressionICTypeInfo);
				DebugPrint.print(debugMessage);
				return null;
			}
		}
		
		SEMANTIC_VariableSymbolInfo varSymbolInfo = new SEMANTIC_VariableSymbolInfo(varName, varICTypeInfo);
		SEMANTIC_SymbolTable.insertNewSymbol(varSymbolInfo);
		
		return new SEMANTIC_ICTypeInfo();
	}
	
	private void bequeathClassAndFunctionNamesToAssignment(AST_StatementAssign assignment) throws SEMANTIC_NoInitForMethodException{
		assertClassAndFunctionNamesInitialized();
		
		assignment.currentClassName = this.currentClassName;
		assignment.currentFunctionName = this.currentFunctionName;
	}

	private AST_StatementAssign convertToAssignment() throws SEMANTIC_NullFieldException{
		AST_Location assignmentLocation = new AST_LocationSimple(varName);
		
		AST_Exp assignmentValue = exp;
		if (assignmentValue == null){
			assignmentValue = new AST_ExpLiteral(new AST_LiteralInteger(
					IR_Node.VAR_DEFAULT_INIT_VALUE)); 
		}
		
		return new AST_StatementAssign(assignmentLocation, assignmentValue);
	}
	
	@Override
	public IR_StatementStoreCommand createIR() throws SEMANTIC_SemanticErrorException{
		SEMANTIC_FunctionSymbolInfo functionInfo = getFunctionSymbolInfo();
		int variableOffset = AST_Node.FRAME_OFFSET_OF_FIRST_LOCAL - functionInfo.frameSize;
		SEMANTIC_VariableSymbolInfo variableInfo = new SEMANTIC_VariableSymbolInfo(varName, varICTypeInfo, variableOffset, false);
		SEMANTIC_SymbolTable.insertNewSymbol(variableInfo);
		functionInfo.frameSize += SEMANTIC_SymbolTable.ADDRESS_SIZE; 
		
		AST_StatementAssign assignment = convertToAssignment();
		bequeathClassAndFunctionNamesToAssignment(assignment);
		return assignment.createIR();
	}
}
