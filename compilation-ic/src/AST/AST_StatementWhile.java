package AST;

import IR.IR_EXP;
import IR.IR_Statement;
import IR.IR_StatementWhile;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;

public class AST_StatementWhile extends AST_StatementCondition{

	public AST_StatementWhile(AST_Exp cond, AST_Statement body){
		super(cond, body);
	}

	@Override
	public IR_StatementWhile createIR() throws SEMANTIC_SemanticErrorException{
		bequeathClassAndFunctionNamesToChildren();
		
		IR_EXP condIR = cond.createIR();
		
		SEMANTIC_SymbolTable.createNewScope();
		IR_Statement bodyIR = body.createIR();
		SEMANTIC_SymbolTable.closeCurrentScope();
		
		SEMANTIC_FunctionSymbolInfo functionInfo = getFunctionSymbolInfo();
		int whileIndex = functionInfo.getNewWhileIndex();
		String labelName = String.format("Label_%d_while_%s", whileIndex, getLabelPrefix());
		
		return new IR_StatementWhile(condIR, bodyIR, labelName);
	}
	
}