package AST;

import IR.IR_Method;
import IR.IR_MethodList;
import SEMANTIC.SEMANTIC_SemanticAnalysisException;

public class AST_FieldMethodList extends AST_Node{
	public AST_FieldMethod head;
	public AST_FieldMethodList tail;
	
	public AST_FieldMethodList(AST_FieldMethod head, AST_FieldMethodList tail){
		AST_FieldMethodList iterator=tail;
		if((iterator!=null)&&(!iterator.isEmpty())){
			this.head=tail.head;

			while(iterator.tail!=null){
				iterator=iterator.tail;
			}
			iterator.tail=new AST_FieldMethodList(head, null);
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
	
	public IR_MethodList createIR() throws SEMANTIC_SemanticAnalysisException{
		assertClassNameInitialized();
		
		if (head == null){
			return new IR_MethodList(null, null);
		}
		if(this.tail!=null){
			this.tail.currentClassName = this.currentClassName;
		}
		
		this.head.currentClassName = this.currentClassName;
		if(this.head instanceof AST_Method){
			AST_Method astMethod=(AST_Method)this.head;
			IR_Method temp=astMethod.createIR();
			
			IR_MethodList tailCreateIRResult =  null;
			
			if(tail!=null){
				tailCreateIRResult = tail.createIR();
			}
				
			return new IR_MethodList(temp,tailCreateIRResult);
		}
		else{
			AST_Field astField = (AST_Field)this.head;
			astField.currentClassName = this.currentClassName;
			astField.createIR();
			AST_FieldMethodList iterator=tail;
			while((iterator!=null)&&(iterator.head instanceof AST_Field)){
				astField = (AST_Field)iterator.head;
				astField.currentClassName = this.currentClassName;
				astField.createIR();
				iterator=iterator.tail;
			}
			
			IR_Method headCreateIRResult = null;
			IR_MethodList tailCreateIRResult = null;
			
			if(iterator!=null){
				if(iterator.head!=null){
					((AST_Method) iterator.head).currentClassName = this.currentClassName;
					headCreateIRResult = ((AST_Method) iterator.head).createIR();
					if(iterator.tail!=null){
						iterator.tail.currentClassName = this.currentClassName;
						tailCreateIRResult =  iterator.tail.createIR();
					}
				}
			}
			
			return new IR_MethodList(headCreateIRResult, tailCreateIRResult);
			
		}
	}
}
