# Immortals_ARSW_2019-2


## Part I 

### 1)

The consumption of the corresponding process is about 12.5% of the CPU time (see image below). This is called consumption because the process is using (consuming) a ressource, which is the CPU time. The responsible class is the class **Consumer** because, while the production is slow (because of the loop *while* checking if the queue is empty or not).

[](https://i.imgur.com/EdYFPJV.png)


### 2)

By using a solution with threads using *wait()* and *notify()* to avoid using the CPU to check if the queue is empty or full, we can observe that the process uses a lot less of CPU time (from 12.5% to almost 0%).

*add image*

### 3)

By using a very small queue, a producter producing very fast (Only waiting 1ms between two productions) and a consumer consuming very fast (waiting 1000ms between consuming two elements), the application doesn't crash and the CPU usage stays low (almost 0% CPU time).

*add image*

