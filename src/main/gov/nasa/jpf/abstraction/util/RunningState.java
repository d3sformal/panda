package gov.nasa.jpf.abstraction.util;

/**
 * Distinguished states of the target execution
 */
public class RunningState implements Cloneable {
	private enum State {
		JUST_BEGAN_RUNNING, // ~ static init of the target or main method invoked
		RUNNING, // ~ we entered the target part of the execution already and we still did not leave it 
		NOT_RUNNING, // ~ we are out of the target scope
		JUST_CEASED_RUNNING // ~ we just left (return)
	}
	
	private int entered;
	private State state;
	
	protected RunningState(int entered, State state) {
		this.entered = entered;
		this.state = state;
	}
	
	public RunningState() {
		this(0, State.NOT_RUNNING);
	}
	
	public void enter() {
		switch (state) {
		case NOT_RUNNING:
		case JUST_CEASED_RUNNING:
			state = State.JUST_BEGAN_RUNNING;
			break;
		default:
			state = State.RUNNING;
		}
		
		++entered;
	}
	
	public void leave() {
		--entered;
		
		switch (state) {
		case JUST_BEGAN_RUNNING:
		case RUNNING:
			if (entered == 0) {
				state = State.JUST_CEASED_RUNNING;
			}
			break;
		default:
			state = State.NOT_RUNNING;
		}
	}
	
	public boolean isRunning() {
		switch (state) {
		case JUST_BEGAN_RUNNING:
		case RUNNING:
			return true;
		default:
			return false;
		}
	}
	
	public boolean hasBeenRunning() {
		switch (state) {
		case NOT_RUNNING:
			return false;
		default:
			return true;
		}
	}
	
	public boolean hasNotBeenRunning() {
		switch (state) {
		case RUNNING:
			return false;
		default:
			return true;
		}
	}
	
	public boolean isNotRunning() {
		return !isRunning();
	}
	
	@Override
	public RunningState clone() {
		return new RunningState(entered, state);
	}

	public void touch() {
		switch (state) {
		case JUST_BEGAN_RUNNING:
			state = State.RUNNING;
			break;
		case JUST_CEASED_RUNNING:
			state = State.NOT_RUNNING;
			break;
		}
	}
}
