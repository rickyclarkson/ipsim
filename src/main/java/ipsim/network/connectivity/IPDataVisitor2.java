package ipsim.network.connectivity;

public interface IPDataVisitor2<T>
{
	T visitRequest();

	T visitReply();

	T visitNetUnreachable();

	T visitHostUnreachable();

	T visitTimeToLiveExceeded();
}
