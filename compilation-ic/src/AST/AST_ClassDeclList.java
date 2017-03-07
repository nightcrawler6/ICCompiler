package AST;

import IR.IR_ClassDeclList;
import SEMANTIC.SEMANTIC_SemanticErrorException;

public class AST_ClassDeclList extends AST_Node{
	
	public AST_ClassDecl head;
	public AST_ClassDeclList tail;

	public AST_ClassDeclList(AST_ClassDecl head,AST_ClassDeclList tail){
		AST_ClassDeclList iterator=tail;
		if((iterator!=null)&&(!iterator.isEmpty())){
			this.head=tail.head;
			while(iterator.tail!=null){
				iterator=iterator.tail;
			}
			iterator.tail=new AST_ClassDeclList(head, null);
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
	
	public IR_ClassDeclList createIR() throws SEMANTIC_SemanticErrorException{
		if(tail!=null){
			return new IR_ClassDeclList(head.createIR(),tail.createIR());
		}
		return new IR_ClassDeclList(head.createIR(),null);
	}
}

