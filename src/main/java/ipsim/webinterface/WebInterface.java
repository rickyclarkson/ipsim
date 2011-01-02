package ipsim.webinterface;

import fj.Effect;
import fj.data.Option;
import ipsim.Global;
import ipsim.gui.UserMessages;
import ipsim.lang.CheckedIllegalStateException;
import java.io.IOException;
import javax.swing.JOptionPane;

import static ipsim.NetworkContext.errors;
import static ipsim.lang.Assertion.assertNotNull;
import static ipsim.webinterface.Web.webInteraction;
import static ipsim.webinterface.WebInterfaceUtility.getLogAndSaveValues;
import static ipsim.webinterface.WebInterfaceUtility.matchesExceptionResponse;

/**
 * SU is normal problems, TS is troubleshoot.
 */
public class WebInterface {
    public static void putException(final String exceptiontext, final String currentconfig) {
        try {
            final int sequence = 1;
            final String output = webInteraction("PUT", "200", "", "exception/log" + sequence, exceptiontext) + webInteraction("PUT", "200", "", "exception/save" + sequence, currentconfig);

            if (!matchesExceptionResponse(output)) {
                handleError();
                return;
            }

            final String[] values = getLogAndSaveValues(output);

            UserMessages.message("Tell your tutor to look at exception " + values[0] + " and log " + values[1] + '.');
        } catch (final IOException exception) {
            handleError();
        }
    }

    private static void handleError() {
        JOptionPane.showMessageDialog(Global.global.get().frame, "Problem accessing network", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static Option<String> getProblem(final String testNumber) {
        try {
            final String returnCode = webInteraction("TGET", "200", "", testNumber, "");

            if (returnCode.startsWith("3")) {
                JOptionPane.showMessageDialog(Global.global.get().frame, "The test cannot be done at this time", "Error", JOptionPane.ERROR_MESSAGE);

                return Option.none();
            }

            if (!returnCode.startsWith("101")) {
                JOptionPane.showMessageDialog(Global.global.get().frame, "Problem Accessing Network", "Error", JOptionPane.ERROR_MESSAGE);

                return Option.none();
            }

            return Option.some(returnCode);
        } catch (final IOException exception) {
            JOptionPane.showMessageDialog(Global.global.get().frame, "Problem Accessing Network", "Error", JOptionPane.ERROR_MESSAGE);

            return Option.none();
        }
    }

    /**
     * @return a serialised network.
     * @throws CheckedIllegalStateException if something went wrong, but the user will have been notified.
     * @throws NoSuchConfigurationException
     */
    public static NamedConfiguration getNamedConfiguration(final Effect<String> errors, final String configurationName) throws CheckedIllegalStateException, NoSuchConfigurationException {
        assertNotNull(configurationName);

        try {
            final String input = webInteraction("CGET", "200", "", configurationName, "");

            if (input.startsWith("101: OK\n"))
                return new NamedConfiguration(configurationName, input.substring("101: OK\n".length()));

            if (input.startsWith("101: OK \""))
                return new NamedConfiguration(input.substring("101: OK \"".length(), input.indexOf((int) '"', "101: OK \"".length() + 1)), input.substring(input.indexOf((int) '\n') + 1));

            if (input.startsWith("407")) {
                errors.e("Cannot download configuration " + configurationName);
                throw new NoSuchConfigurationException(input);
            }

            throw new CheckedIllegalStateException(input);
        } catch (final IOException exception) {
            errors.e("Problem Accessing Network");

            throw new CheckedIllegalStateException(exception);
        }
    }

    /**
     * TODO remove 456, replace with test number from user.
     */
    public static String putSUProblem(final String user, final String suProblem) throws IOException {
        return webInteraction("TPUT", "200", "456", "su/problems/" + user, suProblem);
    }

    public static String putSUSolution(final String testNumber, final String email, final String suSolution) throws IOException {
        final String returnCode = webInteraction("TPUT", "200", testNumber, "su/solutions/" + email, suSolution);

        if (returnCode.startsWith("3")) {
            errors("The test cannot be done at this time");

            throw new IOException(returnCode);
        }

        return returnCode;
    }

    public static String putNamedConfiguration(final String name, final String string) throws IOException {
        return webInteraction("PUT", "200", "", "saved/" + name, string);
    }
}