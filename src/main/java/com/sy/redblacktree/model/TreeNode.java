package com.sy.redblacktree.model;

import com.sy.redblacktree.constant.RedBlackTreeConst;



public class TreeNode<T> {

    public TreeNode(T value) {
        this.value = value;
        this.color = RedBlackTreeConst.Color.RED;
    }

    /**
     * 权值
     */
    private T value;

    /**
     * 颜色, 0.红色 1.黑色
     */
    private Integer color;

    /**
     * 左子节点
     */
    private TreeNode leftChildren;

    /**
     * 右子节点
     */
    private TreeNode rightChildren;

    /**
     * 父亲节点
     */
    private TreeNode fartherNode;


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public TreeNode getLeftChildren() {
        return leftChildren;
    }

    public void setLeftChildren(TreeNode leftChildren) {
        this.leftChildren = leftChildren;
    }

    public TreeNode getRightChildren() {
        return rightChildren;
    }

    public void setRightChildren(TreeNode rightChildren) {
        this.rightChildren = rightChildren;
    }

    public TreeNode getFartherNode() {
        return fartherNode;
    }

    public void setFartherNode(TreeNode fartherNode) {
        this.fartherNode = fartherNode;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "value=" + value +
                ", color=" + color +
                ", leftChildren=" + (leftChildren == null ? "null" : leftChildren.getValue()) +
                ", rightChildren=" + (rightChildren == null ? "null" : rightChildren.getValue()) +
                ", fartherNode=" + (fartherNode == null ? "null" : fartherNode.getValue()) +
                '}';
    }


}
