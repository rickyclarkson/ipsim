package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.persistence2.Deserialiser;
import ipsim.persistence2.Serialiser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public final class SerialisationTest
{
	private static boolean tests(final Test test)
	{
		final StringWriter writer=new StringWriter();
		test.write(new Serialiser(new PrintWriter(writer)));

		return test.read(new Deserialiser(new StringReader(writer.getBuffer().toString())));
	}

	private static interface Test
	{
		void write(Serialiser serialiser);

		boolean read(Deserialiser deserialiser);
	}

	private static final Test stringTest=new Test()
	{
		@Override
        public void write(final Serialiser serialiser)
		{
			serialiser.write("Hello");
		}

		@Override
        public boolean read(final Deserialiser deserialiser)
		{
			return "Hello".equals(deserialiser.readString());
		}
	};

	private static final Test testStringArray=new Test()
	{
		@Override
        public void write(final Serialiser serialiser)
		{
			serialiser.enter();
			serialiser.write("o\\ne");
			serialiser.write("two");
			serialiser.exit();
		}

		@Override
        public boolean read(final Deserialiser deserialiser)
		{
			deserialiser.enter();
			deserialiser.readString();
			return "two".equals(deserialiser.readString());
		}
	};

	private static final Test testHierarchy=new Test()
	{
		@Override
        public void write(final Serialiser serialiser)
		{
			serialiser.write("network");
			serialiser.enter();
			serialiser.write("computer");
			serialiser.write(":id");
			serialiser.write("5");
			serialiser.enter();
			serialiser.write("card");
			serialiser.write(":ip-address");
			serialiser.write("146.87.1.1");
			serialiser.exit();
			serialiser.exit();
		}

		@Override
        public boolean read(final Deserialiser deserialiser)
		{
			deserialiser.readString();
			deserialiser.enter();
			deserialiser.readString();
			deserialiser.readString();
			deserialiser.readString();
			deserialiser.enter();
			deserialiser.readString();
			deserialiser.readString();
			return "146.87.1.1".equals(deserialiser.readString());
		}
	};

	public static final UnitTest testSerialisingStrings=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final List<Test> tests=new ArrayList<Test>();
			tests.add(stringTest);
			tests.add(testStringArray);
			tests.add(testHierarchy);

			for (final Test test : tests)
				if (!tests(test))
					return false;

			return true;
		}

		public String toString()
		{
			return "SerialisationTest";
		}
	};
}