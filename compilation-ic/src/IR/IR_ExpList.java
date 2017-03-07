package IR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import CODEGEN.CODEGEN_Temporary;
import SEMANTIC.SEMANTIC_IRExpListGenerateCodeException;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class IR_ExpList extends IR_EXP{
	public IR_EXP head;
	public IR_ExpList tail;
	public IR_ExpList(IR_EXP head, IR_ExpList tail)
	{
		this.head=head;
		this.tail=tail;
	}
	
	public CODEGEN_Temporary generateCode() throws SEMANTIC_IRExpListGenerateCodeException{
		throw new SEMANTIC_IRExpListGenerateCodeException();
	}
	
	public List<CODEGEN_Temporary> generateCodeList() throws IOException, SEMANTIC_IRExpListGenerateCodeException, SEMANTIC_SemanticAnalysisException{
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
