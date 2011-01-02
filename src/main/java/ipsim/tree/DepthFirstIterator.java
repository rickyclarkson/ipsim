package ipsim.tree;

import ipsim.util.Collections;
import ipsim.util.Stack;
import ipsim.util.StackUtility;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The root element is processed first.
 */
final class DepthFirstIterator<T> implements Iterator<T> {
    private final Stack<TreeNode<T>> stack = Collections.stack();

    DepthFirstIterator(final Iterable<TreeNode<T>> root) {
        StackUtility.pushAll(stack, root);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public T next() {
        if (stack.isEmpty())
            throw new NoSuchElementException();

        final TreeNode<T> node = stack.pop();

        for (final TreeNode<T> node2 : node.getChildNodes())
            stack.push(node2);

        return node.getValue();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}