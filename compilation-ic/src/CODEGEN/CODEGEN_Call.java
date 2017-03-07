package CODEGEN;

import IR.IR_Binop;
import IR.IR_ExpBinop;
import IR.IR_ExpCall;
import IR.IR_ExpList;
import IR.IR_ExpMemory;
import IR.IR_ExpNewArray;
import IR.IR_ExpNewClass;
import IR.IR_LiteralConstant;
import IR.IR_LiteralString;
import IR.IR_Method;
import IR.IR_MethodList;
import IR.IR_StatementStoreCommand;
import IR.IR_StatementWhile;
import IR.IR_Temporary;
import SEMANTIC.SEMANTIC_TooManyTempsException;

public class CODEGEN_Call {
	
	public String convertOpToMipsBinop(IR_Binop op, CODEGEN_Temporary left, CODEGEN_Temporary right, CODEGEN_Temporary result){
		switch(op){
		case PLUS:
			return String.format("add %s,%s,%s", result.getName(),left.getName(), right.getName());
		case MINUS:
			return String.format("sub %s,%s,%s",result.getName(),left.getName(), right.getName());
		case DIVIDE:
			return String.format("div %s,%s,%s", result.getName(), left.getName(), right.getName());
		case TIMES:
			return String.format("mult %s,%s,%s", result.getName(), left.getName(), right.getName());
		case LT:
			return String.format("slt %s,%s,%s",result.getName(), left.getName(), right.getName());
		case LTE:
			return String.format("",result.getName(),left.getName(), right.getName());
		case GT:
			break;
		case GTE:
			break;
		case EQUAL:
			break;
		case NEQUAL:
			break;
		}
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_ExpBinop binopExp) throws SEMANTIC_TooManyTempsException{
		CODEGEN_Temporary t1 = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		return t1;
	}
		
	public CODEGEN_Temporary codeGen_exp(IR_Temporary temp){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_LiteralConstant literalConst){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_LiteralString literalString){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_ExpCall callExp){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_ExpList expList){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_ExpMemory memExp){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_ExpNewArray newArrayExp){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_exp(IR_ExpNewClass newClass){
		return null;
	}
	
	
	public CODEGEN_Temporary codeGen_move(IR_StatementStoreCommand moveStmt){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_expCall(IR_ExpCall expCall){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_expMem(IR_ExpMemory expMem){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_LiteralConst(IR_LiteralConstant literalConst){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_LiteralString(IR_LiteralString literalString){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_expNewArray(IR_ExpNewArray expNewArray){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_method(IR_Method method){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_method_list(IR_MethodList methodsList){
		return null;
	}
	
	public CODEGEN_Temporary codeGen_STMT_WHILE(IR_StatementWhile stmtWhile){
		return null;
	}
	
}
