package AST;

import IR.IR_StatementList;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;

public class AST_DoublyStatementList extends AST_StatementList 
{	
	public AST_DoublyStatementList(AST_StatementList stmtList){
		super(stmtList);
	}
	
	@Override
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_SymbolTable.createNewScope();
		SEMANTIC_ICTypeInfo result = super.validate(className);
		SEMANTIC_SymbolTable.closeCurrentScope();
		return result;
	}
	
	@Override
	public IR_StatementList createIR() throws SEMANTIC_SemanticErrorException{
		SEMANTIC_SymbolTable.createNewScope();
		IR_StatementList stmtListIR = super.createIR();
		SEMANTIC_SymbolTable.closeCurrentScope();
		return stmtListIR;
	}
}
