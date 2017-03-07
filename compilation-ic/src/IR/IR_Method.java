package IR;

import java.io.IOException;

import AST.AST_Node;
import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_Temporary;
import CODEGEN.CODEGEN_Misc;
import CODEGEN.CODEGEN_StringAttacher;
import CODEGEN.CODEGEN_TemporaryFactory;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_TempsPastLimitException;

public class IR_Method extends IR_Node 
{
	public IR_AsmLabel label; 
	public IR_StatementList body;
	public int frameSize;
	public boolean isMainFunc;
	public boolean isPrintIntFunc = false;
	
	public static final String EPILOG_LABEL_SUFFIX = "_epilog";
	
	public IR_Method(IR_AsmLabel label, IR_StatementList body, int frameSize, boolean isMainFunc, boolean isPrintIntFunc){
		this.label = label;
		this.body = body;
		this.frameSize = frameSize;
		this.isMainFunc = isMainFunc;
		this.isPrintIntFunc = isPrintIntFunc;
	}
	
	public static void appendProlog(CODEGEN_StringAttacher printed, int funcFrameSize) throws IOException{
		CODEGEN_Misc.codeGen_Push(printed, "$ra");
		CODEGEN_Misc.codeGen_Push(printed, "$fp");
		printed.appendNL("mov $fp, $sp");
		printed.appendNL(String.format("addi $sp,$sp,%d", funcFrameSize*(-1)));
	}
	
	public void printProlog() throws IOException{
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		appendProlog(printed, this.frameSize);
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
	
	public static void appendEpilog(CODEGEN_StringAttacher printed) throws IOException{
		printed.appendNL("mov $sp, $fp");
		CODEGEN_Misc.codeGen_Pop(printed, "$fp");
		CODEGEN_Misc.codeGen_Pop(printed, "$ra");
		printed.appendNL("jr $ra");
	}
	
	public void printEpilog() throws IOException{
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		appendEpilog(printed);
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
	
	public void generatePrintIntCode() throws IOException, SEMANTIC_TempsPastLimitException{
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();

		CODEGEN_Temporary givenInteger = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		CODEGEN_Temporary arg1Offset = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		
		printed.appendNL(String.format("addi %s,$fp,%d",arg1Offset.getName(), AST_Node.FRAME_OFFSET_OF_FIRST_FORMAL));
		printed.appendNL(String.format("lw %s,0(%s)",givenInteger.getName(),arg1Offset.getName()));
		
		printed.appendNL(String.format("mov $a0,%s", givenInteger.getName()));
		printed.appendNL("li $v0,1");
		printed.appendNL("syscall");
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
	
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		if(isPrintIntFunc){
			this.label = new IR_AsmLabel(PRINTINT_FUNC_LABEL);
		}
		this.label.generateCode();
		this.printProlog();
		if(isPrintIntFunc){
			generatePrintIntCode();
		}
		else if (body != null){
			body.generateCode();	
		}
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(String.format("%s%s:%s", this.label.name, EPILOG_LABEL_SUFFIX, System.lineSeparator()));
		this.printEpilog();
	}
}
