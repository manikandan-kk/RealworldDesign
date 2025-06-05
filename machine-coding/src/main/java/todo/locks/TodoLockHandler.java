package todo.locks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TodoLockHandler {

    private final Map<String, ReadWriteLock> todoLocks;

    public TodoLockHandler() {
        this.todoLocks = new ConcurrentHashMap<>();
    }

    public void addLock(String todoId) {
        todoLocks.put(todoId, new ReentrantReadWriteLock());
    }

    public boolean acquireReadLock(String todoId) throws InterruptedException {
        ReadWriteLock lock = get(todoId);
        return lock.readLock().tryLock(2, SECONDS);
    }

    public void releaseReadLock(String todoId) {
        ReadWriteLock lock = get(todoId);
        lock.readLock().unlock();
    }

    public boolean acquireWriteLock(String todoId) throws InterruptedException {
        ReadWriteLock lock = get(todoId);
        return lock.writeLock().tryLock(2, SECONDS);
    }

    public void releaseWriteLock(String todoId) {
        ReadWriteLock lock = get(todoId);
        lock.writeLock().unlock();
    }

    private ReadWriteLock get(String todoId) {
        if (!todoLocks.containsKey(todoId)) {
            throw new IllegalStateException(format("Lock does not exist for todoId: %s", todoId));
        }
        return todoLocks.get(todoId);
    }
}
