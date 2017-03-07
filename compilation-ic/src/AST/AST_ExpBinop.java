package AST;

import IR.IR_Binop;
import IR.IR_EXP;
import IR.IR_ExpBinop;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_SymbolTable;
import UTILS.DebugPrint;

public class AST_ExpBinop extends AST_Exp{
	public AST_Binop op;
	public AST_Exp left;
	public AST_Exp right;
	public boolean isStrConcat = false;
	
	public AST_ExpBinop(AST_Exp left,AST_Exp right,AST_Binop op){
		this.left = left;
		this.right = right;
		this.op = op;
	}
	

	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticErrorException{
		SEMANTIC_ICTypeInfo leftInfo  = left.validate(className);
		SEMANTIC_ICTypeInfo rightInfo = right.validate(className);
		
		if(leftInfo==null || rightInfo==null){
			return null;
		}
			
		if(op instanceof AST_BinopPlus){
			if(leftInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT) && rightInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT)){
				return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_INT,0);
			}
			
			if(leftInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_STRING) && rightInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_STRING)){
				isStrConcat = true;
				return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_STRING,0);
			}
			
			String debugMessage = String.format("AST_EXP_BINOP.validate: %s + %s is undefined.", leftInfo, rightInfo);
			DebugPrint.print(debugMessage);
			return null;
		}
		else if(op instanceof AST_BinopMinus || op instanceof AST_BinopTimes || op instanceof AST_BinopDivide){
			if(leftInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT) && rightInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT))
				return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_INT,0);
			
			return null;			
		}
		
		else if(op instanceof AST_BinopGT || op instanceof AST_BinopGTE || op instanceof AST_BinopLT || op instanceof AST_BinopLTE){
			if(leftInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT) && rightInfo.isFlatICType(SEMANTIC_ICTypeInfo.IC_TYPE_INT)){
				return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_INT,0);
			}
			return null;					
		}
		else if(op instanceof AST_BinopEqual || op instanceof AST_BinopNotEqual){

			if (SEMANTIC_SymbolTable.validatePredeccessor(rightInfo, leftInfo) ||
				SEMANTIC_SymbolTable.validatePredeccessor(leftInfo, rightInfo)){
				return new SEMANTIC_ICTypeInfo(SEMANTIC_ICTypeInfo.IC_TYPE_INT,0);
			}
			
			return null;			
		}
		return null;
	}
	
	IR_Binop getBinOperation(){
		if(this.op instanceof AST_BinopMinus){
			return IR_Binop.MINUS;
		}
		if(this.op instanceof AST_BinopTimes){
			return IR_Binop.TIMES;
		}
		if(this.op instanceof AST_BinopPlus){
			return IR_Binop.PLUS;
		}
		if(this.op instanceof AST_BinopDivide){
			return IR_Binop.DIVIDE;
		}
		if(this.op instanceof AST_BinopGT){
			return IR_Binop.GT;
		}
		if(this.op instanceof AST_BinopGTE){
			return IR_Binop.GTE;
		}
		if(this.op instanceof AST_BinopLT){
			return IR_Binop.LT;
		}
		if(this.op instanceof AST_BinopLTE){
			return IR_Binop.LTE;
		}
		if(this.op instanceof AST_BinopEqual){
			return IR_Binop.EQUAL;
		}
		if(this.op instanceof AST_BinopNotEqual){
			return IR_Binop.NEQUAL;
		}
		
		return null;
	}
	
	public IR_ExpBinop createIR() throws SEMANTIC_SemanticErrorException{
		IR_Binop currentOP=getBinOperation();
		assertClassAndFunctionNamesInitialized();
		
		left.currentClassName=this.currentClassName;
		right.currentClassName=this.currentClassName;
		left.currentFunctionName=this.currentFunctionName;
		right.currentFunctionName=this.currentFunctionName;
		
		IR_EXP leftExp  = left.createIR();
		IR_EXP rightExp = right.createIR();
		return new IR_ExpBinop(leftExp, rightExp, currentOP, isStrConcat);
	}
}