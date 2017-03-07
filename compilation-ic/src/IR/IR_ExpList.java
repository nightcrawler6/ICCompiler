package IR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import CODEGEN.CODEGEN_Temporary;
import SEMANTIC.SEMANTIC_CodeGenExpException;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_ExpList extends IR_EXP{
	public IR_EXP head;
	public IR_ExpList tail;
	public IR_ExpList(IR_EXP head, IR_ExpList tail)
	{
		this.head=head;
		this.tail=tail;
	}
	
	public CODEGEN_Temporary generateCode() throws SEMANTIC_CodeGenExpException{
		throw new SEMANTIC_CodeGenExpException();
	}
	
	public List<CODEGEN_Temporary> generateCodeList() throws IOException, SEMANTIC_CodeGenExpException, SEMANTIC_SemanticErrorException{
		List<CODEGEN_Temporary> argsList = new ArrayList<CODEGEN_Temporary>();
		argsList.add(head.generateCode());
		IR_ExpList iterator = tail;

		while(iterator!=null){
			if(iterator.head!=null){
				argsList.add(iterator.head.generateCode());
			}
			iterator = iterator.tail;
		}
		return argsList;
	}
}
