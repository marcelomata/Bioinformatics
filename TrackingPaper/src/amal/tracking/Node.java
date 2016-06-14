package amal.tracking;

import java.util.ArrayList;

public class Node<T> {

    private ArrayList<Node<T>> children = new ArrayList<Node<T>>();
    private Node<T> parent = null;
    private T data = null;

    public Node(T data) {
        this.data = data;
    }

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public ArrayList<Node<T>> getChildren() {
        return children;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void addChild(T data) {

        Node<T> child = new Node<T>(data);
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public void removeParent() {
        this.parent = null;
    }

    public Node<T> getLastDescendant() {
        Node<T> tmp = this;
        while ((tmp.getChildren() != null) && (tmp.getChildren().size() == 1)) {
            tmp = tmp.getChildren().get(0);
        }
        return tmp;
    }

    public ArrayList<Node<T>> getNodesToDescendant() {
        ArrayList<Node<T>> lin = new ArrayList<Node<T>>();
        Node<T> tmp = this;
        lin.add(tmp);
        while ((tmp.getChildren() != null) && (tmp.getChildren().size() == 1)) {
            tmp = tmp.getChildren().get(0);
            lin.add(tmp);
        }

        return lin;
    }

    public boolean hasBrothers() {
        Node<T> par = getParent();
        if (par == null) {
            return false;
        }
        return par.getChildren().size() > 1;
    }

    // last direct ancestor, no split
    public Node<T> getAncestor() {
        Node<T> par = this;
        while (!par.hasBrothers()) {
            if (par.getParent() == null) {
                return par;
            } else {
                par = par.getParent();
            }
        }
        return par;
    }

    @Override
    public String toString() {
        return "Node(" + data.toString() + ")";
    }

}
