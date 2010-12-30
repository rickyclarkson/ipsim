package ipsim.persistence;

import fj.F;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

public interface XMLDeserialiser
{
	<T> T readObject(SerialisationDelegate<T> delegate, F<Object,T> caster);

	@Nullable
	String readAttribute(final Node node,final String name);

	String[] getObjectNames(final Node node);

	@Nullable
	<T> T readObject(final Node node,final String name,SerialisationDelegate<T> delegate,F<Object,T> caster);

	String typeOfChild(final Node node, final String name);
}