<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Object Pool Design Pattern README</title>
</head>
<body>

  <h1>Object Pool Design Pattern</h1>

  <p>
    The <strong>Object Pool Pattern</strong> is a <em>creational design pattern</em> that aims to reuse and manage a set of pre-initialized objects rather than creating and destroying them on demand. 
    It is particularly useful when the creation of objects is expensive and a large number of short-lived or frequently used objects is required.
  </p>

  <hr>

<h2>Key Intent</h2>
  <ul>
    <li>Manage and reuse objects that are costly to create.</li>
    <li>Maintain a pool of available objects and provide a mechanism for requesting and returning them.</li>
  </ul>

  <hr>

<h2>When to Use</h2>
  <ul>
    <li>
      You have objects that are expensive to create (e.g., database connections, threads, socket connections).
    </li>
    <li>
      You create these objects frequently and their usage duration is relatively short, so many objects might be created and destroyed in a short time span.
    </li>
    <li>
      You want to limit the maximum number of objects in active use to manage resource constraints.
    </li>
  </ul>

  <hr>

<h2>Participants</h2>
  <ul>
    <li>
      <strong>Object Pool</strong>: Manages a set of reusable objects. Provides methods to acquire (check out) and release (return) objects.
    </li>
    <li>
      <strong>Reusable Object</strong>: The actual object that is expensive to create and is stored and managed by the pool.
    </li>
    <li>
      <strong>Client</strong>: Requests objects from the pool and returns them after use.
    </li>
  </ul>

  <hr>

<h2>Structure</h2>
  <p>A simplistic UML-like diagram of the Object Pool pattern could be as follows:</p>
  <pre>
  +----------------+         +---------------+
  |   Client       |<------->|  ObjectPool   |
  +----------------+   uses  +---------------+
            ^                |+ acquire()    |
            |                |+ release()    |
            |                +---------------+
            |                    
            v
     +-----------------+
     | ReusableObject |
     +-----------------+
     | - doWork()     |
     +-----------------+
  </pre>

  <hr>

<h2>Example (Java)</h2>
  <p>Below is a simple example demonstrating the Object Pool pattern:</p>

  <pre><code>// Reusable object class
public class ExpensiveResource {
    private String id;

    public ExpensiveResource(String id) {
        this.id = id;
        System.out.println("ExpensiveResource created: " + id);
        // Simulate expensive creation
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }

    public void doWork() {
        System.out.println("ExpensiveResource " + id + " is doing work.");
    }

    public String getId() {
        return id;
    }
}

// Object Pool class
import java.util.Queue;
import java.util.LinkedList;

public class ResourcePool {
    private Queue<ExpensiveResource> available = new LinkedList<>();
    private int counter = 1;
    private final int maxPoolSize = 3;  // for example

    // Acquire an ExpensiveResource from the pool
    public synchronized ExpensiveResource acquire() {
        if (available.isEmpty()) {
            if (counter <= maxPoolSize) {
                // Create a new resource if pool is not at max capacity
                ExpensiveResource newResource = new ExpensiveResource("R" + counter++);
                return newResource;
            } else {
                // Wait until a resource is released
                while (available.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return available.poll();
    }

    // Release an ExpensiveResource back to the pool
    public synchronized void release(ExpensiveResource resource) {
        available.offer(resource);
        // Notify waiting threads that a resource is available
        notifyAll();
    }
}

// Client code
public class Client {
    public static void main(String[] args) {
        ResourcePool pool = new ResourcePool();

        // Acquire a resource from the pool
        ExpensiveResource res1 = pool.acquire();
        res1.doWork();

        ExpensiveResource res2 = pool.acquire();
        res2.doWork();

        // Release a resource back to the pool
        pool.release(res1);

        // Acquire again (could be the same resource we just released)
        ExpensiveResource res3 = pool.acquire();
        res3.doWork();

        // Release remaining resources
        pool.release(res2);
        pool.release(res3);
    }
}
  </code></pre>

  <p>
    In this example:
    <ul>
      <li><strong>ExpensiveResource</strong>: The costly object to create.</li>
      <li><strong>ResourcePool</strong>: Manages a collection of <code>ExpensiveResource</code> objects and controls acquisition and release.</li>
      <li><strong>Client</strong>: Obtains resources from the pool, uses them, and releases them back to the pool.</li>
    </ul>
  </p>

  <hr>

<h2>Benefits</h2>
  <ul>
    <li><strong>Performance Improvement:</strong> Reduces the overhead of creating new objects by reusing instances.</li>
    <li><strong>Resource Management:</strong> You can limit the number of objects in use, ensuring you don’t exceed resource constraints.</li>
    <li><strong>Scalability:</strong> Helps handle burst traffic or demand spikes by having readily available objects.</li>
  </ul>

  <hr>

<h2>Drawbacks</h2>
  <ul>
    <li><strong>Complexity:</strong> Implementing a thread-safe pool with waiting, notification, and proper synchronization can be complex.</li>
    <li><strong>Memory Usage:</strong> If objects remain in the pool unused for long periods, they occupy memory unnecessarily.</li>
    <li><strong>Suitability:</strong> Only beneficial if object creation is significantly expensive or usage is frequent enough to warrant reuse.</li>
  </ul>

  <hr>

<h2>Related Patterns</h2>
  <ul>
    <li><strong>Singleton</strong>: Sometimes the pool itself is implemented as a singleton for global access.</li>
    <li><strong>Flyweight</strong>: Also focuses on reducing object creation cost, but through shared intrinsic state rather than pooling.</li>
    <li><strong>Factory Method</strong>: Often used within the pool to create objects if none are available.</li>
  </ul>

  <hr>

<h2>Conclusion</h2>
  <p>
    The Object Pool pattern is valuable when object creation is expensive or you have limited system resources and need to recycle objects. 
    By managing a reusable set of pre-allocated objects, you can drastically reduce creation overhead and effectively manage resource usage. 
    It introduces additional complexity in implementation, particularly regarding thread-safety and lifecycle management, 
    but it can significantly enhance performance in the right circumstances.
  </p>

</body>
</html>
