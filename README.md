# Immortals_ARSW_2019-2


## Part I 

### 1)

The consumption of the corresponding process is about 12.5% of the CPU time (see image below). This is called consumption because the process is using (consuming) a ressource, which is the CPU time. The responsible class is the class **Consumer** because, while the production is slow (because of the loop *while* checking if the queue is empty or not).

[](https://i.imgur.com/EdYFPJV.png)


### 2)

By using a solution with threads using *wait()* and *notify()* to avoid using the CPU to check if the queue is empty or full, we can observe that the process uses a lot less of CPU time (from 12.5% to almost 0%).


### 3)

By using a very small queue, a producter producing very fast (Only waiting 1ms between two productions) and a consumer consuming very fast (waiting 1000ms between consuming two elements), the application doesn't crash and the CPU usage stays low (almost 0% CPU time).


## Part 2

### 1)

*Nothing to say*

### 2)

For N immortals, the sum of the health points (which should be invariant) is : *int sum = N \* DEFAULT_IMMORTAL_HEALTH*

### 3)

Pause and check will check the health of each immortal, sum it and print the result. However, it is possible that, while this function checks the health of immortals, some of them fight, thus changing the total health point total at a time *t*. Moreover, the getters and setters the *health* of immortals are not synchronized. This can also result in data inconsistency

We can observe this happening because almost every time we click on "*Pause and check*", the value of **Health sum**, which should be invariant, changes.

### 4)

*see code*


### 5)

After pausing the threads, we can observe that the invariant is still unfulfilled. This is due to the fact that access to the health of Immortals isn't synchronized, resulting in data inconsistency.

For exemple, A can attack B, and in the meantime, C can attack A. While C attacks A, the health of A may not be updated in time for C to get the good value after A attacked B, resulting in data inconsistency


### 6)

To solve that problem, I'm using an ArrayList of locks. When A attacks B, A will request the locks corresponding of A and B in the arraylist (with two synchronizing blocks nested), and then will update the values.


### 7)

The program comes to an halt because we encounter an interlocking situation.

For exemple, with 3 immortals : 

A wants to fight B ==> requests locks for A and B

B wants to fight C ==> requests locks for B and C

C wants to fight A ==> requests locks for C and A

If the three immortals want to fight at the same time, an interlocking situation can appear.

### 8)

To solve this problem, I am using one ReetrantLock per immortal. Those locks are being stored in an ArrayList. When trying to fight another immortal, the immortal A will ask for his lock in the arrayList, then ask for the lock of the immortal B.
For this to be non blocking, I am using the *tryLock()* function nested in a while loop :

```
while(true){

            if(fightLocks.get(myIndex).tryLock()){
                boolean fightDone = false;
                if(fightLocks.get(nextFighterIndex).tryLock()){
                    fightDone = true;
                    if (i2.getHealth() > 0) {
                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        changeHealth(this.health.get() + defaultDamageValue);
        
        
                        updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                        } else {
                            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        
                        }
                    fightLocks.get(nextFighterIndex).unlock();
                }
                fightLocks.get(myIndex).unlock();
                if(fightDone)
                    break;
            }
        }
```

### 9)

Unfortunately, the invariant is being broken when using more immortals. I don't know why. I've investigated the fact that all threads have to be paused before calculating the sum (using booleans, using an AtomicCounter...etc), but this doesn't seem to work. 

### 10)

* 1) It seems that, even by removing immortals as they die, it is possible for immortals to try to fight them before they're getting removed. 

* 2) ```
            if(this.getHealth() == 0){
                immortalsPopulation.remove(myIndex);
                this.stop();
            } 
     ```

### 11)

Stop function has been implemented



