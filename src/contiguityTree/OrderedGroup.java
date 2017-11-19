package contiguityTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


//Group of items that always appear contiguously, always in the same order (or in the exact opposite order)
public class OrderedGroup extends Group {
	private ArrayList<Task> orderedSubTasks;
	protected boolean reversible;
	
	//Various Constructors
	private OrderedGroup (Label label, Task parent, int size, boolean rev) {
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
    public void incorporateChildren (List<Task>demo) throws IncorporationError{
        for (Task subTask : orderedSubTasks) {
            subTask.incorporate(demo);
        }
    }
    
    public void createNewIncorporator(List<Task> demo) throws IncorporationError { incorporator = new OrderedIncorporator(this, demo);}
    
    
    //TASK TO TASK METHODS
    public boolean contentEquals (Task task) {
        //early abort
        if (task==null) return false;
        if (size!=task.getSize()) return false;
        if (absoluteSize()!=task.absoluteSize()) return false;
        if (!sameType(task)) return false;
        
        OrderedGroup og = (OrderedGroup)task;
   
        //no matter what if they are in the same order, they are content equals
        for (int i=0; i<size; i++){
            if (!getSubTask(i).contentEquals(og.getSubTask(i))) break;
            if (i==size-1) return true;
        }
        
        //if its reversible, it has another chance at being equal
        if (reversible){
            for (int i=0; i<size; i++){
                if (!getSubTask(i).contentEquals(og.getSubTask(size-1-i))) return false;
                if (i==size-1) return true;
            }
        }
        
        return false;
    }
    
    public Task fullCopy (){ 
        List <Task> subTaskCopies = new LinkedList <Task> ();
        for (Task subTask : orderedSubTasks) {
             subTaskCopies.add(subTask.fullCopy());
        }
        return new OrderedGroup(label.copyLabel(), null, subTaskCopies, reversible);
    }
    
    
    //INTERFACE WITH ORDERED SUBTASKS
    public int lenientIndexOfSubTask (Task task) { //return the index of the equal subTask even if task has a PieceLabel 
        int index=0;
        for (Task subTask : orderedSubTasks) {
            if (subTask.equals(task)) return index;
            index++;
        }
        return -1;
    }
    
    public Task getSubTask (int index) { return orderedSubTasks.get(index); }
    protected List<Task> getOrderedSubTasks (){return orderedSubTasks;}
    
    
    //TRAVERSAL METHODS
    public void getNextPossibleTasks(List<Primitive> list){
        if (completed.contains(orderedSubTasks.get(0))){
            for (int i=1; i<size; i++) {
               Task temp = orderedSubTasks.get(i);
               if (! completed.contains(temp)) {
                   temp.getNextPossibleTasks(list);
                   return;
               }
            }
        }
        else if (reversible && completed.contains(orderedSubTasks.get(size-1))){
            for (int i=size-1; i>=0; i--) {
                Task temp = orderedSubTasks.get(i);
                if (! completed.contains(temp)) {
                    temp.getNextPossibleTasks(list);
                    return;
                }
             }
        }
        else { //we can take either end (if reversible)
            orderedSubTasks.get(0).getNextPossibleTasks(list);
            if (reversible) {orderedSubTasks.get(size-1).getNextPossibleTasks(list);}
        }
    }
    
    //GENERAL GROUP METHODS - DOCUMENTED IN GROUP CLASS
	protected boolean sameType (Task task) {
		if (task instanceof OrderedGroup){
			if ( ((OrderedGroup)task).isReversible() == reversible ) return true;
		}
		return false;
	}
	
	public boolean isOrdered () {return true;}
	public boolean isReversible () {return reversible;}
    protected Collection<Task> getSubTasksForEfficientTraversal (){ return getOrderedSubTasks();}
    protected String name () {
        if (reversible) return "Reversible";
        return "Sequential";
    }
}
