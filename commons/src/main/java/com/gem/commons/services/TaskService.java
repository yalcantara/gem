package com.gem.commons.services;


import com.gem.commons.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.concurrent.*;


@Service
@ApplicationScope
@org.springframework.context.annotation.Lazy
public class TaskService {
	
	private static final int THREADS = 10;
	private static final int PRIORITY = Thread.NORM_PRIORITY - 2;
	
	
	private final Lazy<ExecutorService> srv;
	
	public TaskService(){
		
		srv = Lazy.wrap(()->{
			
			return new ThreadPoolExecutor(THREADS,
					THREADS,
					0l,
					TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(),
					new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							var t = new Thread(r);
							t.setPriority(PRIORITY);
							t.setDaemon(false);
							return t;
						}
					});
		});
	}
	
	
	public <V> Future<V> async(Callable<V> callable){
		return srv.get().submit(callable);
	}
}
