package ipsim.persistence;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import fpeas.function.Function;

public interface XMLDeserialiser
{
	<T> T readObject(SerialisationDelegate<T> delegate, Function<Object,T> caster);

	@Nullable
	String readAttribute(final Node node,final String name);

	String[] getObjectNames(final Node node);

	@Nullable
	<T> T readObject(final Node node,final String name,SerialisationDelegate<T> delegate,Function<Object,T> caster);

	String typeOfChild(final Node node, final String name);
}