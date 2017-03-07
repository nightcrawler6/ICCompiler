

import java.io.*;

import AST.*;
import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_StringCollector;
import CODEGEN.CODEGEN_TemporaryFactory;
import IR.IR_Program;
import SEMANTIC.SEMANTIC_SymbolTable;
import java_cup.runtime.Symbol;

public class Main{
	
	static public void main(String argv[]) throws Exception{
		Lexer l;
		Parser p;
		FileReader file_reader;
		String inputICLocation = argv[0];
		String assemblyFileLocation = argv[1];
		
		try{
			file_reader = new FileReader(inputICLocation);

			l = new Lexer(file_reader);

			p = new Parser(l);
			
			SEMANTIC_SymbolTable.hashTable.clear();
			Symbol temp=p.parse();
			AST_Program astNode = (AST_Program)temp.value;
			if(astNode.validate(null)!=null){				
				CODEGEN_AssemblyFilePrinter.reset();
				FileWriter printer = CODEGEN_AssemblyFilePrinter.getInstance(assemblyFileLocation);
				CODEGEN_TemporaryFactory.reset();
				
				SEMANTIC_SymbolTable.hashTable.clear();
				CODEGEN_StringCollector.reset();
				IR_Program irNode = astNode.createIR();
				
				CODEGEN_StringCollector.printStringsToAssembly();
				
				CODEGEN_AssemblyFilePrinter.printVirtualFunctionsTablesAndUpdateVFTAdresses();
				
				CODEGEN_AssemblyFilePrinter.getInstance(null).write(String.format(".text%s", System.lineSeparator()));

				irNode.generateCode();
				printer.close();
			}
			else{
				BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(assemblyFileLocation)));
				outputWriter.write("FAIL");
				outputWriter.close();
			}
			
    	}     
		catch (Exception e){
			try{
				BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(assemblyFileLocation)));
				outputWriter.write("FAIL");
				outputWriter.close();
			}
			catch (Exception exiting){
				System.out.println(String.format("Error: %s", exiting.getMessage()));
			}
		}
		
	}
}


