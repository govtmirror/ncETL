package gov.usgs.cida.ncetl.sis;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

public class LatchTrigger implements Trigger {
	
	private AtomicBoolean go = new AtomicBoolean(false);
	
	@Override
	public synchronized Date nextExecutionTime(TriggerContext triggerContext) {
		if (go.getAndSet(false)) {
			return new Date();
		}
		return null;
	}

	public void enable() {
		setGo(true);
	}
	
	public void disable() {
		setGo(false);
	}
	
	public void setGo(boolean v) {
		go.set(v);
	}
}
