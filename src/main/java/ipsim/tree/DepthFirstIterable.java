package ipsim.tree;

import java.util.Iterator;

final class DepthFirstIterable<T> implements Iterable<T> {
    private final Iterable<TreeNode<T>> roots;

    DepthFirstIterable(final Iterable<TreeNode<T>> roots) {
        this.roots = roots;
    }

    @Override
    public Iterator<T> iterator() {
        return new DepthFirstIterator<T>(roots);
    }
}