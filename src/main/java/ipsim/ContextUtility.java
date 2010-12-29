package ipsim;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextUtility
{

	static Logger createLogger()
	{
		final Logger logger=Logger.getAnonymousLogger();
		logger.setLevel(Level.FINE);

		logger.severe("Started logger");
		return logger;
	}
}