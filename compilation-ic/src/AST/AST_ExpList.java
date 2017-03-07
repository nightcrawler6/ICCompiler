package AST;

import IR.IR_ExpList;
import SEMANTIC.SEMANTIC_SemanticErrorException;
import SEMANTIC.SEMANTIC_NoHeadFoundException;

public class AST_ExpList extends AST_Node{
	public AST_Exp head;
	public AST_ExpList tail;

	public AST_ExpList(AST_Exp head,AST_ExpList tail) throws SEMANTIC_NoHeadFoundException{
		if ((head == null) && (tail != null)){
			throw new SEMANTIC_NoHeadFoundException();
		}
		AST_ExpList iterator=tail;
		if((iterator!=null)&&(!iterator.isEmpty())){
			this.head=tail.head;
			while(iterator.tail!=null){
				iterator=iterator.tail;
			}
			iterator.tail=new AST_ExpList(head, null);
			this.tail=tail.tail;
		}
		else{
			this.head=head;
			this.tail=null;
		}
	}
	public boolean isEmpty(){
		return ((this.tail==null)&&(this.head==null));
	}
	
	public IR_ExpList createIR() throws SEMANTIC_SemanticErrorException{
		assertClassAndFunctionNamesInitialized();
		this.head.currentClassName=this.currentClassName;
		this.head.currentFunctionName=this.currentFunctionName;
		IR_ExpList temp=null;
		if(this.tail!=null){
			this.tail.currentClassName=this.currentClassName;
			this.tail.currentFunctionName=this.currentFunctionName;
			temp=this.tail.createIR();
		}
		return new IR_ExpList(this.head.createIR(),temp);
	}
}