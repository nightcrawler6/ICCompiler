package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_Utils;
import CODEGEN.CODEGEN_StringNLBuilder;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;
import SEMANTIC.SEMANTIC_SymbolTable;
import SEMANTIC.SEMANTIC_UnsupportedBinOpException;

public class IR_ExpBinop extends IR_EXP {
	public IR_EXP left;
	public IR_EXP right;
	public IR_Binop operation;
	public boolean isStrConcat;
	
	public IR_ExpBinop(IR_EXP left, IR_EXP right, IR_Binop operation, boolean isStrConcat){
		this.left = left;
		this.right = right;
		this.operation = operation;
		this.isStrConcat = isStrConcat;
	}
	
	private boolean isMathematicOperation(){
		switch(this.operation){
		case TIMES:
			return true;
		case PLUS: 
			return true;
		case MINUS:
			return true;
		case DIVIDE:
			return true;
		default:
			break;
		}
		return false;
	}
	
	private String findSpecificBinop(){
		switch(this.operation){
		case LT:
			return "blt";
		case GT:
			return "bgt";
		case LTE:
			return "ble";
		case GTE:
			return "bge";
		case EQUAL:
			return "beq";
		case NEQUAL: 
			return "bne";
		case TIMES:
			return "mul";
		case PLUS:
			return "add";
		case MINUS:
			return "sub";
		case DIVIDE:
			return "div";
		default:
			break;
		}
		return null;
	}
	
	private String getOppositeConditionalOperation() throws SEMANTIC_UnsupportedBinOpException{
		switch(this.operation){
		case GT:
			return "ble";
		case GTE:
			return "blt";
		case LT:
			return "bge";
		case LTE:
			return "bgt";
		case EQUAL:
			return "bne";
		case NEQUAL:
			return "beq";
		default:
			break;
		}
		throw new SEMANTIC_UnsupportedBinOpException();
	}
	
	private void generateCodeForStrConcat(CODEGEN_Temporary str1Temp, 
										  CODEGEN_Temporary str2Temp, 
										  CODEGEN_Temporary resultTemp,
										  CODEGEN_StringNLBuilder printed) throws IOException{
		CODEGEN_Utils.codeGen_Push(printed, str2Temp.getName());
		CODEGEN_Utils.codeGen_Push(printed, str1Temp.getName());
		
		printed.appendNL(String.format("jal %s", IR_Program.STRCAT_FUNCTION_LABEL));
		printed.appendNL(String.format("addi $sp, $sp, %d", 2 * SEMANTIC_SymbolTable.ADDRESS_SIZE));
		printed.appendNL(String.format("mov %s, $v0", resultTemp.getName()));
	}
	
	@Override
	public CODEGEN_Temporary generateCode() throws IOException, SEMANTIC_SemanticAnalysisException{
		CODEGEN_Temporary result = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary t1 = left.generateCode();
		CODEGEN_Temporary t2 = right.generateCode();
		
		CODEGEN_StringNLBuilder printed = new CODEGEN_StringNLBuilder();
		
		if(isMathematicOperation()){
			if (isStrConcat){
				generateCodeForStrConcat(t1, t2, result, printed);
			}
			else{
				printed.appendNL(String.format("%s %s,%s,%s", findSpecificBinop(),result.getName(),t1.getName(), t2.getName()));				
			}
		}
		else{			
			String branchLabelOK = String.format("Label_%d_binop_ok", CODEGEN_AssemblyFilePrinter.addLabelIndex());
			String branchLabelEnd =  String.format("Label_%d_binop_end", CODEGEN_AssemblyFilePrinter.addLabelIndex());
			printed.appendNL(String.format("li %s,0",result.getName()));
			printed.appendNL(String.format("%s %s,%s,%s", findSpecificBinop(),t1.getName(), t2.getName(), branchLabelOK));
			printed.appendNL(String.format("%s %s,%s,%s", getOppositeConditionalOperation(),t1.getName(),t2.getName(),branchLabelEnd));
			printed.appendNL(String.format("%s:", branchLabelOK));
			printed.appendNL(String.format("addi %s,%s,1",result.getName(), result.getName()));
			printed.appendNL(String.format("%s:", branchLabelEnd));
		}
		
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
		
		return result;
	}
}
