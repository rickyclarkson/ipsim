package ipsim.swing;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An abstract class to perform lengthy GUI-interacting tasks in a dedicated thread.
 * <p>
 * When writing a multi-threaded application using Swing, there are two constraints to keep in mind: (refer to <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html"> How to Use Threads </a> for more details):
 * <ul>
 * <li> Time-consuming tasks should not be run on the <i>Event Dispatch Thread</i>. Otherwise the application becomes unresponsive. </li>
 * <li> Swing components should be accessed on the <i>Event Dispatch Thread</i> only. </li>
 * </ul>
 * <p>
 * <p>
 * These constraints mean that a GUI application with time intensive computing needs at least two threads: 1) a thread to perform the lengthy task and 2) the <i>Event Dispatch Thread</i> (EDT) for all GUI-related activities. This involves inter-thread communication which can be tricky to implement.
 * <p>
 * {@code SwingWorker} is designed for situations where you need to have a long running task run in a background thread and provide updates to the UI either when done, or while processing. Subclasses of {@code SwingWorker} must implement the {@link #doInBackground} method to perform the background computation.
 * <p>
 * <b>Workflow</b>
 * <p>
 * There are three threads involved in the life cycle of a {@code SwingWorker} :
 * <ul>
 * <li>
 * <p>
 * <i>Current</i> thread: The {@link #execute} method is called on this thread. It schedules {@code SwingWorker} for the execution on a <i>worker</i> thread and returns immediately. One can wait for the {@code SwingWorker} to complete using the {@link #get get} methods.
 * <li>
 * <p>
 * <i>Worker</i> thread: The {@link #doInBackground} method is called on this thread. This is where all background activities should happen. To notify {@code PropertyChangeListeners} about bound properties changes use the {@link #firePropertyChange firePropertyChange} and {@link #getPropertyChangeSupport} methods. By default there are two bound properties available: {@code state} and {@code progress}.
 * <li>
 * <p>
 * <i>Event Dispatch Thread</i>: All Swing related activities occur on this thread. {@code SwingWorker} invokes the {@link #process process} and {@link #done} methods and notifies any {@code PropertyChangeListeners} on this thread.
 * </ul>
 * <p>
 * Often, the <i>Current</i> thread is the <i>Event Dispatch Thread</i>.
 * <p>
 * Before the {@code doInBackground} method is invoked on a <i>worker</i> thread, {@code SwingWorker} notifies any {@code PropertyChangeListeners} about the {@code state} property change to {@code StateValue.STARTED}. After the {@code doInBackground} method is finished the {@code done} method is executed. Then {@code SwingWorker} notifies any {@code PropertyChangeListeners} about the {@code state} property change to {@code StateValue.DONE}.
 * <p>
 * {@code SwingWorker} is only designed to be executed once. Executing a {@code SwingWorker} more than once will not result in invoking the {@code doInBackground} method twice.
 * <p>
 * <b>Sample Usage</b>
 * <p>
 * The following example illustrates the simplest use case. Some processing is done in the background and when done you update a Swing component.
 * <p>
 * Say we want to find the "Meaning of Life" and display the result in a {@code JLabel}.
 *
 * <pre>
 *    final JLabel label;
 *    class MeaningOfLifeFinder extends SwingWorker&lt;String, Object&gt; {
 *        {@code @Override}
 *        public String doInBackground() {
 *            return findTheMeaningOfLife();
 *        }
 *
 *        {@code @Override}
 *        protected void done() {
 *            try {
 *                label.setText(get());
 *            } catch (Exception ignore) {
 *            }
 *        }
 *    }
 *
 *    (new MeaningOfLifeFinder()).execute();
 * </pre>
 *
 * <p>
 * The next example is useful in situations where you wish to process data as it is ready on the <i>Event Dispatch Thread</i>.
 * <p>
 * Now we want to find the first N prime numbers and display the results in a {@code JTextArea}. While this is computing, we want to update our progress in a {@code JProgressBar}. Finally, we also want to print the prime numbers to {@code System.out}.
 *
 * <pre>
 *  class PrimeNumbersTask extends
 *          SwingWorker&lt;List&lt;Integer&gt;, Integer&gt; {
 *      PrimeNumbersTask(JTextArea textArea, int numbersToFind) {
 *          //initialize
 *      }
 *
 *      {@code @Override}
 *      public List&lt;Integer&gt; doInBackground() {
 *          while (! enough &amp;&amp; ! isCancelled()) {
 *                  number = nextPrimeNumber();
 *                  publish(number);
 *                  setProgress(100 * numbers.size() / numbersToFind);
 *              }
 *          }
 *          return numbers;
 *      }
 *
 *      {@code @Override}
 *      protected void process(Integer... chunks) {
 *          for (int number : chunks) {
 *              textArea.append(number + &quot;\n&quot;);
 *          }
 *      }
 *  }
 *
 *  JTextArea textArea = new JTextArea();
 *  final JProgressBar progressBar = new JProgressBar(0, 100);
 *  PrimeNumbersTask task = new PrimeNumbersTask(textArea, N);
 *  task.addPropertyChangeListener(
 *      new PropertyChangeListener() {
 *          public  void propertyChange(PropertyChangeEvent evt) {
 *              if (&quot;progress&quot;.equal s(evt.getPropertyName())) {
 *                  progressBar.setValue((Integer)evt.getNewValue());
 *              }
 *          }
 *      });
 *
 *  task.execute();
 *  System.out.println(task.get()); //prints all prime numbers we have got
 * </pre>
 *
 * <p>
 * Because {@code SwingWorker} implements {@code Runnable}, a {@code SwingWorker} can be submitted to an {@link Executor} for execution.
 * @author Igor Kushnirskiy
 * @version $Revision: 1.1 $ $Date: 2005/06/18 21:27:15 $
 * @param <T>
 *                the result type returned by this {@code SwingWorker's} {@code doInBackground} and {@code get} methods
 * @param <V>
 *                the type used for carrying out intermediate results by this {@code SwingWorker's} {@code publish} and {@code process} methods
 */
public abstract class SwingWorker<T,V> implements Future<T>,Runnable
{
	/**
	 * number of worker threads.
	 */
	private static final int MAX_WORKER_THREADS=10;

	/**
	 * current progress.
	 */
	private volatile int progress;

	/**
	 * current state.
	 */
	private volatile StateValue state;

	/**
	 * everything is run inside this FutureTask. Also it is used as a delegatee for the Future API.
	 */
	private final FutureTask<T> future;

	/**
	 * all propertyChangeSupport goes through this.
	 */
	private final SwingPropertyChangeSupport propertyChangeSupport;

	private static ExecutorService executorService=null;

	/**
	 * Values for the {@code state} bound property.
	 */
	public enum StateValue
	{
		/**
		 * Initial {@code SwingWorker} state.
		 */
		PENDING,
		/**
		 * {@code SwingWorker} is {@code STARTED} before invoking {@code doInBackground}.
		 */
		STARTED,

		/**
		 * {@code SwingWorker} is {@code DONE} after {@code doInBackground} method is finished.
		 */
		DONE
	}

	/**
	 * Constructs this {@code SwingWorker}.
	 */
	public SwingWorker()
	{
		final Callable<T> callable=new Callable<T>()
		{
			@Override
            public T call() throws Exception
			{
				setState(StateValue.STARTED);
				return doInBackground();
			}
		};

		future=new FutureTask<T>(callable)
		{
			@Override
			protected void done()
			{
				doneEDT();
				setState(StateValue.DONE);
			}
		};

		state=StateValue.PENDING;
		propertyChangeSupport=new SwingPropertyChangeSupport(this,true);
	}

	/**
	 * Computes a result, or throws an exception if unable to do so.
	 * <p>
	 * Note that this method is executed only once.
	 * <p>
	 * Note: this method is executed in a background thread.
	 * @return the computed result
	 * @throws Exception
	 *                 if unable to compute a result
	 */
	protected abstract T doInBackground() throws Exception;

	/**
	 * Sets this {@code Future} to the result of computation unless it has been cancelled.
	 */
	@Override
    public final void run()
	{
		future.run();
	}

	/**
	 * Executed on the <i>Event Dispatch Thread</i> after the {@code doInBackground} method is finished. The default implementation does nothing. Subclasses may override this method to perform completion actions on the <i>Event Dispatch Thread</i>. Note that you can query status inside the implementation of this method to determine the result of this task or whether this task has been cancelled.
	 * @see #doInBackground
	 * @see #isCancelled()
	 * @see #get
	 */
	protected void done()
	{
	}

	/**
	 * Returns the {@code progress} bound property.
	 * @return the progress bound property.
	 */
	public final int getProgress()
	{
		return progress;
	}

	/**
	 * Schedules this {@code SwingWorker} for execution on a <i>worker</i> thread. There are a number of <i>worker</i> threads available. In the event all <i>worker</i> threads are busy handling other {@code SwingWorkers} this {@code SwingWorker} is placed in a waiting queue.
	 * <p>
	 * Note: {@code SwingWorker} is only designed to be executed once. Executing a {@code SwingWorker} more than once will not result in invoking the {@code doInBackground} method twice.
	 */
	public final void execute()
	{
		getWorkersExecutorService().execute(this);
	}

	// Future methods START
	/**
	 * {@inheritDoc}
	 */
	@Override
    public final boolean cancel(final boolean mayInterruptIfRunning)
	{
		return future.cancel(mayInterruptIfRunning);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final boolean isCancelled()
	{
		return future.isCancelled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final boolean isDone()
	{
		return future.isDone();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Note: calling {@code get} on the <i>Event Dispatch Thread</i> blocks <i>all</i> events, including repaints, from being processed until this {@code SwingWorker} is complete.
	 * <p>
	 * When you want the {@code SwingWorker} to block on the <i>Event Dispatch Thread</i> we recommend that you use a <i>modal dialog</i>.
	 * <p>
	 * For example:
	 *
	 * <pre>
	 *  class SwingWorkerCompletionWaiter extends PropertyChangeListener {
	 *      private JDialog dialog;
	 *
	 *      public SwingWorkerCompletionWaiter(JDialog dialog) {
	 *          this.dialog = dialog;
	 *      }
	 *
	 *      public void propertyChange(PropertyChangeEvent event) {
	 *          if (&quot;state&quot;.equal s(event.getPropertyName())
	 *                  &amp;&amp; equalT(SwingWorker.StateValue.DONE).run(event.getNewValue())) {
	 *              dialog.setVisible(false);
	 *              dialog.dispose();
	 *          }
	 *      }
	 *  }
	 *  JDialog dialog = new JDialog(owner, true);
	 *  swingWorker.addPropertyChangeListener(
	 *      new SwingWorkerCompletionWaiter(dialog));
	 *  swingWorker.execute();
	 *  //the dialog will be visible until the SwingWorker is done
	 *  dialog.setVisible(true);
	 * </pre>
	 */
	@Override
    public final T get() throws InterruptedException,ExecutionException
	{
		return future.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Please refer to {@link #get} for more details.
	 */
	@Override
    public final T get(final long timeout,final TimeUnit unit) throws InterruptedException,ExecutionException,TimeoutException
	{
		return future.get(timeout,unit);
	}

	// Future methods END

	// PropertyChangeSupports methods START
	/**
	 * Adds a {@code PropertyChangeListener} to the listener list. The listener is registered for all properties. The same listener object may be added more than once, and will be called as many times as it is added. If {@code listener} is {@code null}, no exception is thrown and no action is taken.
	 * <p>
	 * Note: This is merely a convenience wrapper. All work is delegated to {@code PropertyChangeSupport} from {@link #getPropertyChangeSupport}.
	 * @param listener
	 *                the {@code PropertyChangeListener} to be added
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	/**
	 * Removes a {@code PropertyChangeListener} from the listener list. This removes a {@code PropertyChangeListener} that was registered for all properties. If {@code listener} was added more than once to the same event source, it will be notified one less time after being removed. If {@code listener} is {@code null}, or was never added, no exception is thrown and no action is taken.
	 * <p>
	 * Note: This is merely a convenience wrapper. All work is delegated to {@code PropertyChangeSupport} from {@link #getPropertyChangeSupport}.
	 * @param listener
	 *                the {@code PropertyChangeListener} to be removed
	 */
	public final void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	/**
	 * Reports a bound property update to any registered listeners. No event is fired if {@code old} and {@code new} are equal and non-null.
	 * <p>
	 * This {@code SwingWorker} will be the source for any generated events.
	 * <p>
	 * When called off the <i>Event Dispatch Thread</i> {@code PropertyChangeListeners} are notified asynchronously on the <i>Event Dispatch Thread</i>.
	 * <p>
	 * Note: This is merely a convenience wrapper. All work is delegated to {@code PropertyChangeSupport} from {@link #getPropertyChangeSupport}.
	 * @param propertyName
	 *                the programmatic name of the property that was changed
	 * @param oldValue
	 *                the old value of the property
	 * @param newValue
	 *                the new value of the property
	 */
	public final void firePropertyChange(final String propertyName,final Object oldValue,final Object newValue)
	{
		getPropertyChangeSupport().firePropertyChange(propertyName,oldValue,newValue);
	}

	/**
	 * Returns the {@code PropertyChangeSupport} for this {@code SwingWorker}. This method is used when flexible access to bound properties support is needed.
	 * <p>
	 * This {@code SwingWorker} will be the source for any generated events.
	 * <p>
	 * Note: The returned {@code PropertyChangeSupport} notifies any {@code PropertyChangeListener}s asynchronously on the <i>Event Dispatch Thread</i> in the event that {@code firePropertyChange} or {@code fireIndexedPropertyChange} are called off the <i>Event Dispatch Thread</i>.
	 * @return {@code PropertyChangeSupport} for this {@code SwingWorker}
	 */
	public final PropertyChangeSupport getPropertyChangeSupport()
	{
		return propertyChangeSupport;
	}

	// PropertyChangeSupports methods END

	/**
	 * Returns the {@code SwingWorker} state bound property.
	 * @return the current state
	 */
	public final StateValue getState()
	{
		/*
		 * DONE is a special case to keep getState and isDone is sync
		 */
		if (isDone())
			return StateValue.DONE;
		return state;
	}

	/**
	 * Sets this {@code SwingWorker} state bound property.
	 * @param the
	 *                state state to set
	 */
	void setState(final StateValue state)
	{
		final StateValue old=this.state;
		this.state=state;
		firePropertyChange("state",old,state);
	}

	/**
	 * Invokes {@code done} on the EDT.
	 */
	void doneEDT()
	{
		final Runnable doDone=new Runnable()
		{
			@Override
            public void run()
			{
				done();
			}
		};
		if (SwingUtilities.isEventDispatchThread())
			doDone.run();
		else
			SwingUtilities.invokeLater(doDone);
	}

	/**
	 * returns workersExecutorService. returns the service stored in the appContext or creates it if necessary. If the last one it triggers autoShutdown thread to get started.
	 * @return ExecutorService for the {@code SwingWorkers}
	 * @see #startAutoShutdownThread
	 */
	private static synchronized ExecutorService getWorkersExecutorService()
	{
		if (executorService==null)
		{
			// this creates non-daemon threads.
			final ThreadFactory threadFactory=new ThreadFactory()
			{
				final ThreadFactory defaultFactory=Executors.defaultThreadFactory();

				@Override
                public Thread newThread(final Runnable r)
				{
					final Thread thread=defaultFactory.newThread(r);
					thread.setName("SwingWorker-"+thread.getName());
					return thread;
				}
			};

			/*
			 * We want a to have no more than MAX_WORKER_THREADS running threads. We want a worker thread to wait no longer than 1 second for new tasks before terminating.
			 */
			executorService=new ThreadPoolExecutor(0, MAX_WORKER_THREADS, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory)
			{

				private final ReentrantLock pauseLock=new ReentrantLock();
				private final Condition unpaused=pauseLock.newCondition();
				private boolean isPaused=false;
				private final ReentrantLock executeLock=new ReentrantLock();

				@Override
				public void execute(final Runnable command)
				{
					/*
					 * ThreadPoolExecutor first tries to run task in a corePool. If all threads are busy it tries to add task to the waiting queue. If it fails it run task in maximumPool. We want corePool to be 0 and maximumPool to be MAX_WORKER_THREADS We need to change the order of the execution. First try corePool then try maximumPool pool and only then store to the waiting queue. We can not do that because we would need access to the private methods. Instead we enlarge corePool to MAX_WORKER_THREADS before the execution and shrink it back to 0 after. It does pretty much what we need. While we changing the corePoolSize we need to stop running worker threads from accepting new tasks.
					 */

					// we need atomicity for the execute method.
					executeLock.lock();
					try
					{

						pauseLock.lock();
						try
						{
							isPaused=true;
						}
						finally
						{
							pauseLock.unlock();
						}

						setCorePoolSize(MAX_WORKER_THREADS);
						super.execute(command);
						setCorePoolSize(0);

						pauseLock.lock();
						try
						{
							isPaused=false;
							unpaused.signalAll();
						}
						finally
						{
							pauseLock.unlock();
						}
					}
					finally
					{
						executeLock.unlock();
					}
				}

				@Override
				protected void afterExecute(final Runnable r, final Throwable t)
				{
					super.afterExecute(r, t);
					pauseLock.lock();
					try
					{
						while (isPaused)
							unpaused.await();
					}
					catch (final InterruptedException ignore)
					{

					}
					finally
					{
						pauseLock.unlock();
					}
				}
			};
		}
		return executorService;
	}
}