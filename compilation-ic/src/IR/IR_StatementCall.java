package IR;

import java.io.IOException;

import SEMANTIC.SEMANTIC_SemanticErrorException;

public class IR_StatementCall extends IR_Statement{
	public IR_Call call;
	
	public IR_StatementCall(IR_Call call){
		this.call=call;
	}
	
	@Override
	public void generateCode() throws IOException, SEMANTIC_SemanticErrorException{
		call.generateCode();
	}
}
