package IR;

public abstract class IR_StatementCondition extends IR_Statement{
	
	public IR_EXP cond;
	public IR_Statement body;
	public IR_AsmLabel startLabel;
	public IR_AsmLabel endLabel;
	
	public IR_StatementCondition(IR_EXP cond, IR_Statement body, String labelName){
		this.cond = cond;
		this.body = body;
		this.startLabel = new IR_AsmLabel(labelName + "_start");
		this.endLabel = new IR_AsmLabel(labelName + "_end");
	}
}
