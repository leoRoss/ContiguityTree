package contiguityTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Ordered groups are sequential, and can sometimes be executed in the reverse order
//Unordered groups can be scrambled

public abstract class Group extends Task {
	protected Set <Task> subTasks;
	
	public Group (Label lab, Task par, int numSubTasks) {
		super(lab,par, numSubTasks);
		subTasks = new HashSet<Task>((int) Math.ceil(numSubTasks / 0.75)); //initialize so will not need be resized
	}
	
	public void encorporate (List<Task> demo) {
		//Let my subTasks try to encorporate themselves in the demo
		encorporateChildren(demo);
		//At this point, each of my subTasks is either in the demo or all of its pieces are in the demo
		Encorporator.encorporate(demo, this);
	}
	
	public abstract void encorporateChildren (List<Task> demo);
	
	public int absoluteSize(){
		int sum =0;
		for (Task subTask : subTasks) {
			sum += subTask.absoluteSize();
		}
		return sum;
	}
	
	//copy over subTasks from a collection into my subTasks
	//assign myself as each subTask's parent
	protected void copyCollection (Collection <Task> col){
		for (Task task : col){
			task.setParent((Task)this);
			addTask(task);
		}
	}
	
	/*
	//ASSUMPTION: Whenever I make a new group, if any subtask does not have a parent, they are a candidate for absorbtion (delete the node take its kids as my own)
	protected void copyCollection (Collection <Task> col){
		for (Task task : col){
			if (sameType(task) && task.getParent()==null) { //time to clean, that means take my delete the node task and steal his kids :)
				copyCollection( ((Group)task).getSubTasks() );
			}
			else {
				task.setParent((Task)this);
				addTask(task);
			}
		}
	}
	*/
	
	protected abstract void addTask (Task task);
	
	protected abstract boolean sameType (Task task);
	
	public boolean contains (Task task) {
		return subTasks.contains(task);
	}
	
	//PRINTING
	public void printMe (int depth) {
	    printSpace(depth);
	    System.out.print(name() + ": " + label + " {");
	    printPreconditions();
	    System.out.println();
	    for (Task subTask : getSubTasks()){
	    	subTask.printMe(depth+1);
		}
	    printSpace(depth);
	    System.out.println(")");
	}
	
	protected abstract String name ();
	public abstract boolean isOrdered ();
	public abstract boolean isReversible ();
	
	
	//GETTERS
	protected Collection<Task> getSubTasks (){
		return subTasks;
	}
}
