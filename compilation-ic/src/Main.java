

import java.io.*;

import AST.*;
import CODEGEN.CODEGEN_AssemblyFilePrinter;
import CODEGEN.CODEGEN_StringCollector;
import CODEGEN.CODEGEN_TemporaryFactory;
import IR.IR_Program;
import SEMANTIC.SEMANTIC_SymbolTable;
import java_cup.runtime.Symbol;

public class Main
{
	public static final String OK_STRING = "OK";
	public static final String FAIL_STRING = "FAIL";
	
	static public void main(String argv[]) throws Exception
	{
		Lexer l;
		Parser p;
		FileReader file_reader;
		String inputFileName = argv[0];
		String outputFileName = argv[1];
		String assemblyOutput = argv[1];
		
		// TODO: Surround with try-catch
	//	try
	//	{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFileName);

			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);
			SEMANTIC_SymbolTable.hashTable.clear();
			Symbol temp=p.parse();
			AST_Program astNode = (AST_Program)temp.value;
			if(astNode.validate(null)!=null)
			{
				// Writing output
				BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(outputFileName)));
				outputWriter.write(OK_STRING);
				outputWriter.close();
				
				CODEGEN_AssemblyFilePrinter.reset();
				FileWriter printer = CODEGEN_AssemblyFilePrinter.getInstance(assemblyOutput);
				CODEGEN_TemporaryFactory.reset();
				// builds ir tree.
				SEMANTIC_SymbolTable.hashTable.clear();
				CODEGEN_StringCollector.reset();
				IR_Program irNode = astNode.createIR();
				
				// prints the strings before use them in the generate code.
				CODEGEN_StringCollector.printStringsToAssembly();
				
				// prints vftables
				CODEGEN_AssemblyFilePrinter.printVirtualFunctionsTablesAndUpdateVFTAdresses();
				
				CODEGEN_AssemblyFilePrinter.getInstance(null).write(String.format(".text%s", System.lineSeparator()));
				// codeGen
				irNode.generateCode();
				printer.close();
			}
			else
			{
			// Writing output
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(outputFileName)));
			outputWriter.write(FAIL_STRING);
			outputWriter.close();
			}
			
    /*	}     
		catch (Exception e)
		{
			try
			{
				BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File(outputFileName)));
				outputWriter.write(FAIL_STRING);
				outputWriter.close();
			}
			catch (Exception e2)
			{
			}
		}
		*/
	}
}


