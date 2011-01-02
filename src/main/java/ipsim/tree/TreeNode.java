package ipsim.tree;

import ipsim.lang.Stringable;

public interface TreeNode<T> extends Stringable {
    Iterable<TreeNode<T>> getChildNodes();

    T getValue();
}