package IR;

import java.io.IOException;

import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_StringNLBuilder;

public class IR_AsmLabel extends IR_Node{
	public String name;
	
	public IR_AsmLabel(String name){
		this.name = name;
	}
	
	public void generateCode() throws IOException{
		CODEGEN_StringNLBuilder printed = new CODEGEN_StringNLBuilder();
		printed.appendNL(String.format("%s:", name));	
		CODEGEN_AssemblyFilePrinter.getInstance(null).write(printed.toString());
	}
}
