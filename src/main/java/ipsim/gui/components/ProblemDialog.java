package ipsim.gui.components;

import static anylayout.AnyLayout.useAnyLayout;
import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import fpeas.either.Either;
import fpeas.either.EitherUtility;
import fpeas.function.Function;
import fpeas.function.FunctionUtility;
import fpeas.maybe.MaybeUtility;
import static ipsim.Global.getNetworkContext;
import static ipsim.Global.global;
import ipsim.NetworkContext;
import ipsim.Caster;
import static ipsim.NetworkContext.errors;
import ipsim.awt.ComponentUtility;
import ipsim.lang.Runnables;
import static ipsim.lang.Runnables.throwRuntimeException;
import ipsim.network.Problem;
import static ipsim.network.Problem.MIN_SUBNETS;
import ipsim.network.ProblemBuilder;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import static ipsim.network.ethernet.NetMaskUtility.getNetMask;
import ipsim.swing.Buttons;
import static ipsim.swing.Buttons.closeButton;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.IPAddressTextFieldUtility;
import ipsim.swing.SubnetMaskTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ProblemDialog
{
	public static JDialog createProblemDialog()
	{
		final JDialog dialog=createDialogWithEscapeKeyToClose(global.get().frame,"Edit Problem");

		dialog.setSize(400,220);
		ComponentUtility.centreOnParent(dialog, global.get().frame);

		final PercentConstraints constraints=PercentConstraintsUtility.newInstance(dialog.getContentPane());
		useAnyLayout(dialog.getContentPane(),0.5f,0.5f,constraints.getSizeCalculator(),typicalDefaultConstraint(throwRuntimeException));

		constraints.add(new JLabel("Network Number"),10,10,35,10,false,false);

		final IPAddressTextField ipAddressTextField=IPAddressTextFieldUtility.newInstance();

		@Nullable
		final Problem problem=getNetworkContext().network.problem;
		final IPAddress networkNumber=problem==null ? new IPAddress(0) : problem.netBlock.networkNumber;

		final NetMask subnetMask=problem==null ? getNetMask(0) : problem.netBlock.netMask;

		final int numberOfSubnets=problem==null ? MIN_SUBNETS : problem.numberOfSubnets;

		ipAddressTextField.setIPAddress(networkNumber);

		constraints.add(ipAddressTextField.textField,50,10,30,10,false,false);

		constraints.add(new JLabel("Network Mask",SwingConstants.RIGHT),10,30,35,10,false,false);

		final SubnetMaskTextField subnetMaskTextField=new SubnetMaskTextField();

		subnetMaskTextField.setNetMask(subnetMask);
		constraints.add(subnetMaskTextField,50,30,35,10,false,false);
		constraints.add(new JLabel("Number of Subnets"),10,50,35,10,false,false);

		final JTextField numberOfSubnetsTextField=new JTextField(2);

		numberOfSubnetsTextField.setText(String.valueOf(numberOfSubnets));
		constraints.add(numberOfSubnetsTextField,50,50,30,15,false,false);

		constraints.add(Buttons.newButton("OK", new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				final ProblemBuilder builder=new ProblemBuilder();

				final Either<ProblemBuilder.Stage2,String> either=builder.withSubnets(Integer.parseInt(numberOfSubnetsTextField.getText()));

				final Function<ProblemBuilder.Stage2,Runnable> doNothing=FunctionUtility.constant(Runnables.nothing);
				either.visit(doNothing,new Function<String,Runnable>()
				{
					@Override
                    @NotNull
					public Runnable run(@NotNull final String failure)
					{
						return new Runnable()
						{
							@Override
                            public void run()
							{
								final String message="The number of subnets must be a whole number between "+MIN_SUBNETS+" and "+Problem.MAX_SUBNETS;

								errors(message);
							}
						};
					}
				}).run();

				final Function<ProblemBuilder.Stage2,Boolean> falseConstant=FunctionUtility.constant(false);
				final Function<String,Boolean> trueConstant=FunctionUtility.constant(true);

				if (either.visit(falseConstant,trueConstant))
					return;

				final ProblemBuilder.Stage2 stage2=EitherUtility.unsafeLeft(either);

				final Either<Problem,String> either2=stage2.withNetBlock(new NetBlock(ipAddressTextField.getIPAddress(), subnetMaskTextField.getNetMask()));

				final Function<Problem,Runnable> doNothing2=FunctionUtility.constant(Runnables.nothing);

				either2.visit(doNothing2,new Function<String,Runnable>()
				{
					@Override
                    @NotNull
					public Runnable run(@NotNull final String failure)
					{
						return new Runnable()
						{
							@Override
                            public void run()
							{
								errors("Invalid netblock");
							}
						};
					}
				}).run();

				if (!EitherUtility.isLeft(either2))
					return;

				final Problem tmpProblem=EitherUtility.unsafeLeft(either2);
				if (!((tmpProblem.netBlock.networkNumber.rawValue&tmpProblem.netBlock.netMask.rawValue)==tmpProblem.netBlock.networkNumber.rawValue))
				{
					NetworkContext.errors("Invalid network number");

					return;
				}

				getNetworkContext().network.problem=Caster.asProblem(MaybeUtility.just(tmpProblem));

				dialog.setVisible(false);
				dialog.dispose();
			}
		}),15,85,20,15,false,false);

		constraints.add(closeButton("Close",dialog),75,85,20,15,false,false);

		return dialog;
	}
}