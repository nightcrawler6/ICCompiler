package AST;

import IR.IR_EXP;
import IR.IR_LiteralConstant;
import IR.IR_LiteralString;
import SEMANTIC.SEMANTIC_ICTypeInfo;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class AST_ExpLiteral extends AST_Exp{
	public AST_Literal l;
	
	public AST_ExpLiteral(AST_Literal l)
	{
		this.l=l;
	}


	public SEMANTIC_ICTypeInfo validate(String className) throws SEMANTIC_SemanticAnalysisException
	{
		return l.validate(className);
	}
	
	// create IR_LITERAL_CONST or IR_LITERAL_STRING according to the local field.
	public IR_EXP createIR()
	{
		if(this.l instanceof AST_LiteralInteger){
			return new IR_LiteralConstant(((AST_LiteralInteger) this.l).i);
		}else if(this.l instanceof AST_LiteralQuote){
			return new IR_LiteralString(((AST_LiteralQuote) this.l).str);
		}else if(this.l instanceof AST_LiteralNull){
			return new IR_LiteralConstant(0);
		}

		// This will never happen
		return null; 
	}
}
