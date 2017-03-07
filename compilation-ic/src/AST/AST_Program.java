package AST;

import IR.IR_Program;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_SymbolTable;
import UTILS.DebugPrint;

public class AST_Program extends AST_Node{
	public AST_ClassDeclList l;
	
	public AST_Program(AST_ClassDeclList l){
		this.l=l;
	}
	
	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException{
		SEMANTIC_SymbolTable.createNewScope();
		AST_ClassDeclList iterator=l;
		while((iterator!=null) && (iterator.head!=null)){
			if(iterator.head.validate(className)==null){
				return null;
			}
			iterator=iterator.tail;
		}

		if(!SEMANTIC_SymbolTable.doesOneMainExistInProgram()){
			DebugPrint.print("AST_PROGRAM.validate: The program doesn't contain exactly one valid main method");
			return null;
		}
		SEMANTIC_SymbolTable.closeCurrentScope();
		return new SEMANTIC_ICTypeInfo();
	}
	
	public IR_Program createIR() throws SEMANTIC_SemanticAnalysisException{
		return new IR_Program(l.createIR());
	}
	
}
