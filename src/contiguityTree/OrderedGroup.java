package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


//Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
public class OrderedGroup extends Group {
	private ArrayList<Task> orderedSubTasks;
	protected boolean reversible;
	
	//Various Constructors
	public OrderedGroup (Label label, Task parent, int size, boolean rev) {
		super(label, parent, size);
		orderedSubTasks = new ArrayList<Task>(size);
        reversible = rev;
	}
	
	public OrderedGroup (Label label, Task parent,  List <Task> subTasksToAdd, boolean rev) {
		this(label, parent, subTasksToAdd.size(), rev);
		addListToSubTasks(subTasksToAdd);
	}
    
	
	//This constructor will absorb any subTasks which share my direction
	public OrderedGroup (Label label, Task parent,  List <Task> subTasksToAdd, boolean rev, List<Integer> subTaskDirections, int myDirection) {
        this(label, parent, subTasksToAdd.size(), rev);
        addListToSubTasks(subTasksToAdd, subTaskDirections, myDirection);
    }
	
	private void addListToSubTasks(List <Task> entireListToAdd, List<Integer> subTaskDirections, int myDirection) {
	    Iterator<Task> taskIt = entireListToAdd.iterator();
	    Iterator<Integer> dirIt = subTaskDirections.iterator();
	    while(taskIt.hasNext()) {
	        Task subTask = taskIt.next();
	        int subTaskDir = dirIt.next(); //we want this to fail if directions run out before subtask do
	        if (subTaskDir==myDirection && subTask instanceof OrderedGroup ) { //time to absorb, that means remove all ties to the subtask and steal his kids :)
	            addListToSubTasks( ((OrderedGroup)subTask).getOrderedSubTasks() );
            }
	        else {
                addTaskSafe(subTask);
            }
	    }
	}
	
	private void addListToSubTasks(List <Task> entireListToAdd) {
	    for (Task subTask : entireListToAdd)  addTaskSafe(subTask);
    }
	
	protected void addTask (Task task) {
        if (task.isPiece()) throw new Error ("Please only add Tasks using addTaskSafe()!");
        subTasks.add(task);
        orderedSubTasks.add(task);
    }
	
	
	//INCORPORATION METHODS
    public void encorporateChildren (List<Task>demo){
        for (Task subTask : orderedSubTasks) {
            subTask.incorporate(demo);
        }
    }
    
    public void createNewIncorporator(List<Task> demo) {
        incorporator = new OrderedIncorporator(this, demo);
    }
    

    
	
	protected boolean sameType (Task task) {
		if (task instanceof OrderedGroup){
			if ( ((OrderedGroup)task).isReversible() == reversible ) return true;
		}
		return false;
	}
	
	protected String name () {
		if (reversible) return "Reversible";
		return "Sequential";
	}
	
	public boolean isOrdered () {return true;}
	public boolean isReversible () {return reversible;}
	
	protected List<Task> getOrderedSubTasks (){return orderedSubTasks;}
	
	public Task getSubTask (int index) {
		return orderedSubTasks.get(index); 
	}
	
	//lenientIndex will return the index of the subTask even if a Piece is passed in
    public int lenientIndexOfSubTask (Task task) {
        int index=0;
        for (Task subTask : orderedSubTasks) {
            if (subTask.equals(task)) return index;
            index++;
        }
        return -1;
    }
    
    protected Collection<Task> getPrintSubTasks (){
        return getOrderedSubTasks();
    }
	
}
