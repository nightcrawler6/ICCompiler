package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_StringAttacher;

public class IR_AsmLabel extends IR_Node{
	public String name;
	
	public IR_AsmLabel(String name){
		this.name = name;
	}
	
	public void generateCode() throws IOException{
		CODEGEN_StringAttacher printed = new CODEGEN_StringAttacher();
		printed.appendNL(String.format("%s:", name));	
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
}
