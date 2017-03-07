package CODEGEN;

import java.io.IOException;

import SEMANTIC.SEMANTIC_TooManyTempsException;

public class CODEGEN_Utils {

	public static void codeGen_Push(CODEGEN_StringNLBuilder printed, String pushedObject) throws IOException{
		printed.appendNL("addi $sp,$sp,-4");
		printed.appendNL(String.format("sw %s,0($sp)", pushedObject));
	}
	
	public static void codeGen_Pop(CODEGEN_StringNLBuilder printed, String pop) throws IOException{
		printed.appendNL(String.format("lw %s,0($sp)",pop));
		printed.appendNL("addi $sp,$sp,4");
	}
	
	public static CODEGEN_Temporary codeGen_malloc(CODEGEN_StringNLBuilder printed,int allocationSize) throws IOException, SEMANTIC_TooManyTempsException{
		printed.appendNL(String.format("li $a0,%d",allocationSize));
		return common_codeGen_malloc(printed);
	}
	
	public static CODEGEN_Temporary codeGen_malloc(CODEGEN_StringNLBuilder printed,CODEGEN_Temporary allocationSize) throws IOException, SEMANTIC_TooManyTempsException{
		printed.appendNL(String.format("mov $a0,%s",allocationSize.getName()));
		return common_codeGen_malloc(printed);

	}
	
	private static CODEGEN_Temporary common_codeGen_malloc(CODEGEN_StringNLBuilder printed) throws IOException, SEMANTIC_TooManyTempsException{
		printed.appendNL( "li $v0,9");
		printed.appendNL("syscall");
		CODEGEN_Temporary resultAddress = CODEGEN_TemporaryFactory.getAndAddNewTemp();
		printed.appendNL(String.format("mov %s,$v0",resultAddress.getName()));		
		return resultAddress;
	}	
}
