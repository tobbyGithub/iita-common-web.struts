package org.iita.struts.action.admin;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.iita.struts.BaseAction;

import com.opensymphony.xwork2.Action;

/**
 * Action to display JRE status on memory usage and threads.
 * 
 * @author mobreza
 * 
 */
@SuppressWarnings("serial")
public class JavaStatusAction extends BaseAction {
	private static final Log LOG = LogFactory.getLog(JavaStatusAction.class);
	private List<? extends Thread> threads;
	private Map<Long, ThreadCpuUsage> cpuUsage;

	public int getAvailableProcessors() {
		return Runtime.getRuntime().availableProcessors();
	}

	public long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

	public long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Action method to run Garbage Collector
	 * 
	 * @return
	 */
	public String gc() {
		LOG.debug("Running Garbage Collector");
		Runtime.getRuntime().gc();
		LOG.debug("Done running Garbage collector");
		return "refresh";
	}

	public List<? extends Thread> getThreads() {
		return this.threads;
	}
	
	public Map<Long, ThreadCpuUsage> getCpuUsage() {
		LOG.debug("getCpuUsage");
		return cpuUsage;
	}

	public static List<? extends Thread> visit(ThreadGroup group, int level) {
		List<Thread> threadlist = null;
		// Get threads in `group'
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);
		// Enumerate each thread in `group'
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			if (threadlist == null)
				threadlist = new ArrayList<Thread>();
			threadlist.add(thread);
		}

		// Get thread subgroups of `group'
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);
		// Recursively visit each subgroup
		for (int i = 0; i < numGroups; i++) {
			if (threadlist == null)
				threadlist = new ArrayList<Thread>();
			List<? extends Thread> sub = visit(groups[i], level + 1);
			if (sub != null)
				threadlist.addAll(sub);
		}

		return threadlist;
	}

	@Override
	public String execute() {
		// Find the root thread group
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		// Visit each thread group
		this.threads = visit(root, 0);
		ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
		if (tmxb!=null && tmxb.isThreadCpuTimeSupported()) {
			LOG.info("JVM supports thread CPU time measurement.");
			this.cpuUsage=findCpuUsage(tmxb, threads);
		}
		return Action.SUCCESS;
	}

	/**
	 * @param tmxb
	 * @param threads2
	 * @return 
	 */
	private Map<Long, ThreadCpuUsage> findCpuUsage(ThreadMXBean tmxb, List<? extends Thread> threads) {
		Map<Long, ThreadCpuUsage> cpu=new Hashtable<Long, ThreadCpuUsage>(); 
		for (int i=0; i<threads.size(); i++) {
			Thread thread = threads.get(i);
			long threadId = thread.getId();
			ThreadCpuUsage cpuUsage = new ThreadCpuUsage();
			cpuUsage.cpuTime1=tmxb.getThreadCpuTime(threadId);
			cpuUsage.userTime1=tmxb.getThreadUserTime(threadId);
			cpuUsage.time1=System.currentTimeMillis();
			cpu.put(threadId, cpuUsage);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			LOG.warn("Interrupted from sleep: " + e.getMessage());
		}
		
		for (int i=0; i<threads.size(); i++) {
			Thread thread = threads.get(i);
			long threadId = thread.getId();
			ThreadCpuUsage cpuUsage = cpu.get(threadId);
			cpuUsage.cpuTime2=tmxb.getThreadCpuTime(threadId);
			cpuUsage.userTime2=tmxb.getThreadUserTime(threadId);
			cpuUsage.time2=System.currentTimeMillis();

			cpuUsage.cpuTime=cpuUsage.cpuTime2- cpuUsage.cpuTime1;
			cpuUsage.userTime=cpuUsage.userTime2- cpuUsage.userTime1;
			cpuUsage.time=cpuUsage.time2-cpuUsage.time1;
			
			if (cpuUsage.cpuTime>0) {
				cpuUsage.threadInfo = tmxb.getThreadInfo(threadId, 50);				
			}
			
			cpuUsage.utilization=(cpuUsage.cpuTime) / ((cpuUsage.time) * 1000000F);
		}
		
		return cpu;
	}
	
	private class ThreadCpuUsage {
		public ThreadInfo threadInfo;
		public long userTime1, userTime2, userTime;
		public long cpuTime1, cpuTime2, cpuTime;
		public long time1, time2, time;
		public double utilization;
	}
}
