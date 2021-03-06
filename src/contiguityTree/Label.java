package contiguityTree;

/**
 * Labels are used to track Nodes
 *
 * There are two types of Labels:
 *      Label: A PQ-Tree only contains these types of labels. They have a Integer identifier
 *      PieceLabel: These only exist during the Incorporation Phase.
 *          This phase occurs when a InnerNode N in an existing PQ-Tree is attempting to copy its information/structure into a new permutation
 *          If InnerNode N does not find all of its elements in one contiguous node, its elements are broken into smaller groups, each of which is assigned a pieceLabel.
 *
 * A Label will be equal (and will hash the same) as any other Label or Piecelabel with the same id
 * StrictEquals ensures two labels are the same type and have the same attributes (id and optionally brotherhood & uuid)
 * PieceLabels are only ever hashed with other PieceLabels, same for normal Labels
 * Thus hash is simply the id (perfect distribution)
 */
public class Label {
    private static int next_available_id = 0;
    int id;

    public Label() {
        id = next_available_id++;
    }

    public Label(int i) {
        id = i;
    }

    //Labels are only ever hashed against other Labels, not including PieceLabels. Thus we have a perfect hash distribution
    public int hashCode() {
        return id;
    }

    //Labels are equal as long as they have the same id
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Label)) return false;
        Label label = (Label) obj;
        return id == label.id;
    }

    //Labels are strictequal as long as they have the same id and are both full labels, not PieceLabels
    public boolean strictEquals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Label)) return false;
        if ((obj instanceof PieceLabel)) return false;
        Label label = (Label) obj;
        return id == label.id;
    }


    public Label copyLabel() {
        return new Label(id);
    }

    public boolean isPiece() {
        return false;
    }

    public String toString() {
        return "" + id;
    }

    int getId() {
        return id;
    }

    public int getBrotherhoodSize() {
        return 1;
    }
}
