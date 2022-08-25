package su.knst.lokcyberbio.applications.utils;

import su.knst.lokcyberbio.misc.ApplicationTask;
import su.knst.lokcyberbio.misc.ApplicationTaskWithResult;
import su.knst.lokutils.applications.Application;
import su.knst.lokutils.render.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class SyncApplication extends Application {
    protected ReentrantLock lock = new ReentrantLock();

    protected HashMap<ApplicationTaskWithResult, CompletableFuture> tasksWithResult = new HashMap<>();
    protected ArrayList<ApplicationTask> tasks = new ArrayList<>();

    public SyncApplication(Window window) {
        super(window);
    }

    public SyncApplication() {
    }

    @Override
    public void updateEvent() {
        super.updateEvent();

        lock.lock();

        HashMap<ApplicationTaskWithResult, CompletableFuture> tasksWithResultCloned = (HashMap<ApplicationTaskWithResult, CompletableFuture>) tasksWithResult.clone();
        ArrayList<ApplicationTask> tasksCloned = (ArrayList<ApplicationTask>) tasks.clone();

        tasks.clear();
        tasksWithResult.clear();

        lock.unlock();

        for (Map.Entry<ApplicationTaskWithResult, CompletableFuture> e : tasksWithResultCloned.entrySet()) {
            e.getValue().complete(e.getKey().run());
        }

        for (ApplicationTask task : tasksCloned) {
            task.run();
        }
    }

    public <T> CompletableFuture<T> addTask(ApplicationTaskWithResult<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();

        lock.lock();

        tasksWithResult.put(task, future);

        lock.unlock();

        return future;
    }

    public int getTasksCount() {
        lock.lock();
        try {
            return tasks.size() + tasksWithResult.size();
        }finally {
            lock.unlock();
        }
    }

    public void addTask(ApplicationTask task) {
        lock.lock();

        tasks.add(task);

        lock.unlock();
    }
}
