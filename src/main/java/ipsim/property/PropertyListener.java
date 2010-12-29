package ipsim.property;

public interface PropertyListener<T>
{
	void propertyChanged(Property<T> property,T oldValue,T newValue);
}