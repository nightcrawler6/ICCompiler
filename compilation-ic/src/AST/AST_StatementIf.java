package AST;

import IR.IR_EXP;
import IR.IR_Statement;
import IR.IR_StatementIf;
import SEMANTIC.SEMANTIC_FunctionSymbolInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_SymbolTable;

public class AST_StatementIf extends AST_StatementCondition{

	public AST_StatementIf(AST_Exp cond, AST_Statement body){
		super(cond, body);
	}
	
	@Override
	public IR_StatementIf createIR() throws SEMANTIC_SemanticAnalysisException{
		bequeathClassAndFunctionNamesToChildren();
		
		IR_EXP condIR = cond.createIR();
		
		SEMANTIC_SymbolTable.createNewScope();
		IR_Statement bodyIR = body.createIR();
		SEMANTIC_SymbolTable.closeCurrentScope();
		
		SEMANTIC_FunctionSymbolInfo functionInfo = getFunctionSymbolInfo();
		int ifIndex = functionInfo.getNewIfIndex();
		String labelName = String.format("Label_%d_if_%s", ifIndex, getLabelPrefix());
		
		return new IR_StatementIf(condIR, bodyIR, labelName);
	}
	
}