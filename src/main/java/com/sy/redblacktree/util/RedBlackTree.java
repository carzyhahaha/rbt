package com.sy.redblacktree.util;

import com.sy.redblacktree.constant.RedBlackTreeConst;
import com.sy.redblacktree.exception.UnSupportException;
import com.sy.redblacktree.model.TreeNode;

import java.util.*;

public class RedBlackTree<T> {

    // 或许这并不是一颗红黑树...
    private static final int UNMATCH = -1;

    // 如果是根节点, 直接插红
    private static final int RULE0 = 0;

    // 如果父节点为黑色, 无论在那一支添加节点都不会违反红黑树的五个性质
    private static final int RULE1 = 1;

    // 如果父亲节点为红色, 且为左支, 叔叔节点为黑色, 且插入点为左支时, 祖父与父亲换色, 右旋
    private static final int RULE2 = 2;

    // 如果父亲节点为红色, 且为右支, 叔叔节点为黑色, 且插入点为右支时 与RULE2为镜像, 祖父与父亲换色, 左旋
    private static final int RULE3 = 3;

    // 如果父亲节点为红色, 且为左支, 叔叔节点为黑色, 且插入点为右支时, 应该先以父节点左旋
    // 把原先的父节点看做是新的要插入的节点，把原先要插入的节点看做是新的父节点，那就变成了RULE2的情况
    private static final int RULE4 = 4;

    // 如果父亲节点为红色, 且为右支, 叔叔节点为黑色, 且插入点为左支时, 应该先以父节点右旋
    // 把原先的父节点看做是新的要插入的节点，把原先要插入的节点看做是新的父节点，那就变成了RULE3的情况
    private static final int RULE5 = 5;

    // 如果父亲节点为红色, 叔叔节点为红色, 吧祖父节点和父. 叔叔节点交换颜色,
    // 再把祖父节点看作插入的点, 调整树重新符合红黑树性质
    private static final int RULE6 = 6;

    public TreeNode<T> getRoot() {
        return root;
    }

    private TreeNode<T> root;

    /** 比较规则 **/
    private Comparator<T> comparator;

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }


    public Boolean add(TreeNode children) {

        if (root == null) {
            root = children;
            children.setColor(RedBlackTreeConst.Color.BLACK);
            return true;
        } else {
            try {
                dfsTreeAndGetFartherNode(root, children);
            } catch (UnSupportException e) {
                e.printStackTrace();
            }
            Integer rule = matchRule(children.getFartherNode(), children);
            TreeNode fartherNode = children.getFartherNode();
            TreeNode grandFartherNode = fartherNode.getFartherNode();
            if (Objects.equals(rule, RULE0)) {
            } else if (Objects.equals(rule, RULE1)) {
            } else if (Objects.equals(rule, RULE2)) {
                changeColor(fartherNode, grandFartherNode);
                rightRotate(grandFartherNode);
                if (Objects.equals(grandFartherNode, root)) {
                    root = fartherNode;
                }

                fartherNode.setColor(RedBlackTreeConst.Color.RED);
                fartherNode.getLeftChildren().setColor(RedBlackTreeConst.Color.BLACK);
                fartherNode.getRightChildren().setColor(RedBlackTreeConst.Color.BLACK);
                doWhenRule6RecurveBalance(fartherNode);
            } else if (Objects.equals(rule, RULE3)) {
                changeColor(fartherNode, grandFartherNode);
                leftRotate(grandFartherNode);
                if (Objects.equals(grandFartherNode, root)) {
                    root = fartherNode;
                }
            } else if (Objects.equals(rule, RULE4)) {
                leftRotate(fartherNode);
                changeColor(grandFartherNode, children);
                rightRotate(grandFartherNode);
                if (Objects.equals(grandFartherNode, root)) {
                    root = children;
                }
            } else if (Objects.equals(rule, RULE5)) {
                rightRotate(fartherNode);
                changeColor(grandFartherNode, children);
                leftRotate(grandFartherNode);
                if (Objects.equals(grandFartherNode, root)) {
                    root = children;
                }
            } else if (Objects.equals(rule, RULE6)) {
                TreeNode uncleNode = Objects.equals(fartherNode, grandFartherNode.getLeftChildren())
                        ? grandFartherNode.getRightChildren() : grandFartherNode.getLeftChildren();
                grandFartherNode.setColor(RedBlackTreeConst.Color.RED);
                fartherNode.setColor(RedBlackTreeConst.Color.BLACK);
                uncleNode.setColor(RedBlackTreeConst.Color.BLACK);
                doWhenRule6RecurveBalance(grandFartherNode);
            }
            return true;
        }



    }

    /**
     * 匹配调整规则
     * @param fartherNode
     * @param children
     * @return
     */
    private int matchRule(TreeNode fartherNode, TreeNode children) {

        TreeNode grandFartherNode = fartherNode.getFartherNode();

        if (grandFartherNode == null) {
            return RULE0;
        }

        // 儿子属于左支或右支
        Integer childrenBelongLR = Objects.equals(fartherNode.getLeftChildren(), children) ? 0 : 1;

        Integer fartherBelongLR = Objects.equals(grandFartherNode.getLeftChildren(), fartherNode) ? 0 : 1;

        TreeNode uncleNode  =  fartherBelongLR == 0 ? grandFartherNode.getRightChildren() : grandFartherNode.getLeftChildren();



        if (Objects.equals(fartherNode.getColor(), RedBlackTreeConst.Color.BLACK)) {
            return RULE1;
        }else if (Objects.equals(fartherNode.getColor(), RedBlackTreeConst.Color.RED)
                && fartherBelongLR == 0
                && childrenBelongLR == 0
                && (uncleNode == null || Objects.equals(uncleNode.getColor(), RedBlackTreeConst.Color.BLACK))) {
            return RULE2;
        }else if (Objects.equals(fartherNode.getColor(), RedBlackTreeConst.Color.RED)
                && fartherBelongLR == 1
                && childrenBelongLR == 1
                && (uncleNode == null || Objects.equals(uncleNode.getColor(), RedBlackTreeConst.Color.BLACK))) {
            return RULE3;
        }else if (Objects.equals(fartherNode.getColor(), RedBlackTreeConst.Color.RED)
                && fartherBelongLR == 0
                && childrenBelongLR == 1
                && (uncleNode == null || Objects.equals(uncleNode.getColor(), RedBlackTreeConst.Color.RED))) {
            return RULE4;
        }else if (Objects.equals(fartherNode.getColor(), RedBlackTreeConst.Color.RED)
                && fartherBelongLR == 1
                && childrenBelongLR == 0
                && (uncleNode == null || Objects.equals(uncleNode.getColor(), RedBlackTreeConst.Color.RED))) {
            return RULE5;
        } else if (Objects.equals(fartherNode.getColor(), RedBlackTreeConst.Color.RED)
                && (uncleNode != null && Objects.equals(uncleNode.getColor(), RedBlackTreeConst.Color.RED))) {
            return RULE6;
        }
        return UNMATCH;
    }

    private void doWhenRule6RecurveBalance(TreeNode affectNode) {

        TreeNode affectNodeFarther = affectNode.getFartherNode();
        if (affectNodeFarther == null) {
            affectNode.setColor(RedBlackTreeConst.Color.BLACK);
        }

        if (affectNodeFarther != null && !Objects.equals(affectNodeFarther.getColor(), RedBlackTreeConst.Color.BLACK)) {
            TreeNode affectNodeGrand = affectNodeFarther.getFartherNode();
            changeColor(affectNodeGrand, affectNodeFarther);
            Integer affectNodeBelongLR = Objects.equals(affectNodeFarther.getLeftChildren(), affectNode) ? 0 : 1;

            if (affectNodeBelongLR == 0) {
                rightRotate(affectNodeGrand);
            } else {
                leftRotate(affectNodeGrand);
            }

            if (Objects.equals(affectNodeGrand, root)) {
                root = affectNodeFarther;
            }

            affectNodeFarther.setColor(RedBlackTreeConst.Color.RED);
            affectNodeFarther.getLeftChildren().setColor(RedBlackTreeConst.Color.BLACK);
            affectNodeFarther.getRightChildren().setColor(RedBlackTreeConst.Color.BLACK);
            doWhenRule6RecurveBalance(affectNodeFarther);
        }
    }


    // 换色
    private void changeColor(TreeNode n1, TreeNode n2) {
        Integer n1Color = n1.getColor();
        Integer n2Color = n2.getColor();

        n1.setColor(n2Color);
        n2.setColor(n1Color);
    }

    // 左旋
    private static void leftRotate(TreeNode fartherNode) {

        // 祖父节点
        TreeNode grandFartherNode = fartherNode.getFartherNode();

        TreeNode rightChildrenNode = fartherNode.getRightChildren();

        // 右子树的左节点
        TreeNode rightChilLeftOff = rightChildrenNode.getLeftChildren();

        if (grandFartherNode != null) {

            rightChildrenNode.setFartherNode(grandFartherNode);

            if (Objects.equals(grandFartherNode.getLeftChildren(), fartherNode)) {
                grandFartherNode.setLeftChildren(rightChildrenNode);
            } else {
                grandFartherNode.setRightChildren(rightChildrenNode);
            }

        } else {
            rightChildrenNode.setFartherNode(null);
        }

        fartherNode.setRightChildren(rightChilLeftOff);
        fartherNode.setFartherNode(rightChildrenNode);
        rightChildrenNode.setLeftChildren(fartherNode);

    }

    // 右旋
    private static void rightRotate(TreeNode fartherNode) {

        // 祖父节点
        TreeNode grandFartherNode = fartherNode.getFartherNode();

        TreeNode leftChildrenNode = fartherNode.getLeftChildren();

        // 左子树的右节点
        TreeNode leftChilRightOff = leftChildrenNode.getRightChildren();

        if (grandFartherNode != null) {

            leftChildrenNode.setFartherNode(grandFartherNode);

            if (Objects.equals(grandFartherNode.getLeftChildren(), fartherNode)) {
                grandFartherNode.setLeftChildren(leftChildrenNode);
            } else {
                grandFartherNode.setRightChildren(leftChildrenNode);
            }

        } else {
            leftChildrenNode.setFartherNode(null);
        }

        fartherNode.setLeftChildren(leftChilRightOff);
        fartherNode.setFartherNode(leftChildrenNode);
        if (leftChilRightOff != null)
            leftChilRightOff.setFartherNode(fartherNode);
        leftChildrenNode.setRightChildren(fartherNode);
    }


    private void dfsTreeAndGetFartherNode(TreeNode<T> checkNode, TreeNode<T> children) throws UnSupportException {

        if (comparator.compare(checkNode.getValue(), children.getValue()) == -1) {
            // 找到合适点, 插入
            if (checkNode.getLeftChildren() == null) {
                checkNode.setLeftChildren(children);
                children.setFartherNode(checkNode);
            }
            dfsTreeAndGetFartherNode(checkNode.getLeftChildren(), children);
        } else if (comparator.compare(checkNode.getValue(), children.getValue()) == 1) {
            if (checkNode.getRightChildren() == null) {
                checkNode.setRightChildren(children);
                children.setFartherNode(checkNode);
            }
            dfsTreeAndGetFartherNode(checkNode.getRightChildren(), children);
        }

    }


    // 删除节点
    private void del(TreeNode<T> delNode) {
        TreeNode<T> successor = null;
        TreeNode<T> farther = null;
        TreeNode<T> successorChild = null;
        Integer color = null;

        if (delNode.getLeftChildren() != null && delNode.getRightChildren() != null) {

            farther = delNode.getFartherNode();
            // 获取后继节点 (右子树的最左子)
            successor = delNode.getRightChildren();

            while (successor.getLeftChildren() != null) {
                successor = successor.getLeftChildren();
            }

            System.out.println("successor = " + successor);

            if (farther != null) {
                if (Objects.equals(delNode, farther.getLeftChildren())) {
                    farther.setLeftChildren(successor);
                } else {
                    farther.setLeftChildren(successor);
                }
            } else {
                root = successor;
            }


            successorChild = successor.getRightChildren();
            farther = successor.getFartherNode();
            color = successor.getColor();

            if (farther == delNode) {
                farther = successor;
            } else {
                if (successorChild != null) {
                    successorChild.setFartherNode(farther);
                }
                farther.setLeftChildren(successorChild);
                successor.setRightChildren(delNode.getRightChildren());
                delNode.getRightChildren().setFartherNode(successor);
            }
            successor.setFartherNode(delNode.getFartherNode());
            successor.setColor(delNode.getColor());
            successor.setLeftChildren(delNode.getLeftChildren());
            delNode.getLeftChildren().setFartherNode(successor);

            //方便下边统一处理 无关算法
            successor = successorChild;
        } else {
            System.out.println(delNode);
            if (delNode.getLeftChildren() != null) {
                successor = delNode.getLeftChildren();
            } else {
                successor = delNode.getRightChildren();
            }

            farther = delNode.getFartherNode();
            System.out.println("farther = " + farther);
            color = delNode.getColor();

            if (successor != null) {
                successor.setFartherNode(farther);
            }

            if (farther != null) {
                if (Objects.equals(farther.getLeftChildren(), delNode)) {
                    farther.setLeftChildren(successor);
                } else {
                    farther.setRightChildren(successor);
                }
            } else {
                root = successor;
            }
        }

        if (color == RedBlackTreeConst.Color.BLACK) {
            balanceAfterDel(successor, farther);
        }

        delNode = null;
        return;
    }

    /**
     * 删除后的平衡
     */
    private void balanceAfterDel(TreeNode<T> node, TreeNode<T> farther) {

        TreeNode<T> borther = null;
        while ((node == null || isBlack(node)) && (node != root)) {
            if (Objects.equals(farther.getLeftChildren(), node)) {
                borther = farther.getRightChildren();

                if (isRed(borther)) {
                    // 兄弟节点是红色
                    setBlack(borther);
                    setRed(farther);
                    leftRotate(farther);
                    borther = farther.getRightChildren();
                }

                if (isBlack(borther) && borther.getLeftChildren() == null || isBlack(borther.getLeftChildren()) &&
                        (borther.getRightChildren() == null || isBlack(borther.getRightChildren()))) {
                    // 兄弟节点是黑色, 并且其两个儿子都是黑色(或nil)
                    setRed(borther);
                    node = farther;
                    farther = node.getFartherNode();
                } else {
                    if (borther.getRightChildren() == null || isBlack(borther.getRightChildren())) {
                        // 兄弟节点是黑色, 并且其左儿子是红色, 右儿子是黑色
                        setBlack(borther.getLeftChildren());
                        setRed(borther);
                        rightRotate(borther);
                        borther = farther.getLeftChildren();
                    }
                    // 此时兄弟节点是黑色, 其右儿子为红色, 左儿子任意颜色
                    borther.setColor(farther.getColor());
                    setBlack(farther);
                    setBlack(borther.getLeftChildren());
                    leftRotate(farther);
                    node = root;
                    break;
                }
            } else {

               borther = farther.getLeftChildren();
               if (isRed(borther)) {
                   setBlack(borther);
                   setRed(farther);
                   rightRotate(farther);
                   borther = farther.getLeftChildren();
               }

               if ((borther.getLeftChildren() == null || isBlack(borther.getLeftChildren()))
                       && (borther.getRightChildren() == null || isBlack(borther.getRightChildren()))) {
                       setRed(borther);
                       node = farther;
                       farther = node.getFartherNode();
               } else {
                   if (borther.getLeftChildren() == null || isBlack(borther.getLeftChildren())) {
                       setBlack(borther.getRightChildren());
                       setRed(borther);
                       leftRotate(borther);
                       borther = farther.getLeftChildren();
                   }

                   borther.setColor(farther.getColor());
                   setBlack(farther);
                   setBlack(borther.getLeftChildren());
                   rightRotate(farther);
                   node = root;
                   break;
               }

            }
        }

        if (node != null) {
            setBlack(node);
        }
    }

    private boolean isBlack(TreeNode<T> node) {
        return Objects.equals(RedBlackTreeConst.Color.BLACK, node.getColor());
    }

    private boolean isRed(TreeNode<T> node) {
        return Objects.equals(RedBlackTreeConst.Color.RED, node.getColor());
    }

    private void setBlack(TreeNode<T> node) {
        node.setColor(RedBlackTreeConst.Color.BLACK);
    }

    private void setRed(TreeNode<T> node) {
        node.setColor(RedBlackTreeConst.Color.RED);
    }

    static class BfsNode<T> {

        BfsNode(TreeNode<T> node, Integer floor) {
            this.node = node;
            this.floor = floor;
        }
        TreeNode<T> node;
        Integer floor;
    }

    public Integer showTree() {

        TreeNode<T> root = this.root;

        ArrayDeque<BfsNode<T>> que = new ArrayDeque<>();

        que.add(new BfsNode<>(root, 1));

        Integer index = 1;
        Integer sum = 0;
        while (!que.isEmpty()) {
           BfsNode<T> bfsNode = que.pop();
           TreeNode<T> thisNode = bfsNode.node;
            Integer floor = bfsNode.floor;
            if (thisNode.getLeftChildren() != null){
                que.add(new BfsNode<>(thisNode.getLeftChildren(), floor+1));
            }
            if (thisNode.getRightChildren() != null) {
            que.add(new BfsNode<>(thisNode.getRightChildren(), floor+1));
            }
            if (index != floor) {
                System.out.println();
                index = floor;
            }
            System.out.printf("%15s","["+thisNode.getValue()+","+ (thisNode.getFartherNode() == null  ? "0" : thisNode.getFartherNode().getValue())
                    +","+(thisNode.getColor() == 0 ? "红" : "黑")+"]");

            sum ++;
        }
        System.out.println();
        System.out.println();
        return index;
    }

    public TreeNode<T> find(T t) {
        return dfsFind(root, t);
    }

    private TreeNode<T> dfsFind(TreeNode<T> node, T t) {
        if (node == null) {
            return null;
        }
        Integer compareRes = comparator.compare(node.getValue(), t);

        if (compareRes == -1) {
            return dfsFind(node.getLeftChildren(), t);
        } else if (compareRes == 1){
            return dfsFind(node.getRightChildren(), t);
        }

        return node;
    }

    public static void main(String[] args) throws UnSupportException {

        RedBlackTree<Integer> redBlackTree = new RedBlackTree<>();
        redBlackTree.setComparator((Integer e1, Integer e2)->{
            if (e1 < e2) {
                return 1;
            } else if (e1 > e2){
                return -1;
            }
            return 0;
        });
        redBlackTree.add(new TreeNode(10));
        redBlackTree.add(new TreeNode(9));
        redBlackTree.add(new TreeNode(8));
        redBlackTree.add(new TreeNode(7));
        redBlackTree.add(new TreeNode(6));
        redBlackTree.add(new TreeNode(5));
        redBlackTree.add(new TreeNode(4));
        redBlackTree.add(new TreeNode(3));
        redBlackTree.add(new TreeNode(2));
        redBlackTree.showTree();
        redBlackTree.del(redBlackTree.find(8));
        redBlackTree.showTree();
    }

}
